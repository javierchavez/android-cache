package cachelibrary;


import android.content.Context;
import android.net.http.HttpResponseCache;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;
import cachelibrary.io.Serializer;
import cachelibrary.model.Cachable;
import cachelibrary.net.CachedFetch;
import cachelibrary.net.Fetch;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Class that adds abstraction to {@link android.net.http.HttpResponseCache};
 * This will install the cache dir and makes retrieval cache and serialization
 * simple.
 */
public class Cache
{
  private static final String TAG = "Cache";
  private static Cache instance;
  private final Fetch fetcher;

  /**
   * This is private to enforce Singleton pattern.
   * @param context application/activity context.
   */
  private Cache (Context context)
  {
    this(context, new CachedFetch());
  }

  private Cache (Context context, Fetch fetcher)
  {
    this.fetcher = fetcher;
    if(HttpResponseCache.getInstalled() == null)
    {
      try
      {
        File httpCacheDir = new File(context.getCacheDir(), "http");
        long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        HttpResponseCache.install(httpCacheDir, httpCacheSize);

      }
      catch(IOException e)
      {
        if (BuildConfig.DEBUG)
        {
          Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
      }
    }

  }

  /**
   * This is to be called FIRST in the main activity. This allows a singleton
   * to be shared throughout the application.
   *
   * @param context Application context
   */
  public static void init (Context context)
  {
    if (instance == null)
    {
      instance = new Cache(context);
    }
  }

  /**
   * This is to be called FIRST in the main activity. This allows a singleton
   * to be shared throughout the application.
   *
   * @param context Application context
   * @param fetcher provide custom fetcher
   */
  public static void init (Context context, CachedFetch fetcher)
  {
    if (instance == null)
    {
      instance = new Cache(context, fetcher);
    }
  }

  /**
   * Get instance of this class.
   *
   * @return Cache instance
   */
  public static Cache getInstance ()
  {
    if (instance == null)
    {
      throw new RuntimeException("Must call init first. This class needs an application context.");
    }

    return instance;
  }

  /**
   * Get the raw data from cachable object
   *
   * @param cachable the object that you want data from.
   * @param <C> Cachable class
   * @return byte array of data
   */
  public final <C extends Cachable> byte[] getRawData(C cachable)
  {
    return fetcher.get(cachable);
  }

  /**
   * Get the data and convert, using its serializer, into POJO's
   *
   * @param cachable the object that you want data from.
   * @param <T> the class that represents the data your (POJO). The serializer will return this.
   * @param <C> class that implements {@link Cachable}
   * @return result of calling getData() method on {@link Serializer}.
   */
  public final <T, C extends Cachable> T getData(C cachable)
  {
    byte[] fileData = fetcher.get(cachable);
    Serializer serializer = cachable.getSerializer();
    serializer.decode(fileData);
    return serializer.getData();
  }

  /**
   * Batch get and serialize cachables.
   *
   * @param cachables List of cachables to be retrieved.
   * @param <T> the class that represents the data your (POJO). The serializer will return this.
   * @param <V> class that implements {@link Cachable}
   * @return list of {@link Pair}'s where the first is T the POJO returned from serializer and
   * second is the corresponding {@link Cachable} implementing class.
   */
  @SuppressWarnings("unchecked")
  public final <T, V extends Cachable> List<Pair<T, V>> getData(List<V> cachables)
  {
    List<Pair<T, V>> accumulator = new ArrayList<>();
    for (V cachable : cachables)
    {
      Object obj = getData(cachable);

      if (obj instanceof List<?>)
      {
        for (T object : ((List<T>) obj))
        {
          accumulator.add(Pair.create(object, cachable));
        }
      }
      else
      {
        T data = (T) obj;
        accumulator.add(Pair.create(data, cachable));
      }
    }

    return accumulator;
  }

  /**
   * Save to disk
   *
   * @param filename to save the data as
   * @param data actual data to be stored.
   */
  public void save (Context context, String filename, String data)
  {
    save(context, filename, data.getBytes(), System.currentTimeMillis());
  }


  /**
   * Save to disk
   *
   * @param filename to save the data as
   * @param data actual data to be stored.
   * @param lastModified epoch time in milliseconds, last modified time.
   */
  public void save (Context context, String filename, byte[] data, long lastModified)
  {
    File file = context.getFileStreamPath(filename);
    boolean didSave = file.setLastModified(lastModified);
    if (didSave)
    {
      if (BuildConfig.DEBUG)
      {
        Log.i(TAG, "setting last-modified: " + new Date(lastModified));
      }
    }
    else
    {
      if (BuildConfig.DEBUG)
      {
        Log.i(TAG, "last-modified not saved");
      }
    }

    try
    {
      FileOutputStream out = new FileOutputStream(file);
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      byte[] b = new byte[512];
      int count;
      while ((count = in.read(b)) >= 0)
      {
        out.write(b, 0, count);
      }
      out.flush();
      out.close();
      in.close();
      b = null;

    }
    catch(IOException e)
    {
      if (BuildConfig.DEBUG)
      {
        Log.e(TAG, "IO error trying to save cache", e);
      }
    }
  }


  /**
   * Open a file
   *
   * @param filename that contains data.
   * @return byte array containing data from cached file.
   */
  public byte[] retrieve (Context context, String filename)
  {

    File file = context.getFileStreamPath(filename);
    byte[] bFile = new byte[(int) file.length()];
    FileInputStream fileInputStream = null;
    try
    {
      //convert file into array of bytes
      fileInputStream = new FileInputStream(file);
      fileInputStream.read(bFile);
      fileInputStream.close();
    }
    catch (Exception e)
    {
      if (BuildConfig.DEBUG)
      {
        Log.e(TAG, "IO Error reading cache", e);
      }
    }

    return bFile;
  }

  /**
   * Get the last modified time of a file.
   *
   * @param filename file name to get
   * @return epoch milli.
   */
  public long getLastModified (Context context, String filename)
  {
    File file = context.getFileStreamPath(filename);
    return file.lastModified();
  }

}

package cachelibrary.io;


import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.Date;


/**
 * Class that manages the IO of data on the device.
 */
public class DataCache
{
  private static final String TAG = "Cache";
  private static DataCache instance;
  private final Context mContext;

  /**
   * This is private to enforce Singleton pattern.
   * @param context application/activity context.
   */
  private DataCache (Context context)
  {
    this.mContext = context;
  }

  /**
   * This is to be called FIRST in the main activity. This allows a singleton
   * to be shared throughout the application.
   *
   * @param context Main activity context
   */
  public static void init (Context context)
  {
    if (instance == null)
    {
      instance = new DataCache(context);
    }
  }

  /**
   * Get instance of this class.
   *
   * @return Data instance
   */
  public static DataCache getInstance ()
  {
    if (instance == null)
    {
      throw new RuntimeException("Must call init first");
    }

    return instance;
  }

  /**
   * Save to disk
   *
   * @param filename to save the data as
   * @param data actual data to be stored.
   */
  public void save (String filename, String data)
  {
    save(filename, data.getBytes(), System.currentTimeMillis());
  }


  /**
   * Save to disk
   *
   * @param filename to save the data as
   * @param data actual data to be stored.
   * @param lastModified epoch time in milliseconds, last modified time.
   */
  public void save (String filename, byte[] data, long lastModified)
  {
    File file = mContext.getFileStreamPath(filename);
    boolean didSave = file.setLastModified(lastModified);
    if (didSave)
    {
      Log.i(TAG, "setting last-modified: " + new Date(lastModified));
    }
    else
    {
      Log.i(TAG, "last-modified not saved");
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
      Log.e(TAG, "IO error trying to save cache", e);
    }
  }


  /**
   * Open a file
   *
   * @param filename that contains data.
   * @return byte array containing data from cached file.
   */
  public byte[] retrieve (String filename)
  {

    File file = mContext.getFileStreamPath(filename);
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
      Log.e(TAG, "IO Error reading cache", e);
    }

    return bFile;
  }

  /**
   * Get the last modified time of a file.
   *
   * @param filename file name to get
   * @return epoch milli.
   */
  public long getLastModified (String filename)
  {
    File file = mContext.getFileStreamPath(filename);
    return file.lastModified();
  }

}

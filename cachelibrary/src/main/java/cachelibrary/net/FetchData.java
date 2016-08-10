package cachelibrary.net;

import android.util.Log;
import cachelibrary.model.Cachable;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;


/**
 * Simple worker class that fetches data and caches the result to disk.
 *
 * This only fetches data. It will need to be called from within a Task to prevent holding UI.
 */
public class FetchData
{

  private static final String TAG = "Fetch";
  private static final String CACHE_HEADER = "Cache-Control";

  public byte[] get (Cachable cachable)
  {
    return get(cachable, true);
  }

  public byte[] get (Cachable cachable, boolean useCache)
  {
    return send("GET", cachable, useCache);
  }

  public Map<String, List<String>> head (Cachable cachable)
  {
    throw new RuntimeException("Not Yet Implemented");
    // return send("HEAD", cachable, false);
  }

  @SuppressWarnings("unchecked")
  private <T> T send(String method, Cachable cachable, boolean cache)
  {
    try
    {
      // HttpResponseCache responseCache = HttpResponseCache.getInstalled();
      // Log.i(TAG, "Hit count: " + responseCache.getHitCount());
      // Log.i(TAG, "HTTP req count: " + responseCache.getRequestCount());
      HttpURLConnection connection = setUpConnection(cachable, cache);
      InputStream inputStream;
      try
      {
        inputStream = connection.getInputStream();
      }
      catch (FileNotFoundException e)
      {
        connection.disconnect();
        
        connection = setUpConnection(cachable, false);
        connection.setRequestMethod(method);
        connection.connect();

        // int len = connection.getContentLength();
        // int _rCode = connection.getResponseCode();

        // Log.d(TAG, "HTTP " + connection.getRequestMethod() + " " + connection.toString());
        inputStream = connection.getInputStream();
      }

      BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      int data;
      while ((data = bufferedInputStream.read()) != -1)
      {
        output.write(data);
      }

      bufferedInputStream.close();
      connection.disconnect();

      return (T) output.toByteArray();

    }
    catch(IOException e)
    {
      Log.e(TAG, "IO " + cachable, e);
    }

    return null;
  }


  private final static HttpURLConnection setUpConnection(Cachable cachable, boolean cache) throws IOException
  {
    HttpURLConnection connection = (HttpURLConnection) cachable.url().openConnection();
    connection.setUseCaches(true);


    if (!cache || cachable.expiration() == 0)
    {
      connection.addRequestProperty(CACHE_HEADER, "no-cache");
    }
    else
    {
      connection.addRequestProperty(CACHE_HEADER, "max-stale=" + cachable.expiration());
      connection.addRequestProperty(CACHE_HEADER, "only-if-cached");
    }
    return connection;
  }
}

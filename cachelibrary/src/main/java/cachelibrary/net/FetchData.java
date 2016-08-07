package cachelibrary.net;


import android.app.Activity;
import android.util.Log;
import cachelibrary.io.DataCache;
import cachelibrary.model.Cachable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

  public FetchData (Activity activity)
  {
  }


  public Void get (Cachable dataset)
  {
    return send("GET", dataset, true);
  }

  public byte[] getData (Cachable dataset)
  {
    return send("GET", dataset, false);
  }

  public Map<String, List<String>> head (Cachable dataset)
  {
    return send("HEAD", dataset, false);
  }

  @SuppressWarnings("unchecked")
  private <T> T send(String method, Cachable dataset, boolean cache)
  {
    try
    {

      HttpURLConnection connection = (HttpURLConnection) dataset.url().openConnection();
      // connection.setRequestProperty("If-None-Match", "\"49b3214-903c6-534d333effcf7\"");
      connection.setRequestMethod(method);
      connection.connect();

      int len = connection.getContentLength();
      int _rCode = connection.getResponseCode();
      Log.d(TAG, "HTTP " + connection.getRequestMethod() + " " + connection.toString());

      if (_rCode == -1 || _rCode == HttpURLConnection.HTTP_NOT_MODIFIED)
      {
        Log.d(TAG, "HTTP " + String.valueOf(_rCode));
        return null;
      }
      if (method.equals("HEAD"))
      {
        return (T) connection.getHeaderFields();
      }

      BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      int data;
      while ((data = inputStream.read()) != -1)
      {
        output.write(data);
      }

      inputStream.close();

      if (cache)
      {
        Log.d(TAG, "Saving cache for " + dataset.fileName());
        DataCache.getInstance().save(dataset.fileName(), output.toByteArray(), connection.getLastModified());
      }
      else
      {
        return (T) output.toByteArray();
      }

    }
    catch(IOException e)
    {
      Log.e(TAG, "IO " + dataset, e);
    }

    return null;
  }
}

package cachelibrary.model;

import cachelibrary.io.DataCache;
import cachelibrary.io.Serializer;
import cachelibrary.net.FetchData;
import cachelibrary.util.StringUtil;
import cachelibrary.util.Tuple;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Cachable
{
  long MONTH = 2592000000L;
  long DAY = 86400000L;
  long HOUR = 3600000L;
  long ALWAYS = 10000L;


  URL url ();

  long expiration ();

  Serializer getSerializer ();

  String fileName ();


  final class Util
  {
    public static final boolean shouldDownload (Cachable mParam, FetchData fetcher)
    {
      long lm = DataCache.getInstance().getLastModified(mParam.fileName());
      boolean download = false;

      if (lm == 0)
      {
        // Log.i(TAG, "Could not locate last-modified for: " + mParam);
        return true;
      }
      else if ((mParam.expiration() + lm) < System.currentTimeMillis())
      {
        // Log.i(TAG, "Getting header");
        Map<String, List<String>> header = fetcher.head(mParam);

        if (header == null)
        {
          // Log.d(TAG, "Got a null header");
          return false;
        }

        long datasetLM = DataCache.getInstance().getLastModified(mParam.fileName());

        Date datasetDate = new Date(datasetLM);

        List lmList = header.get("Last-Modified");
        Date d;
        if (lmList != null && lmList.size() > 0)
        {
          d = StringUtil.parseDate((String) lmList.get(0));
          // Log.i(TAG, "Got a date " + d);
        }
        else
        {
          return true;
        }

        if (d == null)
        {
          download = true;
        }
        else if (d.after(datasetDate))
        {
          // Log.i(TAG, "Last modified is newer than phone");
          download = true;
        }
        else if (d.equals(datasetDate))
        {
          // Log.i(TAG, "Last modified is the same");
          download = false;
        }
        else if (d.before(datasetDate))
        {
          // Log.i(TAG, "Last modified is before");
          download = true;
        }

      }
      else
      {
        // Log.i(TAG, "Not yet expired");
        download = false;
      }

      return download;

    }

    public static final void getData(List<? extends Cachable> cachables, FetchData fetcher, List<Tuple> accumulator)
    {
      for (Cachable cachable : cachables)
      {
        boolean download = shouldDownload(cachable, fetcher);

        if (download)
        {
          // Log.i(TAG, "Downloading " + dataset);
          fetcher.get(cachable);
        }
        // Log.d(TAG, "Retrieving cache: " + dataset);

        byte[] fileData = DataCache.getInstance().retrieve(cachable.fileName());
        Serializer serializer = cachable.getSerializer();
        serializer.decode(fileData);
        ArrayList<?> _data = serializer.getData();
        for (Object object : _data)
        {
          accumulator.add(new Tuple<>(object, cachable));
        }
      }
    }

  }
}

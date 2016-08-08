package cachelibrary.util;


import cachelibrary.io.DataCache;
import cachelibrary.io.Serializer;
import cachelibrary.model.Cachable;
import cachelibrary.net.FetchData;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CacheUtil
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


  public static final <T> T getData(Cachable cachable, FetchData fetcher)
  {
      boolean download = shouldDownload(cachable, fetcher);

      if (download)
      {
        fetcher.get(cachable);
      }
      byte[] fileData = DataCache.getInstance().retrieve(cachable.fileName());
      Serializer serializer = cachable.getSerializer();
      serializer.decode(fileData);
      return serializer.getData();

  }


  public static final void getData(List<? extends Cachable> cachables, FetchData fetcher, List<Tuple> accumulator)
  {
    for (Cachable cachable : cachables)
    {
      Object obj = getData(cachable, fetcher);

      if (obj instanceof List)
      {
        for (Object object : ((List) obj))
        {
          accumulator.add(new Tuple<>(object, cachable));
        }
      }
      else
      {
        accumulator.add(new Tuple<>(obj, cachable));
      }
    }
  }

}

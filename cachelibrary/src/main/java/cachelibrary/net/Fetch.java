package cachelibrary.net;


import android.net.http.HttpResponseCache;

import cachelibrary.model.Cachable;

public abstract class Fetch
{

  private HttpResponseCache cache;

  public abstract byte[] get (Cachable cachable);

  public abstract byte[] get (Cachable cachable, boolean useCache);

  public abstract byte[] head (Cachable cachable);

  public void setCache(HttpResponseCache cache)
  {
    this.cache = cache;
  }

  public HttpResponseCache getCache()
  {
    return cache;
  }
}

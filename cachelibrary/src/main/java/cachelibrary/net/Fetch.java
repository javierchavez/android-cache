package cachelibrary.net;


import cachelibrary.model.Cachable;

public interface Fetch
{

  byte[] get (Cachable cachable);

  byte[] get (Cachable cachable, boolean useCache);
}

package cachelibrary.net;


import java.net.MalformedURLException;
import java.net.URL;

import cachelibrary.io.Serializer;
import cachelibrary.model.Cachable;

public enum  DummyData implements Cachable
{

  JsonData
  {
    @Override
    public URL url()
    {
      URL url = null;
      try
      {
        url = new URL("http://datastore.unm.edu/locations/abqbuildings.json");
      }
      catch (MalformedURLException e)
      {
        e.printStackTrace();
      }
      return url;
    }

    @Override
    public long expiration()
    {
      return Cachable.DAY;
    }

    @Override
    public Serializer getSerializer()
    {
      return new DummySerializer();
    }
  },

  JsonDataBad
  {
    @Override
    public URL url()
    {
      URL url = null;
      try
      {
        url = new URL("http://datastore.unm.edu/locations/abqbuildings");
      }
      catch (MalformedURLException e)
      {
        e.printStackTrace();
      }
      return url;
    }

    @Override
    public long expiration()
    {
      return Cachable.DAY;
    }

    @Override
    public Serializer getSerializer()
    {
      return new DummySerializer();
    }
  }

}

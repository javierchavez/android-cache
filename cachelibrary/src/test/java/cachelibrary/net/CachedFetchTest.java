package cachelibrary.net;

import android.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import cachelibrary.io.Serializer;
import cachelibrary.model.Cachable;

@RunWith(MockitoJUnitRunner.class)
public class CachedFetchTest
{
  private CachedFetch fetch;

  @Before
  public void setup()
  {
    fetch = new CachedFetch();
  }

  @Test
  public void get() throws Exception
  {
    Cachable cachable = DummyData.JsonData;

    byte[] data = fetch.get(cachable);
    Serializer serializer = cachable.getSerializer();
    serializer.decode(data);
    ArrayList<Pair> serializedData = serializer.getData();
    Assert.assertNotNull(serializedData);


    data = fetch.get(DummyData.JsonDataBad);
    Assert.assertNotNull(data);
    Assert.assertArrayEquals(new byte[0], data);
    Assert.assertEquals(0, data.length);

    serializer = cachable.getSerializer();
    serializer.decode(data);
    serializedData = serializer.getData();
    Assert.assertArrayEquals(new Object[]{}, serializedData.toArray());

    fetch.get(DummyData.JsonData, true);

  }

  @Test
  public void head() throws Exception
  {
    byte[] data = fetch.head(DummyData.JsonData);
    Assert.assertNotNull(data);
  }

}
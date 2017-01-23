package cachelibrary.net;

import android.content.Context;
import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mockito.Mock;

import java.util.ArrayList;

import cachelibrary.io.Serializer;



public class DummySerializer implements Serializer
{
  private ArrayList points;

  @Override
  public <T> T getData ()
  {
    return (T) points;
  }


  @Override
  public void decode (byte[] data)
  {
    points = new ArrayList<>();

    JSONTokener decoder = new JSONTokener(new String(data));

    try
    {
      JSONArray object = (JSONArray) decoder.nextValue();
      for (int i = 0; i < object.length(); i++)
      {
        JSONObject obj = (JSONObject) object.get(i);
        points.add(Pair.create(obj.getDouble("latitude"), obj.getDouble("longitude")));
      }
    }
    catch(JSONException e)
    {
      //e.printStackTrace();
    }
  }
}

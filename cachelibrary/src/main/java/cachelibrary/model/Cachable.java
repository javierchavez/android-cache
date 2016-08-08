package cachelibrary.model;

import cachelibrary.io.Serializer;
import java.net.URL;

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
}

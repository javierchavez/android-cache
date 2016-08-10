package cachelibrary.model;

import cachelibrary.io.Serializer;
import java.net.URL;


/**
 * Data received from server that you want cached should implement this class.
 */
public interface Cachable
{
  long MONTH  = 60 * 60 * 24 * 28;
  long DAY    = 60 * 60 * 24;
  long HOUR   = 60 * 60;
  long ALWAYS = 0L;


  /**
   * Create a url and return it
   *
   * @return Url to where the data can be gotten.
   */
  URL url ();

  /**
   * How long shall the data be cached/how long to wait to check until new
   * data is available.
   *
   * @return seconds until next check.
   */
  long expiration ();

  /**
   * Get the Serializer responsible for converting data(bytes) into POJO
   *
   * @return The serializer that will handle this data
   */
  Serializer getSerializer ();

}

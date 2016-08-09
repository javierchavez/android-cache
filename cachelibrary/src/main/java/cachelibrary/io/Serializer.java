package cachelibrary.io;


/**
 * Class that is used to convert data that is downloaded into usable POJO's
 */
public interface Serializer
{

  /**
   * Get the data that was decoded.
   *
   * @param <T> Class to be returned this is typically an Array of your
   * POJO.
   *
   * @return data that was created by first calling decode
   */
  <T> T getData ();


  /**
   * Method used to turn data into usable objects. AKA a processing method, and
   * should be called from background thread.
   *
   * @param data array of bytes received from somewhere. This can be a Bitmap,
   * JSON, plain-text, its up to you to convert it and get data ready for getData() to return.
   */
  void decode (byte[] data);
}

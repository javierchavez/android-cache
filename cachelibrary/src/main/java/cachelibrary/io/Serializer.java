package cachelibrary.io;




public interface Serializer
{
  <T> T getData ();

  void decode (byte[] data);
}

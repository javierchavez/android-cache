package cachelibrary.util;


import java.io.Serializable;

public final class Tuple<T, V> implements Serializable
{
  public T t;
  public V v;

  public Tuple (T t, V v)
  {
    this.t = t;
    this.v = v;
  }
}

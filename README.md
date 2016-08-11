# Cache Library for Android

[![Release](https://jitpack.io/v/javierchavez/android-cache.svg?style=flat-square)](https://jitpack.io/#javierchavez/android-cache)

[![Build Status](https://travis-ci.org/javierchavez/android-cache.svg?branch=master)](https://travis-ci.org/javierchavez/android-cache)

This is a wrapper that eases the use of Google's `HttpResponseCache`.

## Usage

## Components

* Cache - Class that you call to handle getting data.
* Serializer - Interface that your serializers will implement. Once you get data 
  from the you need to turn that data into a java object, this class is where that happens.
* Cachable - Interface that your class will implement. You have urls that are endpoints to 
  data you want to consume, you will have a class for every URL, or one `enum` that implements `Cachable`.
  
##### Serializer

This is where you want to convert server response into real java objects. This is an example 
where the server is returning JSON array of user objects.

```java
public class UsersSerializer implements Serializer
{
  private ArrayList users;

  @Override
  public <T> T getData ()
  {
    return (T) users;
  }

  @Override
  public void decode (byte[] data)
  {
    users = new ArrayList<>();
    JSONTokener decoder = new JSONTokener(new String(data));
    try
    {
      JSONArray array = (JSONArray) decoder.nextValue();

      for (int i = 0; i < array.length(); i++)
      {
        User user = new User();
        JSONObject obj = (JSONObject) array.get(i);
        user.setName(obj.getString("name"));
        user.setEmail(obj.getString("email"));
        users.add(user);
      }
    }
    catch(JSONException e)
    {
      
    }
  }
}
```

##### Cachable

This is an example of how you can define your endpoints in a organized fashion.

```java
public enum EndPoints implements Cachable
{
    USERS("http://example.com/users")
    {
        @Override
        public long expiration ()
        {
         return Cachable.ALWAYS;
        }
        
        @Override
        public Serializer getSerializer ()
        {
         return new UserSerializer();
        } 
    };
        
    public URL url ()
    {
        try
        {
          return new URL(url); 
        }
        catch(MalformedURLException e)
        {
          e.printStackTrace();
        }
        
        return null;
    }
  
    private final String url;
    
    public Dataset url (String s)
    {
      this.url = s;
      return this;
    }
}
```
  
##### Cache

First initialize, by installing a cache. This should really only be called once.

```java
Cache.init(getApplicationContext());
```

Now you're ready to use it!
The return of getData() is generic

```java
EndPoints mCachableData = EndPoints.Users;
List<User> data = Cache.getInstance().getData(mCachableData);
```



##### Author
Name: **Javier C**

Email: **javier@javierc.io**

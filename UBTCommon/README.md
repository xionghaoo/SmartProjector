# 网络模块
@(Manual)[HTTP|SOCKET|]

**UBTCommon**的网络库（com.ubtedu.base.net），包含以下功能模块：

- **rxretrofit**
基于RxJava、Retrofit和OkHttp的封装，提供HTTP访问功能，入口主类：RxClient；
- **socket**
基于TCP/UDP的socket封装，即分为TCP和UDP两个模块；TCP入口主类包括：TcpClient和TcpServer，UDP入口主类只有客户端：UdpClient
- **image**
基于Glide的封装，提供图片加载功能，主类：GlideLoader。

- **cache**
缓存，包括磁盘缓存，内存缓存，SP缓存，基于LRU算法。
-**database**
 数据库，基于Greendao数据库，放在UBTNet的原因是download需要用到数据库。

-------------------

[TOC]

## rxretrofit
@(Package)[com.ubtedu.base.net.rxretrofit|]

>此小节是对rxretrofit模块的使用说明，读者需要对RxJava和Retroft有基本的了解。RxJava可以参阅这篇博文：[给 Android 开发者的 RxJava 详解](http://gank.io/post/560e15be2dca930e00da1083)，Retrofit直接看[Retrofit官网](http://square.github.io/retrofit/)

### API分类
rxretrofit模块的主类是RxClient，该类提供了几乎所有的Http网络访问功能接口，包括get，put，post等。

**RxClient的API从功能封装上分为两种:**

- **XXX**
如：get,post,call...，泛型T表示接口返回的数据实体，整个ReponseBody会转换为T。
- **apiXXX**
如：apiGet,apiPost,apiCall...，所有的apiXXX把ResponseBody包装成ApiResult，再返回ApiResult的data字段：


``` java
public class ApiResult<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public ApiResult setCode(int code) {
        this.code = code;
        return this;
    }
    public String getMsg() {
        return msg;
    }

    public ApiResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }
    public T getData() {
        return data;
    }

    public ApiResult setData(T data) {
        this.data = data;
        return this;
    }
...
```
泛型T表示ApiResult中的Data字段。

**RxClient的API从使用上分为两种:**

- **方法形参中带ApiCallback，返回值void**
该使用方式基于callback，和普通的使用方式一样；可以通过返回的Subscription取消订阅。
- **方法形参中不带ApiCallback，返回值Observable**
该使用方式基于RxJava，方便使用返回的Observable对数据进行处理；需要在参数中传进数据实体的Class对象或Type，需要主动订阅。

如何产生数据实体的Type？以List<User>为例：
``` java
Type typeOfTnew TypeToken<List<User>>(){}.getType();
```

**特别说明的API，call和apiCall:**

``` java
public <T> Observable<T> call(Observable<T> observable) {
        return observable.compose(new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        });
    }

public <T> Observable<T> apiCall(Observable<ApiResult<T>> observable) {
       return observable.compose(this.<T>apiTransformer());

    }
```
call和apiCall都是接收外部的Observable，主要用于自定义API，使用方法请看下一节。

### API使用
#### 初始化RxClient
使用RxClient.Builder创建RxClient：
``` java
RxClient client = new RxClient.Builder(this)
                .baseUrl("http://10.10.6.96:3000/")//该baseUrl只用于自定义API
                .connectTimeout(2000)
                .build();
```
#### 使用API

**XXX类型API的使用：**
``` java

client.get("http://10.10.6.96:3000/users/", new HashMap<String, String>(), new ApiCallback<ApiResult<List<User>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d("");
            }
            @Override
            public void onError(ApiException e) {
                LogUtil.d("");
            }
            @Override
            public void onComplete() {
                LogUtil.d("");
            }
            @Override
            public void onNext(ApiResult<List<User>> listApiResult) {
                LogUtil.d("");
            }
        });
```
注意：T = ApiResult<List<User>

**apiXXX类型API的使用：**
``` java
client.apiGet("http://10.10.6.96:3000/users/", new HashMap<String, String>(), new ApiCallback<List<User>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d("");
            }

            @Override
            public void onError(ApiException e) {
                LogUtil.d("");
            }

            @Override
            public void onComplete() {
                LogUtil.d("");
            }
            
            @Override
            public void onNext(List<User> userApiCallback) {
                LogUtil.d("");
            }
        });
```
注意：T = List<User>

**call的使用：**

``` java
public interface DemoApiService {
    @GET("user")
    Observable<ApiResult<User>> getUser();
}
...

DemoApiService api = client.create(DemoApiService.class);
        client.call(api.getUser())
                .subscribe(new ApiCallbackSubscriber<ApiResult<User>>(this, new ApiCallback<ApiResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.d("");
                    }

                    @Override
                    public void onError(ApiException e) {
                        LogUtil.d("");
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.d("");
                    }

                    @Override
                    public void onNext(ApiResult<User> userApiResult) {
                        LogUtil.d("");
                    }
                }));
```
注意：T = ApiResult<User>,也可以换成其它数据实体，只要和服务器返回的json数据对应就行。

**apiCall的使用：**

``` java
public interface DemoApiService {
    @GET("user")
    Observable<ApiResult<User>> getUser();
}
...

 DemoApiService api = client.create(DemoApiService.class);
        client.apiCall(api.getUser())
                .subscribe(new ApiCallbackSubscriber<User>(this, new ApiCallback<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.d("");
                    }

                    @Override
                    public void onError(ApiException e) {
                        LogUtil.d("");
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.d("");
                    }

                    @Override
                    public void onNext(User user) {
                        LogUtil.d("");
                    }
                }));
```
注意：T = User,即只返回ApiResult的data字段。

#### 自定义API
RxClient除了那些基本的API方法之外，还可以定义自己的API方法，最后通过RxClient的call或apiCall方法执行。


**创建自定义API Service：**
``` java
public interface DemoApiService {
    /**类型一：
     * T = ApiModel<User>
     * http://10.10.6.96:3000/user/
     * 可以通过RxClient.call方法执行，整个Response Body被包装成ApiResult<User>返回
     *ApiModel<User>需要跟服务器返回一致
     * @return
     */
    @GET("user")
    Observable<ApiModel<User>> getUserWithResult();

    /**类型二：
     * T = User
     * http://10.10.6.96:3000/user/
     * 可以通过RxClient.apiCall方法执行，整个Response Body被包装成ApiResult<User>,
     * 再返回data字段,即直接返回User实体。
     * @return
     */
    @GET("user")//http://10.10.6.96:3000/user/
    Observable<ApiResult<User>> getUser();
}
```
- **类型一**：返回整个数据实体，通过RxClient的call方法执行。
- **类型一**：返回ApiResult的data字段，通过RxClient的apiCall方法执行

## Socket
@(Package)[com.ubtedu.base.net.socket]

基于TCP/UDP的socket封装，即分为TCP和UDP两个模块；TCP入口主类包括：TcpClient和TcpServer，UDP入口主类只有客户端：UdpClient
### TCP
TCP的socket封装包括Client端和Server端，主要的类有：


- **TcpClient**：TCP Socket的client
- **TcpServer**：TCP Socket的server
-  **TargetInfo**：Socket连接目标
-  **TcpMsg**：Socket消息
- **TcpConnConfig**：TCP Socket的client的config类
- **TcpServerConfig**：TCP Socket的server的config类
#### Client的使用

TcpClient是一个轻量级的对socket封装客户端，内部自动处理连接，可以直接发送String消息，也可以发送byte数据。

##### 创建TcpClient
```java
TcpConnConfig config = new TcpConnConfig.Builder()
                .setConnTimeout(2000)
                .setCharsetName("UTF-8")
                .setIsReconnect(false)
                .create();
TargetInfo target = new TargetInfo("10.10.6.51",2001);
TcpClient client = TcpClient.getTcpClient(target,config);
client.addTcpClientListener(new TcpClientListener(){
            @Override
            public void onConnected(TcpClient client) {
                
            }

            @Override
            public void onSended(TcpClient client, TcpMsg tcpMsg) {

            }

            @Override
            public void onDisconnected(TcpClient client, String msg, Exception e) {

            }

            @Override
            public void onReceive(TcpClient client, TcpMsg tcpMsg) {

            }

            @Override
            public void onValidationFail(TcpClient client, TcpMsg tcpMsg) {

            }
        });
```

##### 发送消息
```java
TcpMsg msg = new TcpMsg("Hello",target, TcpMsg.MsgType.Send);
client.sendMsg(msg);
```
##### 主动断开连接
```java
client.disconnect();
```

#### Server的使用
TODOs

### UDP
TCP的socket封装的客户端，主要的类有：

- **UdpClient**：Udp Socket的client
- **UdpClientConfig**：Udp Socket的client的config类
-  **TargetInfo**：连接目标
-  **UdpMsg**：udp消息
#### Client的使用

UdpClient的使用方式和TcpClient相似。

##### 创建UdpClient
```java
UdpClientConfig udpConfig = new UdpClientConfig.Builder()
                .localPort(2001)
                .charsetName("UTF-8")
                .receiveTimeout(2000)
                .create();
UdpClient udpClient = UdpClient.getUdpClient();
udpClient.config(udpConfig);
udpClient.addUdpClientListener(new UdpClientListener() {
            @Override
            public void onStarted(UdpClient XUdp) {
                
            }

            @Override
            public void onStoped(UdpClient XUdp) {

            }

            @Override
            public void onSended(UdpClient XUdp, UdpMsg udpMsg) {

            }

            @Override
            public void onReceive(UdpClient client, UdpMsg udpMsg) {

            }

            @Override
            public void onError(UdpClient client, String msg, Exception e) {

            }
        });
```

##### 发送消息
```java
TargetInfo target = new TargetInfo("10.10.6.51",2001);
UdpMsg message = new UdpMsg("Hello",target, TcpMsg.MsgType.Send);
        //第二个参数指定是否需要回复，true表示client会开启接收线程等待回复
        udpClient.sendMsg(message,true);
```
##### 开启接收服务
```java
udpClient.startUdpServer();
```

##### 关闭接收服务
```java
udpClient.stopUdpServer();
```

# 2 数据库操作
**方案：使用第三方库LitePal**

## 快速配置LitePal
### 1 配置Gradle
``` groovy
dependencies {
    compile 'org.litepal.android:core:1.5.1'
}
```

### 2 配置litepal.xml
请在assets目录中添加litepal.xml，如果已存在则无需添加，按需求修改。

包含内容
``` xml
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <dbname value="Alpha1X" />

    <version value="1" />

    <list>
        <mapping class="com.ubtedu.alpha1x.DBEntries.AlphaInfo" />
    </list>
    
    <!--
        Define where the .db file should be. "internal" means the .db file
        will be stored in the database folder of internal storage which no
        one can access. "external" means the .db file will be stored in the
        path to the directory on the primary external storage device where
        the application can place persistent files it owns which everyone
        can access. "internal" will act as default.
        For example:
        <storage value="external" />
    -->
    
</litepal>
```
 * **dbname** 数据库名称
 * **version** 数据库版本号
 * **list** 数据库列名所映射的Java类
 * **storage** storage：存储位置. 两个有效参数**internal**、 **external** 

### 3 配置LitePalApplication
LitePal无需传Context，只需要在**AndroidManifest.xml**中作如下配置
``` xml
<manifest>
    <application
        android:name="org.litepal.LitePalApplication"
        ...
    >
        ...
    </application>
</manifest>
```
如果已有自己的application类继承了LitePalApplication，或者继承了其他类
``` xml
<manifest>
    <application
        android:name="com.example.MyOwnApplication"
        ...
    >
        ...
    </application>
</manifest>
```
在**onCreate()**调用LitePal.initialize(this);
```java
public class MyOwnApplication extends AnotherApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
    
}
```

### 4 建表
#### 4.1 建数据库、映射类
设计一个类
``` java
public class Album extends DataSupport {
	
    @Column(unique = true, defaultValue = "unknown")
    private String name;
	
    private float price;
	
    private byte[] cover;
	
    private List<Song> songs = new ArrayList<Song>();

    // generated getters and setters.
    ...
}
```
然后在litepal.xml中添加映射关系
``` xml
<list>
    <mapping class="org.litepal.litepalsample.model.Album" />
</list>
```
创建数据库
``` java
SQLiteDatabase db = LitePal.getDatabase();
```

#### 4.2 更新表
```java
public class Album extends DataSupport {
	
    @Column(unique = true, defaultValue = "unknown")
    private String name;
	
    @Column(ignore = true)
    private float price;
	
    private byte[] cover;
	
    private Date releaseDate;
	
    private List<Song> songs = new ArrayList<Song>();

    // generated getters and setters.
   
}
```
#### 4.3 更新版本号
```xml
<!--   
    <version value="1" />
-->
<version value="2" />
```

#### 4.4 保存数据到数据库
只需创建数据库列对应的映射类对象（该类继承DataSupport），调用其save()方法。
``` java
Album album = new Album();
album.setName("album");
album.setPrice(10.99f);
album.setCover(getCoverImageBytes());
album.save();
```

#### 4.5 更新数据
查找ID
``` java
Album albumToUpdate = DataSupport.find(Album.class, 1);
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.save();
```
指定ID
``` java
Album albumToUpdate = new Album();
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.update(id);
```
根据条件更新
``` java
Album albumToUpdate = new Album();
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.updateAll("name = ?", "album");
```

#### 4.6 删除数据
删除指定ID的数据
``` java
DataSupport.delete(Song.class, id);
```
删除更多记录
``` java
DataSupport.deleteAll(Song.class, "duration > ?" , "350");
```

#### 4.7 查询数据
查询指定ID的数据
``` java
Song song = DataSupport.find(Song.class, id);
```
查询某映射类对应的表所有数据
``` java
List<Song> allSongs = DataSupport.findAll(Song.class);
```
根据条件查询数据
``` java
List<Song> songs = DataSupport.where("name like ?", "song%").order("duration").find(Song.class);
```

### 5 异步操作
当需要保存或查询大量数据时，会耗时过长，不便在主线程操作，需要异步操作。

#### 5.1 在后台进行操作，设置监听等待结果。使用**findAllAsync()**代替**findAll()**
```java
DataSupport.findAllAsync(Song.class).listen(new FindMultiCallback() {
    @Override
    public <T> void onFinish(List<T> t) {
        List<Song> allSongs = (List<Song>) t;
    }
});
```

#### 5.2 异步保存数据。使用**saveAsync()** 代替 **save()**
```java
Album album = new Album();
album.setName("album");
album.setPrice(10.99f);
album.setCover(getCoverImageBytes());
album.saveAsync().listen(new SaveCallback() {
    @Override
    public void onFinish(boolean success) {

    }
});
```

### 6 创建多个数据库
#### 6.1 创建数据库demo2.db，同时创建表Singer、Album、Song。
```java
LitePalDB litePalDB = new LitePalDB("demo2", 1);
litePalDB.addClassName(Singer.class.getName());
litePalDB.addClassName(Album.class.getName());
litePalDB.addClassName(Song.class.getName());
LitePal.use(litePalDB);
```

#### 6.2 如果想创建多个数据库，但数据类型和litepal.xml配置一样
```java
LitePalDB litePalDB = LitePalDB.fromDefault("newdb");
LitePal.use(litePalDB);
```

#### 6.3 切回默认数据库
```java
LitePal.useDefault();
```

#### 6.4 删除指定数据库
```java
LitePal.deleteDatabase("newdb");
```

### 7 代码混淆注意项
```proguard
-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}
```

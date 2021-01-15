package com.ubtedu.base.net.rxretrofit;

import android.content.Context;
import android.util.Log;

import com.ubtedu.base.common.FrameworkConfig;
import com.ubtedu.base.net.rxretrofit.api.ApiService;
import com.ubtedu.base.net.rxretrofit.callback.ApiCallback;
import com.ubtedu.base.net.rxretrofit.convert.GsonConverterFactory;
import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.core.ApiCookie;
import com.ubtedu.base.net.rxretrofit.func.ApiErrFunc;
import com.ubtedu.base.net.rxretrofit.func.ApiFunc;
import com.ubtedu.base.net.rxretrofit.func.ApiResultFunc;
import com.ubtedu.base.net.rxretrofit.func.ApiResultHandleFunc;
import com.ubtedu.base.net.rxretrofit.interceptor.GzipRequestInterceptor;
import com.ubtedu.base.net.rxretrofit.interceptor.HeadersInterceptor;
import com.ubtedu.base.net.rxretrofit.interceptor.OfflineCacheInterceptor;
import com.ubtedu.base.net.rxretrofit.interceptor.OnlineCacheInterceptor;
import com.ubtedu.base.net.rxretrofit.interceptor.PostCacheInterceptor;
import com.ubtedu.base.net.rxretrofit.mode.ApiHost;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.mode.CacheMode;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxCallbackSubscriber;
import com.ubtedu.base.utils.ClassUtil;
import com.ubtedu.base.utils.SSLUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.FieldMap;

/**
 * @Description: 网络操作入口
 * @author: qinicy
 * @date: 17/5/7 19:38
 */
public class RxClient {
    private Context context;
    private ApiService apiService;
    private Retrofit retrofit;
    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpBuilder;
    private ApiCache apiCache;
    private ApiCache.Builder apiCacheBuilder;
    private CacheMode cacheMode;

    private RxClient() {
    }


    /**
     * 可传入自定义的接口服务
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    /**
     * 由外部设置被观察者
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> Observable<T> call(Observable<T> observable) {
        return observable.compose(new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        });
    }

    /**
     * 普通Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> get(String url, Map<String, String> maps, Class<T> clazz) {
        return apiService.get(url, maps).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 普通Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> get(String url, Map<String, String> maps, Type clazz) {
        return apiService.get(url, maps).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 普通Get方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void get(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.get(url, maps, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 带缓存Get方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResult<T>> cacheGet(final String url, final Map<String, String> maps, Class<T> clazz) {
        return this.get(url, maps, clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 带缓存Get方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，无需订阅，只需配置Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void cacheGet(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.cacheGet(url, maps, ClassUtil.getTClass(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 普通POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(final String url, final Map<String, String> parameters, Class<T> clazz) {
        return apiService.post(url, parameters).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 普通POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(final String url, final Map<String, String> parameters, Type type) {
        return apiService.post(url, parameters).compose(this.<T>norTransformer(type));
    }

    /**
     * 普通POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void post(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.post(url, maps, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 带缓存POST方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResult<T>> cachePost(final String url, final Map<String, String> maps, Class<T> clazz) {
        return this.post(url, maps, clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 带缓存POST方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，无需订阅，只需配置Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void cachePost(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.cachePost(url, maps, ClassUtil.getTClass(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 提交表单方式请求，需传入实体类
     *
     * @param url
     * @param fields
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, Class<T> clazz) {
        return apiService.postForm(url, fields).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 提交表单方式请求，需传入实体类
     *
     * @param url
     * @param fields
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, Type type) {
        return apiService.postForm(url, fields).compose(this.<T>norTransformer(type));
    }

    /**
     * 提交表单方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param fields
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, ApiCallback<T> callback) {
        this.form(url, fields, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 提交Body方式请求，需传入实体类
     *
     * @param url
     * @param body
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> body(final String url, final Object body, Class<T> clazz) {
        return apiService.postBody(url, body).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 提交Body方式请求，需传入实体类
     *
     * @param url
     * @param body
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> body(final String url, final Object body, Type type) {
        return apiService.postBody(url, body).compose(this.<T>norTransformer(type));
    }

    /**
     * 提交Body方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param body
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void body(final String url, final Object body, ApiCallback<T> callback) {
        this.body(url, body, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 删除信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> delete(final String url, final Map<String, String> maps, Class<T> clazz) {
        return apiService.delete(url, maps).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 删除信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> delete(final String url, final Map<String, String> maps, Type type) {
        return apiService.delete(url, maps).compose(this.<T>norTransformer(type));
    }

    /**
     * 删除信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void delete(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.delete(url, maps, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 修改信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> put(final String url, final Map<String, String> maps, Class<T> clazz) {
        return apiService.put(url, maps).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 修改信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> put(final String url, final Map<String, String> maps, Type type) {
        return apiService.put(url, maps).compose(this.<T>norTransformer(type));
    }

    /**
     * 修改信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void put(String url, Map<String, String> maps, ApiCallback<T> callback) {
        this.put(url, maps, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 上传图片，需传入请求body和实体类
     *
     * @param url
     * @param requestBody
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, RequestBody requestBody, Class<T> clazz) {
        return apiService.uploadImage(url, requestBody).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 上传图片，需传入图片文件和实体类
     *
     * @param url
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, File file, Class<T> clazz) {
        return apiService.uploadImage(url, RequestBody.create(okhttp3.MediaType.parse("image/jpg; " + "charset=utf-8"), file)).compose
                (this.<T>norTransformer(clazz));
    }

    /**
     * 上传文件
     *
     * @param url
     * @param requestBody
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFile(String url, RequestBody requestBody, MultipartBody.Part file, Class<T> clazz) {
        return apiService.uploadFile(url, requestBody, file).compose(this.<T>norTransformer(clazz));
    }

    /**
     * 上传多文件
     *
     * @param url
     * @param files
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFlies(String url, Map<String, RequestBody> files, Class<T> clazz) {
        return apiService.uploadFiles(url, files).compose(this.<T>norTransformer(clazz));
    }

    /*=============================以下处理服务器返回对象为ApiResult<T>形式的请求=================================*/

    /**
     * 由外部设置被观察者
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> void apiCall(Observable<ApiResult<T>> observable, ApiCallback<T> callback) {
        observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new ApiResultHandleFunc<T>())
                .onErrorResumeNext(new ApiErrFunc<T>())
                .subscribe(new RxCallbackSubscriber<T>(callback));

    }

    /**
     * 由外部设置被观察者,需要自己订阅
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiCall(Observable<ApiResult<T>> observable) {
        return observable.compose(this.<T>apiTransformer());

    }

    /**
     * 返回ApiResult<T>的Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiGet(final String url, final Map<String, String> maps, Class<T> clazz) {
        return apiService.get(url, maps).map(new ApiResultFunc<T>(clazz)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiGet(final String url, final Map<String, String> maps, Type type) {
        return apiService.get(url, maps).map(new ApiResultFunc<T>(type)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的Get方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void apiGet(final String url, final Map<String, String> maps, ApiCallback<T> callback) {
        this.apiGet(url, maps, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 返回ApiResult<T>并带缓存的Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResult<T>> apiCacheGet(final String url, final Map<String, String> maps, Class<T> clazz) {
        return this.apiGet(url, maps, clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 返回ApiResult<T>并带缓存的Get方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void apiCacheGet(final String url, final Map<String, String> maps, ApiCallback<T> callback) {
        this.apiCacheGet(url, maps, ClassUtil.getTClass(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 返回ApiResult<T>的POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiPost(final String url, final Map<String, String> parameters, Class<T> clazz) {
        return apiService.post(url, parameters).map(new ApiResultFunc<T>(clazz)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiPost(final String url, final Map<String, String> parameters, Type type) {
        return apiService.post(url, parameters).map(new ApiResultFunc<T>(type)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param parameters
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void apiPost(final String url, final Map<String, String> parameters, ApiCallback<T> callback) {
        this.apiPost(url, parameters, ClassUtil.typeOfT(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 返回ApiResult<T>并带缓存的POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<CacheResult<T>> apiCachePost(final String url, final Map<String, String> parameters, Class<T> clazz) {
        return this.apiPost(url, parameters, clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 返回ApiResult<T>并带缓存的POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param parameters
     * @param callback
     * @param <T>
     * @return
     */
    public <T> void apiCachePost(final String url, final Map<String, String> parameters, ApiCallback<T> callback) {
        this.apiCachePost(url, parameters, ClassUtil.getTClass(callback)).subscribe(new RxCallbackSubscriber(callback));
    }

    /**
     * 清楚所有缓存
     *
     * @return
     */
    public void clearCache() {
        apiCache.clear();
    }

    /**
     * 清除对应Key的缓存
     *
     * @param key
     */
    public void removeCache(String key) {
        apiCache.remove(key);
    }

    /**
     * 创建ViseApi.Builder
     *
     * @param context
     * @return
     */
    public Builder newBuilder(Context context) {
        return new Builder(context);
    }

    //    private <T> Observable.Transformer<ResponseBody, T> norTransformer(final Class<T> type) {
//        return new Observable.Transformer<ResponseBody, T>() {
//            @Override
//            public Observable<T> call(Observable<ResponseBody> apiResultObservable) {
//                return apiResultObservable
//                        .subscribeOn(Schedulers.io())
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers
//                                .mainThread())
//                        .map(new ApiResultFunc<T>(type))
//                        .flatMap(new ApiResultHandleFunc<T>())
//                        .onErrorResumeNext(new ApiErrFunc<T>());
//            }
//        };
//    }
    private <T> ObservableTransformer<ResponseBody, T> norTransformer(final Type clazz) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiFunc<T>(clazz))
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }

        };
    }

    private <T> ObservableTransformer<ApiResult<T>, T> apiTransformer() {
        return new ObservableTransformer<ApiResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ApiResult<T>> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new ApiResultHandleFunc<T>())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }

    private static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    private void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    private void setRetrofitBuilder(Retrofit.Builder retrofitBuilder) {
        this.retrofitBuilder = retrofitBuilder;
    }

    private void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private void setOkHttpBuilder(OkHttpClient.Builder okHttpBuilder) {
        this.okHttpBuilder = okHttpBuilder;
    }

    private void setApiCache(ApiCache apiCache) {
        this.apiCache = apiCache;
    }

    private void setApiCacheBuilder(ApiCache.Builder apiCacheBuilder) {
        this.apiCacheBuilder = apiCacheBuilder;
    }

    private void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    /**
     * ViseApi的所有配置都通过建造者方式创建
     */
    public static final class Builder {
        private final Context context;
        private final OkHttpClient.Builder okHttpBuilder;
        private final Retrofit.Builder retrofitBuilder;
        private final ApiCache.Builder apiCacheBuilder;
        private okhttp3.Call.Factory callFactory;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private File httpCacheDirectory;
        private ApiCookie apiCookie;
        private Cache cache;
        private String baseUrl;
        private Boolean isCookie = false;
        private Boolean isCache = false;
        private boolean enableLog;
        private CacheMode cacheMode = CacheMode.ONLY_REMOTE;


        public Builder(Context mContext) {
            context = mContext.getApplicationContext();
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(mContext);
        }

        /**
         * 设置自定义OkHttpClient
         *
         * @param client
         * @return
         */
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * 设置Call的工厂
         *
         * @param factory
         * @return
         */
        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置连接超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.MILLISECONDS);
        }

        /**
         * 设置读取超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder readTimeout(int timeout) {
            return readTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置写入超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置是否添加Cookie
         *
         * @param isCookie
         * @return
         */
        public Builder cookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        /**
         * 设置是否添加缓存
         *
         * @param isCache
         * @return
         */
        public Builder cache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        /**
         * 设置代理
         *
         * @param proxy
         * @return
         */
        public Builder proxy(Proxy proxy) {
            okHttpBuilder.proxy(checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * 设置连接池
         *
         * @param connectionPool
         * @return
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.connectTimeout(timeout, unit);
            } else {
                okHttpBuilder.connectTimeout(FrameworkConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置写入超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.writeTimeout(timeout, unit);
            } else {
                okHttpBuilder.writeTimeout(FrameworkConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置读取超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder readTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.readTimeout(timeout, unit);
            } else {
                okHttpBuilder.readTimeout(FrameworkConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置请求BaseURL
         *
         * @param baseUrl
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * 设置转换工厂
         *
         * @param factory
         * @return
         */
        public Builder converterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * 设置CallAdapter工厂
         *
         * @param factory
         * @return
         */
        public Builder callAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * 设置请求头部
         *
         * @param headers
         * @return
         */
        public Builder headers(Map<String, String> headers) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(headers));
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param parameters
         * @return
         */
        public Builder parameters(Map<String, String> parameters) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(parameters));
            return this;
        }

        /**
         * 设置拦截器
         *
         * @param interceptor
         * @return
         */
        public Builder interceptor(Interceptor interceptor) {
            okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置网络拦截器
         *
         * @param interceptor
         * @return
         */
        public Builder networkInterceptor(Interceptor interceptor) {
            okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置Cookie管理
         *
         * @param cookie
         * @return
         */
        public Builder cookieManager(ApiCookie cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.apiCookie = cookie;
            return this;
        }

        /**
         * 设置SSL工厂
         *
         * @param sslSocketFactory
         * @return
         */
        public Builder SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 设置主机验证机制
         *
         * @param hostnameVerifier
         * @return
         */
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
         *
         * @return
         */
        public Builder postGzipInterceptor() {
            interceptor(new GzipRequestInterceptor());
            return this;
        }

        /**
         * 设置缓存Key，主要针对网路请求结果进行缓存
         *
         * @param cacheKey
         * @return
         */
        public Builder cacheKey(String cacheKey) {
            apiCacheBuilder.cacheKey(checkNotNull(cacheKey, "cacheKey == null"));
            return this;
        }

        /**
         * 设置缓存时间，默认永久，主要针对网路请求结果进行缓存
         *
         * @param cacheTime
         * @return
         */
        public Builder cacheTime(long cacheTime) {
            apiCacheBuilder.cacheTime(Math.max(-1, cacheTime));
            return this;
        }

        /**
         * 设置缓存类型，可根据类型自动配置缓存策略，主要针对网络请求结果进行缓存
         *
         * @param mCacheMode
         * @return
         */
        public Builder cacheMode(CacheMode mCacheMode) {
            cacheMode = mCacheMode;
            return this;
        }

        /**
         * 设置在线缓存，主要针对网路请求过程进行缓存
         *
         * @param cache
         * @return
         */
        public Builder cacheOnline(Cache cache) {
            networkInterceptor(new OnlineCacheInterceptor());
            this.cache = cache;
            return this;
        }

        /**
         * 是否打开log
         *
         * @param enable
         * @return
         */
        public Builder log(boolean enable) {
            this.enableLog = enable;
            return this;
        }

        public Builder cachePost(Cache cache) {
            PostCacheInterceptor interceptor = new PostCacheInterceptor(cache);
            interceptor(interceptor);
            this.cache = cache;
            return this;
        }

        /**
         * 设置在线缓存，主要针对网路请求过程进行缓存
         *
         * @param cache
         * @param cacheControlValue
         * @return
         */
        public Builder cacheOnline(Cache cache, final int cacheControlValue) {
            networkInterceptor(new OnlineCacheInterceptor(cacheControlValue));
            this.cache = cache;
            return this;
        }

        /**
         * 设置离线缓存，主要针对网路请求过程进行缓存
         *
         * @param cache
         * @return
         */
        public Builder cacheOffline(Cache cache) {
            networkInterceptor(new OfflineCacheInterceptor(context));
            interceptor(new OfflineCacheInterceptor(context));
            this.cache = cache;
            return this;
        }

        /**
         * 设置离线缓存，主要针对网路请求过程进行缓存
         *
         * @param cache
         * @param cacheControlValue
         * @return
         */
        public Builder cacheOffline(Cache cache, final int cacheControlValue) {
            networkInterceptor(new OfflineCacheInterceptor(context, cacheControlValue));
            interceptor(new OfflineCacheInterceptor(context, cacheControlValue));
            this.cache = cache;
            return this;
        }

        public RxClient build() {
            if (okHttpBuilder == null) {
                throw new IllegalStateException("okHttpBuilder required.");
            }

            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }

            if (apiCacheBuilder == null) {
                throw new IllegalStateException("apiCacheBuilder required.");
            }

            if (baseUrl == null) {
                baseUrl = ApiHost.getHost();
            }
            retrofitBuilder.baseUrl(baseUrl);

            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);

            if (callAdapterFactory == null) {
                callAdapterFactory = RxJava2CallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }

            if (hostnameVerifier == null) {
                hostnameVerifier = new SSLUtil.UnSafeHostnameVerifier(baseUrl);
            }
            okHttpBuilder.hostnameVerifier(hostnameVerifier);

            if (sslSocketFactory == null) {
                sslSocketFactory = SSLUtil.getSslSocketFactory(null, null, null);
            }
            okHttpBuilder.sslSocketFactory(sslSocketFactory);

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(FrameworkConfig.DEFAULT_MAX_IDLE_CONNECTIONS, FrameworkConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
            }
            okHttpBuilder.connectionPool(connectionPool);

            if (isCookie && apiCookie == null) {
                apiCookie = new ApiCookie(context);
            }
            if (isCookie) {
                okHttpBuilder.cookieJar(apiCookie);
            }

            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(context.getCacheDir(), FrameworkConfig.CACHE_HTTP_DIR);
            }
            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, FrameworkConfig.CACHE_MAX_SIZE);
                    }
                    cacheOnline(cache);
                    cacheOffline(cache);
                    cachePost(cache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cache != null) {
                okHttpBuilder.cache(cache);
            }

            if (enableLog) {
                //日志显示级别
                HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
                //新建log拦截器
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d("RxClient", message);
                    }
                });
                loggingInterceptor.setLevel(level);
                okHttpBuilder.addInterceptor(loggingInterceptor);

            }
            OkHttpClient okHttpClient = okHttpBuilder.build();
            retrofitBuilder.client(okHttpClient);
            Retrofit retrofit = retrofitBuilder.build();
            ApiCache apiCache = apiCacheBuilder.build();
            ApiService apiService = retrofit.create(ApiService.class);

            RxClient client = new RxClient();
            client.setOkHttpBuilder(okHttpBuilder);
            client.setCacheMode(cacheMode);
            client.setRetrofitBuilder(retrofitBuilder);
            client.setRetrofit(retrofit);
            client.setApiService(apiService);
            client.setContext(context);
            client.setApiCache(apiCache);
            client.setOkHttpClient(okHttpClient);
            client.setApiCacheBuilder(apiCacheBuilder);
            return client;
        }
    }
}

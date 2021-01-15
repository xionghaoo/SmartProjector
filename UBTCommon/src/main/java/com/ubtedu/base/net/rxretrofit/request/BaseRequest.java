package com.ubtedu.base.net.rxretrofit.request;

import com.ubtedu.base.cache.DiskCache;
import com.ubtedu.base.common.FrameworkConfig;
import com.ubtedu.base.net.rxretrofit.api.ApiService;
import com.ubtedu.base.net.rxretrofit.callback.ApiCallback;
import com.ubtedu.base.net.rxretrofit.config.NetGlobalConfig;
import com.ubtedu.base.net.rxretrofit.config.RxNet;
import com.ubtedu.base.net.rxretrofit.convert.GsonConverterFactory;
import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.core.ApiCookie;
import com.ubtedu.base.net.rxretrofit.func.ApiDataFunc;
import com.ubtedu.base.net.rxretrofit.func.ApiFunc;
import com.ubtedu.base.net.rxretrofit.func.ApiRetryFunc;
import com.ubtedu.base.net.rxretrofit.interceptor.HeadersInterceptor;
import com.ubtedu.base.net.rxretrofit.mode.ApiHost;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.mode.CacheMode;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;
import com.ubtedu.base.net.rxretrofit.mode.HttpHeaders;
import com.ubtedu.base.utils.SSLUtil;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * @Description: 请求基类
 * @author: qinicy
 * @date: 17/5/1 16:05
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected NetGlobalConfig netGlobalConfig;//全局配置
    protected ApiService apiService;//通用接口服务
    protected OkHttpClient okHttpClient;//OkHttp客户端
    protected Retrofit retrofit;//Retrofit对象
    protected ApiCache apiCache;//本地缓存对象
    protected Map<String, String> params = new LinkedHashMap<>();//请求参数
    protected HttpHeaders headers = new HttpHeaders();//请求头
    protected int retryDelayMillis;//请求失败重试间隔时间
    protected int retryCount;//重试次数
    protected String baseUrl;//基础域名
    protected String suffixUrl;//链接后缀
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存
    protected boolean isLocalCache;//是否使用本地缓存
    protected CacheMode cacheMode;//本地缓存类型
    protected String cacheKey;//本地缓存Key
    protected long cacheTime;//本地缓存时间

    /**
     * 添加请求参数
     * @param paramKey
     * @param paramValue
     * @return
     */
    public R addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.params.put(paramKey, paramValue);
        }
        return (R) this;
    }

    /**
     * 添加请求参数
     * @param params
     * @return
     */
    public R addParams(Map<String, String> params) {
        if (params != null) {
            this.params.putAll(params);
        }
        return (R) this;
    }

    /**
     * 移除请求参数
     * @param paramKey
     * @return
     */
    public R removeParam(String paramKey) {
        if (paramKey != null) {
            this.params.remove(paramKey);
        }
        return (R) this;
    }

    /**
     * 设置请求参数
     * @param params
     * @return
     */
    public R params(Map<String, String> params) {
        if (params != null) {
            this.params = params;
        }
        return (R) this;
    }

    /**
     * 添加请求头
     * @param headerKey
     * @param headerValue
     * @return
     */
    public R addHeader(String headerKey, String headerValue) {
        this.headers.put(headerKey, headerValue);
        return (R) this;
    }

    /**
     * 添加请求头
     * @param headers
     * @return
     */
    public R addHeaders(Map<String, String> headers) {
        this.headers.put(headers);
        return (R) this;
    }

    /**
     * 移除请求头
     * @param headerKey
     * @return
     */
    public R removeHeader(String headerKey) {
        this.headers.remove(headerKey);
        return (R) this;
    }

    /**
     * 设置请求头
     * @param headers
     * @return
     */
    public R headers(HttpHeaders headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return (R) this;
    }

    /**
     * 设置请求失败重试间隔时间（毫秒）
     * @param retryDelayMillis
     * @return
     */
    public R retryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return (R) this;
    }

    /**
     * 设置请求失败重试次数
     * @param retryCount
     * @return
     */
    public R retryCount(int retryCount) {
        this.retryCount = retryCount;
        return (R) this;
    }

    /**
     * 设置基础域名，当前请求会替换全局域名
     * @param baseUrl
     * @return
     */
    public R baseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl;
        }
        return (R) this;
    }

    /**
     * 设置请求链接后缀
     * @param suffixUrl
     * @return
     */
    public R suffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
        return (R) this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param connectTimeOut
     * @return
     */
    public R connectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return (R) this;
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param readTimeOut
     * @return
     */
    public R readTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param writeTimeOut
     * @return
     */
    public R writeTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    /**
     * 设置是否进行HTTP缓存
     * @param isHttpCache
     * @return
     */
    public R setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return (R) this;
    }

    /**
     * 设置是否进行本地缓存
     * @param isLocalCache
     * @return
     */
    public R setLocalCache(boolean isLocalCache) {
        this.isLocalCache = isLocalCache;
        return (R) this;
    }

    /**
     * 设置本地缓存类型
     * @param cacheMode
     * @return
     */
    public R cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (R) this;
    }

    /**
     * 设置本地缓存Key
     *
     * @param cacheKey
     * @return
     */
    public R cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (R) this;
    }

    /**
     * 设置本地缓存时间(毫秒)，默认永久
     *
     * @param cacheTime
     * @return
     */
    public R cacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return (R) this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public int getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSuffixUrl() {
        return suffixUrl;
    }

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public long getWriteTimeOut() {
        return writeTimeOut;
    }

    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    public boolean isHttpCache() {
        return isHttpCache;
    }

    public boolean isLocalCache() {
        return isLocalCache;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public <T> Observable<T> request(Class<T> clazz) {
        generateGlobalConfig();
        generateLocalConfig();
        return execute(clazz);
    }

    public <T> Observable<CacheResult<T>> cacheRequest(Class<T> clazz) {
        generateGlobalConfig();
        generateLocalConfig();
        return cacheExecute(clazz);
    }

    public <T> void request(ApiCallback<T> apiCallback) {
        generateGlobalConfig();
        generateLocalConfig();
         execute(apiCallback);
    }

    protected abstract <T> Observable<T> execute(Class<T> clazz);

    protected abstract <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz);

    protected abstract <T> void execute(ApiCallback<T> apiCallback);

    protected <T> ObservableTransformer<ResponseBody, T> norTransformer(final Class<T> clazz) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> upstream) {
                return upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers
                                .mainThread()).map(new ApiFunc<T>(clazz)).retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }

        };
    }

    protected <T> ObservableTransformer<ApiResult<T>, T> apiTransformer() {
        return new ObservableTransformer<ApiResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ApiResult<T>> upstream) {
                return upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread()).map(new ApiDataFunc<T>()).retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }

        };
    }

    /**
     * 生成局部配置
     */
    protected void generateLocalConfig() {
        OkHttpClient.Builder newBuilder = okHttpClient.newBuilder();

        if (netGlobalConfig.getGlobalParams() != null) {
            params.putAll(netGlobalConfig.getGlobalParams());
        }

        if (netGlobalConfig.getGlobalHeaders() != null) {
            headers.put(netGlobalConfig.getGlobalHeaders());
        }

        if (headers.headersMap.size() > 0) {
            newBuilder.addInterceptor(new HeadersInterceptor(headers.headersMap));
        }

        if (retryCount <= 0) {
            retryCount = netGlobalConfig.getRetryCount();
        }

        if (retryDelayMillis <= 0) {
            retryDelayMillis = netGlobalConfig.getRetryDelayMillis();
        }

        if (readTimeOut > 0) {
            newBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        }

        if (writeTimeOut > 0) {
            newBuilder.readTimeout(writeTimeOut, TimeUnit.SECONDS);
        }

        if (connectTimeOut > 0) {
            newBuilder.readTimeout(connectTimeOut, TimeUnit.SECONDS);
        }

        if (isHttpCache) {
            try {
                if (netGlobalConfig.getHttpCache() == null) {
                    netGlobalConfig.httpCache(new Cache(netGlobalConfig.getHttpCacheDirectory(), FrameworkConfig.CACHE_MAX_SIZE));
                }
                netGlobalConfig.cacheOnline(netGlobalConfig.getHttpCache());
                netGlobalConfig.cacheOffline(netGlobalConfig.getHttpCache());
            } catch (Exception e) {
                e.printStackTrace();
            }
            newBuilder.cache(netGlobalConfig.getHttpCache());
        }

        if (isLocalCache) {
            if (cacheKey != null) {
                RxNet.getApiCacheBuilder().cacheKey(cacheKey);
            }
            if (cacheTime > 0) {
                RxNet.getApiCacheBuilder().cacheTime(cacheTime);
            } else {
                RxNet.getApiCacheBuilder().cacheTime(DiskCache.CACHE_NEVER_EXPIRE);
            }
        }
        apiCache = RxNet.getApiCacheBuilder().build();

        if (baseUrl != null) {
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            newRetrofitBuilder.addConverterFactory(netGlobalConfig.getConverterFactory());
            newRetrofitBuilder.addCallAdapterFactory(netGlobalConfig.getCallAdapterFactory());
            if (netGlobalConfig.getCallFactory() != null) {
                newRetrofitBuilder.callFactory(netGlobalConfig.getCallFactory());
            }
            newRetrofitBuilder.client(okHttpClient);
            retrofit = newRetrofitBuilder.build();
        } else {
            RxNet.getRetrofitBuilder().client(okHttpClient);
            retrofit = RxNet.getRetrofitBuilder().build();
        }

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 生成全局配置
     */
    protected void generateGlobalConfig() {
        netGlobalConfig = RxNet.getInstance().config();

        if (netGlobalConfig.getBaseUrl() == null) {
            netGlobalConfig.baseUrl(ApiHost.getHost());
        }
        RxNet.getRetrofitBuilder().baseUrl(netGlobalConfig.getBaseUrl());

        if (netGlobalConfig.getConverterFactory() == null) {
            netGlobalConfig.converterFactory(GsonConverterFactory.create());
        }
        RxNet.getRetrofitBuilder().addConverterFactory(netGlobalConfig.getConverterFactory());

        if (netGlobalConfig.getCallAdapterFactory() == null) {
            netGlobalConfig.callAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        RxNet.getRetrofitBuilder().addCallAdapterFactory(netGlobalConfig.getCallAdapterFactory());

        if (netGlobalConfig.getCallFactory() != null) {
            RxNet.getRetrofitBuilder().callFactory(netGlobalConfig.getCallFactory());
        }

        if (netGlobalConfig.getHostnameVerifier() == null) {
            netGlobalConfig.hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier(netGlobalConfig.getBaseUrl()));
        }
        RxNet.getOkHttpBuilder().hostnameVerifier(netGlobalConfig.getHostnameVerifier());

        if (netGlobalConfig.getSslSocketFactory() == null) {
            netGlobalConfig.SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
        }
        RxNet.getOkHttpBuilder().sslSocketFactory(netGlobalConfig.getSslSocketFactory());

        if (netGlobalConfig.getConnectionPool() == null) {
            netGlobalConfig.connectionPool(new ConnectionPool(FrameworkConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    FrameworkConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        RxNet.getOkHttpBuilder().connectionPool(netGlobalConfig.getConnectionPool());

        if (netGlobalConfig.isCookie() && netGlobalConfig.getApiCookie() == null) {
            netGlobalConfig.apiCookie(new ApiCookie(RxNet.getContext()));
        }
        if (netGlobalConfig.isCookie()) {
            RxNet.getOkHttpBuilder().cookieJar(netGlobalConfig.getApiCookie());
        }

        if (netGlobalConfig.getHttpCacheDirectory() == null) {
            netGlobalConfig.setHttpCacheDirectory(new File(RxNet.getContext().getCacheDir(), FrameworkConfig.CACHE_HTTP_DIR));
        }
        if (netGlobalConfig.isHttpCache()) {
            try {
                if (netGlobalConfig.getHttpCache() == null) {
                    netGlobalConfig.httpCache(new Cache(netGlobalConfig.getHttpCacheDirectory(), FrameworkConfig.CACHE_MAX_SIZE));
                }
                netGlobalConfig.cacheOnline(netGlobalConfig.getHttpCache());
                netGlobalConfig.cacheOffline(netGlobalConfig.getHttpCache());
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        if (netGlobalConfig.getHttpCache() != null) {
            RxNet.getOkHttpBuilder().cache(netGlobalConfig.getHttpCache());
        }

        okHttpClient = RxNet.getOkHttpBuilder().build();
    }
}

package com.demo.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.net.demo.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author tang
 * @date 2018/10/18
 */

public class OkHttpClientManager {

    /**
     * 接口返回的数据是否设置默认值
     */
    private static final boolean JSON_SET_DEFAULT_VALUE = true;


    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static OkHttpClientManager mInstance = null;

    private OkHttpClient mOkHttpClient;
    private Gson mGson;

    private OkHttpClientManager() {
        mGson = new Gson();
        mHandler = new Handler(Looper.getMainLooper());
        mOkHttpClient = new OkHttpClient.Builder()
                //.retryOnConnectionFailure(true).build();
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
//                .addInterceptor(new NetInterceptor())
                .build();
//                .addInterceptor(sLoggingInterceptor)
//                .addInterceptor(sRewriteCacheControlInterceptor)
//                .addInterceptor(httpLoggingInterceptor);
    }

//    class NetInterceptor implements Interceptor{
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request().newBuilder()
//
//                    .addHeader("Connection","close").build();
//
//            return chain.proceed(request);
//        }
//    }


    private Handler mHandler;

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {

                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public void get(String url, Map<String, String> params, final HttpResultCallback callback) {
        if (params != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("?");
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    String value = params.get(key);
                    if (!TextUtils.isEmpty(value)) {
                        stringBuffer.append(key);
                        stringBuffer.append("=");
                        stringBuffer.append(value);
                        stringBuffer.append("&");
                    }

                }
            }
            url = url + stringBuffer.toString();
        }

        if (BuildConfig.DEBUG) {
            Log.d("OkHttpClientManager", "tang 地址 : " + url);
        }
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("app_version", "1.0")
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OkHttpClientManager", "call:" + call);
                onError(callback, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("OkHttpClientManager", "call:" + call);
                handlerResponse(response, callback);
            }
        });

    }

    /**
     * MEDIA_TYPE_FROM为 x-www-form-urlencoded
     *
     * @param url
     * @param params
     * @param callback
     */
    public void postParamsToFrom(String url, Map<String, String> params, final HttpResultCallback callback) {
        StringBuffer stringBuffer = new StringBuffer();

        if (params != null) {
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    String value = params.get(key);
                    if (!TextUtils.isEmpty(value)) {
                        stringBuffer.append(key);
                        stringBuffer.append("=");
                        stringBuffer.append(value);
                        stringBuffer.append("&");
                    }
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.e("OkHttpClientManager", "tang 地址 : " + url);
            Log.e("OkHttpClientManager", "tang 参数 : " + stringBuffer.toString());
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_FROM, stringBuffer.toString());
        final Request request = new Request.Builder().
                url(url).post(requestBody).addHeader("Connection", "close").build();


        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OkHttpClientManager", "call:" + call);

                onError(callback, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("OkHttpClientManager", "call:" + call);
                handlerResponse(response, callback);
            }
        });

    }

    /**
     * MEDIA_TYPE_FROM为 json
     *
     * @param url
     * @param params
     * @param callback
     */
    public void postParamsToJSON(String url, Map<String, String> params, final HttpResultCallback callback) {
        JSONObject jsonObject = new JSONObject();
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    jsonObject.put(key, params.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Log.e("OkHttpClientManager", "tang 地址 : " + url);
            Log.e("OkHttpClientManager", "tang 参数 : " + jsonObject.toString());
        }


        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonObject.toString());
        Request request = new Request.Builder().
                url(url).post(requestBody).build();


        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OkHttpClientManager", "call:" + call);

                onError(callback, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("OkHttpClientManager", "call:" + call);
                handlerResponse(response, callback);
            }
        });

    }


    private void handlerResponse(Response response, HttpResultCallback callback) {
        try {
            if (response.code() == 200) {
                String result = response.body().string();


                if (callback.mType == String.class) {

                    if (BuildConfig.DEBUG) {
                        HttpUrl url = response.request().url();
                        printJson(result, url);
                    }
                    onSuccess(callback, result);
                } else {
                    Object object;
                    Log.e("OkHttpClientManager", "开始解析json");
                    object = mGson.fromJson(result, callback.mType);

                    if (JSON_SET_DEFAULT_VALUE) {
                        result = JSON.toJSONString(object, FEATURES);
                        object = mGson.fromJson(result, callback.mType);
                    }
                    Log.e("OkHttpClientManager", "结束解析json");


                    if (BuildConfig.DEBUG) {
                        HttpUrl url = response.request().url();
                        printJson(result, url);
                    }

                    onSuccess(callback, object);
                }

            } else {
                onError(callback, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(callback, e.toString());
        }
    }

    private void onSuccess(final HttpResultCallback callback, final Object obj) {

        if (callback != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(obj);
                }
            });
        }
    }

    private void onError(final HttpResultCallback callback, final String error) {

        if (callback != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(error);
                }
            });
        }
    }

    /**
     * 打印json数据
     *
     * @param json
     * @param url
     */
    private void printJson(String json, HttpUrl url) {
        Log.d("OkHttpClientManager", "tang url  : " + url);
        try {
            JSONObject jsonObject = new JSONObject(json);
            Log.d("OkHttpClientManager", "tang 数据 : " + jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static SerializerFeature[] FEATURES = {
            //List字段如果为null,输出为[],而非null
            SerializerFeature.WriteNullListAsEmpty,
            //字符类型字段如果为null,输出为”“,而非null
            SerializerFeature.WriteNullStringAsEmpty,
            //数值字段如果为null,输出为0,而非null
            SerializerFeature.WriteNullNumberAsZero,
            //Boolean字段如果为null,输出为false,而非null
            SerializerFeature.WriteNullBooleanAsFalse,
            //结果是否格式化,默认为false
            SerializerFeature.PrettyFormat,
    };
}

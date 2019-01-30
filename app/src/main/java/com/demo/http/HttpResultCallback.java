package com.demo.http;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author tang
 * @date 2018/10/18
 */

public abstract class HttpResultCallback<T> {

    /**
     * 这是请求数据的返回类型，包含常见的（Bean，List等）
     */
    Type mType;

    public HttpResultCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 通过反射想要的返回类型
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("必须指定回调类型 : Missing type parameter.");
        }
        ParameterizedType type = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(type.getActualTypeArguments()[0]);
    }

    public abstract void onSuccess(T t);

    public abstract void onError(String error);
}

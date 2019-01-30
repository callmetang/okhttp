package com.demo.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.demo.beans.ShareBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * @author tang
 * @date 2019/1/30
 */

public class RequestQueue extends IntentService {
    /**
     * 请求文章列表
     */
    public static final int TYPE_CODE_SHARE_LIST = 100;

    /**
     * typeCode
     */
    public static final String TYPE_CODE = "typeCode";

    /**
     * serviceName
     */
    public static final String SERVICE_NAME = "RequestQueue";

    /**
     * 启动服务
     *
     * @param context
     * @param typeCode
     */
    public static void startServiceWithTypeCode(Context context, int typeCode) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setClass(context, RequestQueue.class);
            intent.putExtra(TYPE_CODE, typeCode);
            context.startService(intent);
        }
    }

    public RequestQueue(String name) {
        super(name);
    }

    public RequestQueue() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        int typeCode = intent.getIntExtra(TYPE_CODE, 0);
        switch (typeCode) {
            case TYPE_CODE_SHARE_LIST:
                getShareList();
                break;
            default:
        }
    }

    /**
     * 获取文章列表
     */
    private void getShareList() {
        String url = "http://htapin.haitao.com/V6/share/get_list.php";
        OkHttpClientManager.getInstance().get(url, null, new HttpResultCallback<ResultBean<List<ShareBean>>>() {
            @Override
            public void onSuccess(ResultBean<List<ShareBean>> listResultBean) {
                //请求成功发送给activity更新ui
                EventBus.getDefault().post(new EventMessage(EventCode.REQUEST_SHARE_LIST_CODE, listResultBean));
            }

            @Override
            public void onError(String error) {
                EventBus.getDefault().post(new EventMessage(EventCode.REQUEST_SHARE_ERROR_CODE, error));
            }
        });
    }
}

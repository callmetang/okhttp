package com.demo.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.demo.beans.ShareBean;
import com.demo.http.EventCode;
import com.demo.http.EventMessage;
import com.demo.http.RequestQueue;
import com.demo.http.ResultBean;
import com.net.demo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * @author tang
 * @date 2019/1/29
 */

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestQueue.startServiceWithTypeCode(this, RequestQueue.TYPE_CODE_SHARE_LIST);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {

        Log.d("MainActivity", "event:" + event);

        if (event != null) {
            if (event.getCode() == EventCode.REQUEST_SHARE_LIST_CODE) {
                ResultBean<List<ShareBean>> listResultBean = (ResultBean<List<ShareBean>>) event.getObj();
                TextView textView = new TextView(MainActivity.this);
                textView.setText(listResultBean.toString());
                setContentView(textView);
            }
        }
    }

}

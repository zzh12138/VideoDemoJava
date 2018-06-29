package com.example.zzh.videodemo_java.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.play.DataInter;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.ICover;
import com.kk.taurus.playerbase.utils.NetworkUtils;

/**
 * Created by zhangzhihao on 2018/6/19 11:47.
 * 播放出错界面
 */

public class ErrorCover extends BaseCover {

    private TextView retry;
    private int mCurrPosition;        //当前进度

    public ErrorCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.cover_error, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        retry = findViewById(R.id.cover_error_retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = BundlePool.obtain();
                bundle.putInt(EventKey.INT_DATA, mCurrPosition);
                setCoverVisibility(View.GONE);
                getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW, false);
                requestRetry(bundle);
            }
        });
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                mCurrPosition = 0;                                   //数据源准备好
                setCoverVisibility(View.GONE);
                getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW, false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE:
                mCurrPosition = bundle.getInt(EventKey.INT_ARG1);     //更新播放进度
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        setCoverVisibility(View.VISIBLE);
        notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null);
        getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW, true);
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        int state = NetworkUtils.getNetworkState(getContext());
        if (state < 0) {
            notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null);
            setCoverVisibility(View.VISIBLE);
            getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW, true);
        }
    }


    @Override
    public int getCoverLevel() {
        return ICover.COVER_LEVEL_MEDIUM;
    }
}

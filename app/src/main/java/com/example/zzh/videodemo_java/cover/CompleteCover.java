package com.example.zzh.videodemo_java.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.ICover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;

/**
 * Created by zhangzhihao on 2018/6/19 11:47.
 * 播放结束页面
 */

public class CompleteCover extends BaseCover {
    private TextView replay;

    public CompleteCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.cover_complete, null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:    //解码中
                setPlayCompleteState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:    //播放完毕
                setPlayCompleteState(true);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        if (getGroupValue().getBoolean(DataInter.Key.KEY_COMPLETE_SHOW)) {
            setPlayCompleteState(true);
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        setCoverVisibility(View.GONE);
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    private void setPlayCompleteState(boolean state) {
        setCoverVisibility(state ? View.VISIBLE : View.GONE);
        getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, state);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        replay = findViewById(R.id.cover_complete_replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReplay(null);
                setPlayCompleteState(false);
            }
        });
        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{DataInter.Key.KEY_IS_HAS_NEXT};
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    if (key.equals(DataInter.Key.KEY_IS_HAS_NEXT)) {
                        notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_NEXT, null);
//                        setNextState((Boolean) value);
                        //播放下一个的监听
                    }
                }
            };

    @Override
    public int getCoverLevel() {
        return ICover.COVER_LEVEL_MEDIUM;
    }
}

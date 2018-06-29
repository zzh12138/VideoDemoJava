package com.example.zzh.videodemo_java.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;


import com.example.zzh.videodemo_java.R;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.ICover;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;


/**
 * Created by zhangzhihao on 2018/6/19 11:46.
 * 加载界面
 */

public class LoadingCover extends BaseCover {
    public LoadingCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.cover_loading, null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO:
                setCoverVisibility(View.VISIBLE);
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                setCoverVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        setCoverVisibility(View.GONE);
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();   //获取播放器状态
        if (playerStateGetter != null && isInPlaybackState(playerStateGetter)) {
            if (playerStateGetter.isBuffering()) {
                setCoverVisibility(View.VISIBLE);
            } else {
                setCoverVisibility(View.GONE);
            }
        }
    }

    private boolean isInPlaybackState(PlayerStateGetter playerStateGetter) {
        int state = playerStateGetter.getState();
        return state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_STOPPED;
    }

    @Override
    public int getCoverLevel() {
        return ICover.COVER_LEVEL_LOW;
    }
}

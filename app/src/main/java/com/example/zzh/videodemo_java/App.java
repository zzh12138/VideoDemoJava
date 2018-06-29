package com.example.zzh.videodemo_java;

import android.app.Application;

import com.example.zzh.videodemo_java.play.ExoMediaPlayer;
import com.example.zzh.videodemo_java.play.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;

/**
 * Created by zhangzhihao on 2018/6/23 10:07.
 */

public class App extends Application {
    private static App instance;
    public static final int PLAN_ID_IJK = 1;
    public static final int PLAN_ID_EXO = 2;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//            PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_IJK, IjkPlayer.class.getName(), "IjkPlayer"));
//            PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);
            PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_EXO, ExoMediaPlayer.class.getName(), "ExoPlayer"));
            PlayerConfig.setDefaultPlanId(PLAN_ID_EXO);
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        PlayerLibrary.init(this);
    }
}

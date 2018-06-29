package com.example.zzh.videodemo_java;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.zzh.videodemo_java.adapter.NewsAdapter;
import com.example.zzh.videodemo_java.bean.NewsBean;
import com.example.zzh.videodemo_java.bean.ViewAttr;
import com.example.zzh.videodemo_java.fragment.VideoListFragment;
import com.example.zzh.videodemo_java.itemDecoration.LineItemDecoration;
import com.example.zzh.videodemo_java.play.AssistPlayer;
import com.example.zzh.videodemo_java.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends AppCompatActivity implements NewsAdapter.onVideoTitleClickListener, OnReceiverEventListener
        , OnPlayerEventListener, VideoListFragment.onBackClickListener {

    private static final String TAG = "MainActivity";
    public static final int DURATION = 550;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.root)
    FrameLayout mRoot;
    private List<NewsBean> mList;
    private NewsAdapter mAdapter;
    private VideoListFragment mFragment;
    private boolean isShowVideoList;
    private boolean isLandScape;         //是否横屏
    private FrameLayout mFullContainer;
    private int clickPosition;          //点击跳到视频列表的pos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        mAdapter = new NewsAdapter(mList, this);
        mAdapter.setOnVideoTitleClickListener(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new LineItemDecoration(this, R.drawable.line));
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滑动停止
                if (newState == SCROLL_STATE_IDLE) {
                    //滑动屏幕中间开始
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int first = manager.findFirstVisibleItemPosition();
                    int last = manager.findLastVisibleItemPosition();
                    for (int i = first; i <= last; i++) {
                        if (!TextUtils.isEmpty(mList.get(i).getVideoUrl())) {
                            //列表视频
                            View view = recyclerView.getChildAt(i - first);
                            FrameLayout container = view.findViewById(R.id.adapter_video_container);
                            int l[] = new int[2];
                            container.getLocationOnScreen(l);//y坐标包括状态栏的，所以要减掉
                            int top = l[1] - Util.getStatusBarHeight(MainActivity.this);
                            //获取y坐标，判断是否屏幕中间
                            int screenHeight = Util.getScreenHeight(MainActivity.this);
                            if (top >= screenHeight / 2 - Util.dip2px(MainActivity.this, 200) &&
                                    top <= screenHeight / 2 + Util.dip2px(MainActivity.this, 200) && !AssistPlayer.get().isPlaying()) {
                                ImageView imageView = view.findViewById(R.id.adapter_video_image);
                                imageView.performClick();
                                break;
                            }
                        }
                    }
                    //滑出屏幕高度一半停止播放
                    int playPosition = mAdapter.getPlayPosition();
                    if (playPosition != -1) {
                        View view = recyclerView.getChildAt(playPosition - first);
                        if (view != null) {
                            FrameLayout container = view.findViewById(R.id.adapter_video_container);
                            int l[] = new int[2];
                            container.getLocationOnScreen(l);
                            int top = l[1] - Util.getStatusBarHeight(MainActivity.this);
                            if (top < Util.dip2px(MainActivity.this, -100)
                                    || top > Util.getScreenHeight(MainActivity.this) - Util.dip2px(MainActivity.this, 100)) {
                                AssistPlayer.get().stop();
                                mAdapter.notifyItemRangeChanged(playPosition, 1);
                                mAdapter.setPlayPosition(-1);
                            }
                        } else {
                            AssistPlayer.get().stop();
                            mAdapter.notifyItemRangeChanged(playPosition, 1);
                            mAdapter.setPlayPosition(-1);
                        }
                    }
                }
            }
        });
        AssistPlayer.get().addOnReceiverEventListener(this);
        AssistPlayer.get().addOnPlayerEventListener(this);
    }

    private void initData() {
        mList = new ArrayList<>(20);
        for (int i = 0; i < 17; i++) {
            NewsBean bean = new NewsBean();
            bean.setType(R.layout.adapter_normal);
            bean.setTitle("我是新闻标题新闻标题我是新闻标题新闻标题我是新闻标题新闻标题" + i);
            int result = i % 3;
            switch (result) {
                case 0:
                    bean.setImageUrl("http://img5.imgtn.bdimg.com/it/u=2539397329,4056054332&fm=27&gp=0.jpg");
                    break;
                case 1:
                    bean.setImageUrl("http://img3.imgtn.bdimg.com/it/u=3159360602,2315537063&fm=27&gp=0.jpg");
                    break;
                case 2:
                    bean.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2156236282,1270726641&fm=27&gp=0.jpg");
                    break;
            }
            mList.add(bean);
        }

        NewsBean v1 = new NewsBean();
        v1.setTitle("视频新闻1");
        v1.setType(R.layout.adapter_video);
        v1.setImageUrl("http://img5.imgtn.bdimg.com/it/u=3577771133,2332148944&fm=27&gp=0.jpg");
        v1.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4");
        v1.setCommentNum(666);
        mList.add(4, v1);

        NewsBean v2 = new NewsBean();
        v2.setTitle("视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2");
        v2.setType(R.layout.adapter_video);
        v2.setImageUrl("http://img0.imgtn.bdimg.com/it/u=3622851037,3121030191&fm=27&gp=0.jpg");
        v2.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4");
        v2.setCommentNum(666);
        mList.add(9, v2);

        NewsBean v3 = new NewsBean();
        v3.setTitle("视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3");
        v3.setType(R.layout.adapter_video);
        v3.setImageUrl("http://img5.imgtn.bdimg.com/it/u=3974436224,4269321529&fm=27&gp=0.jpg");
        v3.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4");
        v3.setCommentNum(666);
        mList.add(10, v3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecycler.clearOnScrollListeners();
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();

    }

    @Override
    public void onBackPressed() {
        if (isLandScape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (isShowVideoList) {
            //显示了视频列表
            if (mFragment.isShowComment()) {
                //显示了评论数据
                mFragment.closeCommentFragment();
            } else {
                removeVideoListFragment();
            }
        } else {
            super.onBackPressed();
        }
    }

    //点击标题，添加视频列表list
    @Override
    public void onTitleClick(int position, ViewAttr attr) {
        if (mFragment == null) {
            mFragment = new VideoListFragment();
        }
        clickPosition = position;
        isShowVideoList = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable("attr", attr);
        bundle.putParcelable("news", mList.get(position));
        bundle.putBoolean("isAttach", AssistPlayer.get().isPlaying());
        mFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root, mFragment);
        transaction.commit();
    }

    @Override
    public void removeVideoListFragment() {
        isShowVideoList = false;
        if (mFragment.isPlayingFirst()) {
            mFragment.removeVideoList();
            mRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(mFragment);
                    transaction.commit();
                    NewsAdapter.VideoHolder holder = (NewsAdapter.VideoHolder) mRecycler.findViewHolderForLayoutPosition(clickPosition);
                    AssistPlayer.get().play(holder.container, null);
                }
            }, 750);
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(mFragment);
            transaction.commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            attachFullScreen();
        } else {
            attachList();
        }
        AssistPlayer.get().getReceiverGroup().getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

    //全屏

    private void attachFullScreen() {
        AssistPlayer.get().getReceiverGroup().getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        if (mFullContainer == null) {
            mFullContainer = new FrameLayout(this);
            mFullContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        mRoot.addView(mFullContainer, -1);
        AssistPlayer.get().play(mFullContainer, null);
    }

    private void attachList() {
        mRoot.removeView(mFullContainer);
        AssistPlayer.get().getReceiverGroup().getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        if (isShowVideoList) {
            if (mFragment.isShowComment()) {
                //绑定回评论页面
                mFragment.attachCommentContainer();
            } else {
                //绑定回视频列表页面
                mFragment.attachList();
            }
        } else {
            if (mAdapter != null) {
                NewsAdapter.VideoHolder holder = (NewsAdapter.VideoHolder) mRecycler.findViewHolderForLayoutPosition(mAdapter.getPlayPosition());
                AssistPlayer.get().play(holder.container, null);
            }
        }
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                onBackPressed();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                setRequestedOrientation(isLandScape ?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                AssistPlayer.get().stop();
                break;
        }
    }

}

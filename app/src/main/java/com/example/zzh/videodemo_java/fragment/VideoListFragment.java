package com.example.zzh.videodemo_java.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zzh.videodemo_java.MainActivity;
import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.Util;
import com.example.zzh.videodemo_java.adapter.VideoListAdapter;
import com.example.zzh.videodemo_java.bean.NewsBean;
import com.example.zzh.videodemo_java.bean.ViewAttr;
import com.example.zzh.videodemo_java.itemDecoration.EmptyItemDecoration;
import com.example.zzh.videodemo_java.play.AssistPlayer;
import com.example.zzh.videodemo_java.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.example.zzh.videodemo_java.MainActivity.DURATION;

/**
 * Created by zhangzhihao on 2018/6/23 10:11.
 * 需要记录当前播放的pos
 */

public class VideoListFragment extends Fragment implements VideoListAdapter.onAnimationFinishListener,
        VideoListAdapter.onMessageClickListener, CommentFragment.onCloseClickListener, OnPlayerEventListener, VideoListAdapter.onVideoPlayClickListener {
    private static final String TAG = "VideoListFragment";

    @BindView(R.id.fragment_video_list_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_video_list_back)
    ImageView mBack;
    @BindView(R.id.fragment_video_list_top_layout)
    FrameLayout mTopLayout;
    @BindView(R.id.fragment_video_list_root)
    FrameLayout mRoot;
    Unbinder unbinder;

    private List<NewsBean> mList;
    private VideoListAdapter mAdapter;
    private ViewAttr mAttr;
    private CommentFragment commentFragment;
    private boolean isShowComment;
    private onBackClickListener onBackClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            onBackClickListener = (VideoListFragment.onBackClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int first = manager.findFirstVisibleItemPosition();
                    int pos = manager.findFirstCompletelyVisibleItemPosition();
                    if (pos != mAdapter.getPlayPosition()) {
                        View view = mRecycler.getChildAt(pos - first);
                        ImageView imageView = view.findViewById(R.id.adapter_video_list_image);
                        imageView.performClick();
                    }
                }
            }
        });
        AssistPlayer.get().getReceiverGroup().getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true);
        AssistPlayer.get().addOnPlayerEventListener(this);
        return view;
    }

    private void initData() {
        mAttr = getArguments().getParcelable("attr");
        NewsBean bean = getArguments().getParcelable("news");
        mList = new ArrayList<>(5);
        mList.add(bean);
        mAdapter = new VideoListAdapter(mList, getContext());
        mAdapter.setAttach(getArguments().getBoolean("isAttach", false));
        mAdapter.setAttr(mAttr);
        mAdapter.setOnAnimationFinishListener(this);
        mAdapter.setOnMessageClickListener(this);
        mAdapter.setOnVideoPlayClickListener(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setItemAnimator(new FadeInUpAnimator());
        mRecycler.addItemDecoration(new EmptyItemDecoration(Util.dip2px(getContext(), 40)));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowComment) {
                    closeCommentFragment();
                } else {
                    if (onBackClickListener != null) {
                        onBackClickListener.removeVideoListFragment();
                    }
                }
            }
        });
        //背景颜色从透明黑色过度到黑色
        ObjectAnimator animator = ObjectAnimator.ofInt(mRoot, "backgroundColor", 0x00000000, 0xff000000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(DURATION);
        animator.start();
        if (!mAdapter.isAttach()) {
            //不需要过渡动画，直接请求数据
            onAnimationEnd();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AssistPlayer.get().getReceiverGroup().getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, false);
        AssistPlayer.get().removePlayerEventListener(this);
        mRecycler.clearOnScrollListeners();
        unbinder.unbind();
    }

    //过渡动画执行完毕，显示网络数据
    @Override
    public void onAnimationEnd() {
        for (int i = 0; i < 14; i++) {
            NewsBean v3 = new NewsBean();
            v3.setTitle("视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻" + i);
            v3.setType(R.layout.adapter_video);
            v3.setImageUrl("http://img5.imgtn.bdimg.com/it/u=3974436224,4269321529&fm=27&gp=0.jpg");
            v3.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4");
            v3.setCommentNum(666);
            mList.add(v3);
        }
        mAdapter.notifyItemRangeInserted(1, 14);
        mTopLayout.animate().alpha(1f).setDuration(250);
    }

    //显示评论页面
    @Override
    public void onMessageClick(NewsBean bean, ViewAttr attr) {
        isShowComment = true;
        if (commentFragment == null) {
            commentFragment = new CommentFragment();
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("attr", attr);
        bundle.putParcelable("news", bean);
        commentFragment.setArguments(bundle);
        commentFragment.setOnCloseClickListener(this);
        transaction.add(R.id.fragment_video_list_comment_container, commentFragment);
        transaction.commit();
    }

    public boolean isShowComment() {
        return isShowComment;
    }

    //去除list中除了第一条的数据，然后开始过渡动画
    public void removeVideoList() {
        int size = mList.size() - 1;
        for (int i = size; i > 0; i--) {
            mList.remove(i);
        }
        mAdapter.notifyItemRangeRemoved(1, size);
        final View view = mRecycler.getChildAt(0);
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        final ImageView image = view.findViewById(R.id.adapter_video_list_image);
        final FrameLayout container = view.findViewById(R.id.adapter_video_list_container);
        final TextView title = view.findViewById(R.id.adapter_video_list_title);
        final LinearLayout bottom = view.findViewById(R.id.bottom_layout);
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                title.setVisibility(View.GONE);
                bottom.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
                container.animate().scaleX(container.getMeasuredWidth() / (float) mAttr.getWidth())
                        .scaleY(container.getMeasuredHeight() / (float) mAttr.getHeight())
                        .setDuration(DURATION);
                view.animate().translationY(mAttr.getY() - location[1]).setDuration(DURATION);
                ObjectAnimator animator = ObjectAnimator.ofInt(mRoot, "backgroundColor", 0xff000000, 0x00000000);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setDuration(DURATION);
                animator.start();
            }
        }, 250);
    }

    @Override
    public void closeCommentFragment() {
        isShowComment = false;
        commentFragment.closeFragment();
        mRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(commentFragment);
                transaction.commit();
                VideoListAdapter.VideoHolder holder = (VideoListAdapter.VideoHolder) mRecycler.findViewHolderForLayoutPosition(mAdapter.getPlayPosition());
                AssistPlayer.get().play(holder.container, null);
            }
        }, DURATION);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                int playPosition = mAdapter.getPlayPosition();
                if (playPosition <= mList.size() - 2) {
                    LinearLayoutManager manager = (LinearLayoutManager) mRecycler.getLayoutManager();
                    int first = manager.findFirstVisibleItemPosition();
                    View view = mRecycler.getChildAt(playPosition + 1 - first);
                    mRecycler.smoothScrollBy(0, view.getTop());
                }
                break;
        }
    }

    @Override
    public void onVideoPlay(int position) {
        LinearLayoutManager manager = (LinearLayoutManager) mRecycler.getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        if (position == 0 && !mRecycler.canScrollVertically(-1)) {
            return;
        }
        View view = mRecycler.getChildAt(position - first);
        mRecycler.smoothScrollBy(0, view.getTop() - Util.dip2px(getContext(), 40));
    }

    public interface onBackClickListener {
        void removeVideoListFragment();
    }

    public boolean isPlayingFirst() {
        return AssistPlayer.get().isPlaying() && mAdapter.getPlayPosition() == 0;
    }

    public void attachList() {
        VideoListAdapter.VideoHolder holder = (VideoListAdapter.VideoHolder) mRecycler.findViewHolderForLayoutPosition(mAdapter.getPlayPosition());
        AssistPlayer.get().play(holder.container, null);
    }

    public void attachCommentContainer() {
        commentFragment.attachContainer();
    }
}

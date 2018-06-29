package com.example.zzh.videodemo_java.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.adapter.CommentAdapter;
import com.example.zzh.videodemo_java.adapter.NewsAdapter;
import com.example.zzh.videodemo_java.bean.CommentBean;
import com.example.zzh.videodemo_java.bean.ViewAttr;
import com.example.zzh.videodemo_java.play.AssistPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

import static com.example.zzh.videodemo_java.MainActivity.DURATION;

/**
 * Created by zhangzhihao on 2018/6/23 10:12.
 */

public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";
    @BindView(R.id.fragment_comment_video_container)
    FrameLayout mContainer;
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.fragment_comment_close)
    ImageView mClose;
    @BindView(R.id.fragment_comment_num)
    TextView mCommentNum;
    @BindView(R.id.fragment_comment_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_comment_root)
    RelativeLayout mRoot;
    Unbinder unbinder;

    private List<CommentBean> mList;
    private ViewAttr mAttr;
    private CommentAdapter mAdapter;
    private int[] location;
    private onCloseClickListener onCloseClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            CommentBean bean = new CommentBean();
            bean.setId(String.valueOf(i));
            bean.setUserName("我就是个开发仔" + i);
            bean.setContent("大佬不要再秀了，学不动啦......");
            bean.setPraiseNum(12138);
            mList.add(bean);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCloseClickListener != null) {
                    onCloseClickListener.closeCommentFragment();
                }
            }
        });
        return view;
    }

    private void initData() {
        mAttr = getArguments().getParcelable("attr");
        //背景色动画
        ObjectAnimator animator = ObjectAnimator.ofInt(mRoot, "backgroundColor", 0x00000000, 0xff000000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(DURATION);
        animator.start();
        location = new int[2];
        mContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //绘制完毕，开始执行动画
                mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                mContainer.getLocationOnScreen(location);
                mContainer.setTranslationX(mAttr.getX() - location[0]);
                mContainer.setTranslationY(mAttr.getY() - location[1]);
                mContainer.setScaleX(mAttr.getWidth() / (float) mContainer.getMeasuredWidth());
                mContainer.setScaleY(mAttr.getHeight() / (float) mContainer.getMeasuredHeight());
                mRecycler.setAlpha(0);
                mTextView.setAlpha(0);
                mClose.setAlpha(0);
                mCommentNum.setAlpha(0);
                mContainer.animate().translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(DURATION);
                mRecycler.animate().alpha(1).setDuration(DURATION);
                mTextView.animate().alpha(1).setDuration(DURATION);
                mClose.animate().alpha(1).setDuration(DURATION);
                mCommentNum.animate().alpha(1).setDuration(DURATION);
                AssistPlayer.get().play(mContainer, null);
                return true;
            }
        });
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommentAdapter(getContext(), mList);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void closeFragment() {
        mRecycler.animate().alpha(0).setDuration(DURATION);
        mTextView.animate().alpha(0).setDuration(DURATION);
        mClose.animate().alpha(0).setDuration(DURATION);
        mCommentNum.animate().alpha(0).setDuration(DURATION);
        mContainer.animate().translationY(mAttr.getY() - location[1]).setDuration(DURATION);
        ObjectAnimator animator = ObjectAnimator.ofInt(mRoot, "backgroundColor", 0xff000000, 0x00000000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(DURATION);
        animator.start();
    }

    public void setOnCloseClickListener(CommentFragment.onCloseClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;
    }

    public interface onCloseClickListener {
        void closeCommentFragment();
    }

    public void attachContainer() {
        AssistPlayer.get().play(mContainer, null);
    }
}

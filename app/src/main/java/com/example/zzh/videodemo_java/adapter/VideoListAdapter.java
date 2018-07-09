package com.example.zzh.videodemo_java.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.zzh.videodemo_java.MainActivity;
import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.bean.NewsBean;
import com.example.zzh.videodemo_java.bean.ViewAttr;
import com.example.zzh.videodemo_java.play.AssistPlayer;
import com.kk.taurus.playerbase.entity.DataSource;

import java.util.List;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;


/**
 * Created by zhangzhihao on 2018/6/20 16:38.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder> {
    private static final String TAG = "VideoListAdapter";

    private List<NewsBean> mList;
    private Context mContext;
    private boolean isAttach;
    private ViewAttr attr;
    private int mPlayPosition;
    private onAnimationFinishListener onAnimationFinishListener;
    private onMessageClickListener onMessageClickListener;
    private onVideoPlayClickListener onVideoPlayClickListener;

    public VideoListAdapter(List<NewsBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mPlayPosition = -1;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, int position) {
        final NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        holder.message.setText(String.valueOf(bean.getCommentNum()));
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.image);
        holder.container.removeAllViews();
        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(options).into(holder.icon);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSource dataSource = new DataSource(bean.getVideoUrl());
                AssistPlayer.get().play(holder.container, dataSource);
                mPlayPosition = holder.getLayoutPosition();
                if (onVideoPlayClickListener != null) {
                    onVideoPlayClickListener.onVideoPlay(mPlayPosition);
                }
            }
        });
        if (isAttach && position == 0) {
            holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    holder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int l[] = new int[2];
                    holder.itemView.getLocationOnScreen(l);
                    holder.itemView.setTranslationY(attr.getY() - l[1]);
                    holder.containerLayout.setScaleX(attr.getWidth() / (float) holder.container.getMeasuredWidth());
                    holder.containerLayout.setScaleY(attr.getHeight() / (float) holder.container.getMeasuredHeight());
                    holder.title.setAlpha(0);
                    holder.bottomLayout.setAlpha(0);
                    holder.itemView.animate().translationY(0).setDuration(MainActivity.DURATION);
                    holder.containerLayout.animate().scaleX(1).scaleY(1).setDuration(MainActivity.DURATION);
                    holder.title.animate().alpha(1f).setDuration(MainActivity.DURATION);
                    holder.bottomLayout.animate().alpha(1f).setDuration(MainActivity.DURATION);
                    holder.image.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (onAnimationFinishListener != null) {
                                onAnimationFinishListener.onAnimationEnd();
                            }
                        }
                    }, MainActivity.DURATION);
                    AssistPlayer.get().play(holder.container, null);
                    isAttach = false;
                    mPlayPosition = 0;
                    return true;
                }
            });
        }
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getLayoutPosition() != mPlayPosition) {
                    holder.image.performClick();
                } else {
                    ViewAttr attr = new ViewAttr();
                    int l[] = new int[2];
                    holder.container.getLocationOnScreen(l);
                    attr.setX(l[0]);
                    attr.setY(l[1]);
                    attr.setWidth(holder.container.getWidth());
                    attr.setHeight(holder.container.getHeight());
                    onMessageClickListener.onMessageClick(bean, attr);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getLayoutPosition() != mPlayPosition) {
                    holder.image.performClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttr(ViewAttr attr) {
        this.attr = attr;
    }

    public void setOnAnimationFinishListener(VideoListAdapter.onAnimationFinishListener onAnimationFinishListener) {
        this.onAnimationFinishListener = onAnimationFinishListener;
    }

    public void setOnMessageClickListener(VideoListAdapter.onMessageClickListener onMessageClickListener) {
        this.onMessageClickListener = onMessageClickListener;
    }

    public void setOnVideoPlayClickListener(VideoListAdapter.onVideoPlayClickListener onVideoPlayClickListener) {
        this.onVideoPlayClickListener = onVideoPlayClickListener;
    }

    public int getPlayPosition() {
        return mPlayPosition;
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {
        public FrameLayout container;
        FrameLayout containerLayout;
        TextView title;
        TextView message;
        ImageView icon;
        ImageView image;
        LinearLayout bottomLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.adapter_video_list_container);
            title = itemView.findViewById(R.id.adapter_video_list_title);
            message = itemView.findViewById(R.id.adapter_video_list_message_num);
            icon = itemView.findViewById(R.id.adapter_video_list_icon);
            image = itemView.findViewById(R.id.adapter_video_list_image);
            bottomLayout = itemView.findViewById(R.id.bottom_layout);
            containerLayout = itemView.findViewById(R.id.adapter_video_list_container_layout);
        }

        @Override
        public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
            itemView.setAlpha(0);
        }

        @Override
        public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

        }

        @Override
        public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .alpha(1).setDuration(MainActivity.DURATION).start();
        }

        @Override
        public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .alpha(0).setDuration(MainActivity.DURATION).start();
        }
    }

    public interface onAnimationFinishListener {
        void onAnimationEnd();
    }

    public interface onMessageClickListener {
        void onMessageClick(NewsBean bean, ViewAttr attr);
    }

    public interface onVideoPlayClickListener {
        void onVideoPlay(int position);
    }
}

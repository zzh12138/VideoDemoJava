package com.example.zzh.videodemo_java.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.bean.NewsBean;
import com.example.zzh.videodemo_java.bean.ViewAttr;
import com.example.zzh.videodemo_java.play.AssistPlayer;
import com.kk.taurus.playerbase.entity.DataSource;

import java.util.List;


/**
 * Created by zhangzhihao on 2018/6/19 17:35.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> mList;
    private Context mContext;
    private int mPlayPosition;
    private onVideoTitleClickListener onVideoTitleClickListener;

    public NewsAdapter(List<NewsBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mPlayPosition = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(viewType, parent, false);
        if (viewType == R.layout.adapter_normal) {
            return new NormalHolder(view);
        } else {
            return new VideoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            setNormalData((NormalHolder) holder, position);
        } else {
            setVideoData((VideoHolder) holder, position);
        }
    }

    private void setNormalData(NormalHolder holder, int position) {
        NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.image);
    }

    private void setVideoData(final VideoHolder holder, int position) {
        final NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.imageView);
        holder.container.removeAllViews();
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoTitleClickListener != null) {
                    int location[] = new int[2];
                    holder.container.getLocationOnScreen(location);
                    ViewAttr attr = new ViewAttr();
                    attr.setX(location[0]);
                    attr.setY(location[1]);
                    attr.setWidth(holder.container.getWidth());
                    attr.setHeight(holder.container.getHeight());
                    onVideoTitleClickListener.onTitleClick(holder.getLayoutPosition(), attr);
                }
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSource dataSource = new DataSource(bean.getVideoUrl());
                mPlayPosition = holder.getLayoutPosition();
                if (!dataSource.equals(AssistPlayer.get().getDataSource())) {
                    AssistPlayer.get().play(holder.container, dataSource);
                } else {
                    AssistPlayer.get().play(holder.container, null);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    public void setOnVideoTitleClickListener(NewsAdapter.onVideoTitleClickListener onVideoTitleClickListener) {
        this.onVideoTitleClickListener = onVideoTitleClickListener;
    }

    class NormalHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public NormalHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adapter_normal_title);
            image = itemView.findViewById(R.id.adapter_normal_image);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        public FrameLayout container;

        public VideoHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adapter_video_title);
            imageView = itemView.findViewById(R.id.adapter_video_image);
            container = itemView.findViewById(R.id.adapter_video_container);
        }
    }

    public interface onVideoTitleClickListener {
        void onTitleClick(int position, ViewAttr attr);
    }

    public int getPlayPosition() {
        return mPlayPosition;
    }

    public void setPlayPosition(int mPlayPosition) {
        this.mPlayPosition = mPlayPosition;
    }
}

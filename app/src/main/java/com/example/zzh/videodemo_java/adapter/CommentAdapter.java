package com.example.zzh.videodemo_java.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zzh.videodemo_java.R;
import com.example.zzh.videodemo_java.bean.CommentBean;

import java.util.List;

/**
 * Created by zhangzhihao on 2018/6/22 15:58.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private List<CommentBean> mList;

    public CommentAdapter(Context context, List<CommentBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.adapter_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        CommentBean comment = mList.get(position);
        holder.name.setText(comment.getUserName());
        holder.num.setText(String.valueOf(comment.getPraiseNum()));
        holder.content.setText(comment.getContent());
        RequestOptions options=new RequestOptions().circleCrop();
        Glide.with(context).load(R.mipmap.ic_launcher).apply(options).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView name;
        private TextView num;
        private TextView content;

        public CommentHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.adapter_comment_icon);
            name = itemView.findViewById(R.id.adapter_comment_name);
            num = itemView.findViewById(R.id.adapter_comment_praiseNum);
            content = itemView.findViewById(R.id.comment_content);
        }
    }
}

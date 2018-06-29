package com.example.zzh.videodemo_java.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangzhihao on 2018/6/19 17:36.
 */

public class NewsBean implements Parcelable {
    private String title;
    private String imageUrl;
    private String videoUrl;
    private int type;
    private int commentNum;

    public NewsBean() {
    }

    protected NewsBean(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
        videoUrl = in.readString();
        type = in.readInt();
        commentNum = in.readInt();
    }

    public static final Creator<NewsBean> CREATOR = new Creator<NewsBean>() {
        @Override
        public NewsBean createFromParcel(Parcel in) {
            return new NewsBean(in);
        }

        @Override
        public NewsBean[] newArray(int size) {
            return new NewsBean[size];
        }
    };

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeString(videoUrl);
        dest.writeInt(type);
        dest.writeInt(commentNum);
    }
}

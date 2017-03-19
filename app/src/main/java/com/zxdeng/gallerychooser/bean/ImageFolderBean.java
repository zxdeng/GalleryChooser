package com.zxdeng.gallerychooser.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zengxudeng on 2017/3/19.
 */

public class ImageFolderBean implements Parcelable {
    private String name;  //当前文件夹的名字
    private String path;  //当前文件夹的路径
    private ImageBean coverImage;   //当前文件夹需要要显示的缩略图，默认为最近的一次图片
    private List<ImageBean> images;  //当前文件夹下所有图片的集合

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageBean getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(ImageBean coverImage) {
        this.coverImage = coverImage;
    }

    public List<ImageBean> getImages() {
        return images;
    }

    public void setImages(List<ImageBean> images) {
        this.images = images;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeParcelable(this.coverImage, flags);
        dest.writeTypedList(this.images);
    }

    public ImageFolderBean() {
    }

    protected ImageFolderBean(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.coverImage = in.readParcelable(ImageBean.class.getClassLoader());
        this.images = in.createTypedArrayList(ImageBean.CREATOR);
    }

    public static final Parcelable.Creator<ImageFolderBean> CREATOR = new Parcelable.Creator<ImageFolderBean>() {
        @Override
        public ImageFolderBean createFromParcel(Parcel source) {
            return new ImageFolderBean(source);
        }

        @Override
        public ImageFolderBean[] newArray(int size) {
            return new ImageFolderBean[size];
        }
    };

    @Override
    public String toString() {
        return "ImageFolderBean{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", coverImage=" + coverImage +
                ", images=" + images +
                '}';
    }
}

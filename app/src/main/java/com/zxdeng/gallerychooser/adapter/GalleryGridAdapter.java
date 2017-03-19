package com.zxdeng.gallerychooser.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zxdeng.gallerychooser.R;
import com.zxdeng.gallerychooser.bean.ImageBean;

import java.util.List;

/**
 * Created by zengxudeng on 2017/3/19.
 */

public class GalleryGridAdapter extends RecyclerView.Adapter<GalleryGridAdapter.MyGalleryGridVH>{

    private String TAG = "Gallery";
    private Context mContext;
    private List<ImageBean> imageBeanList;

    public GalleryGridAdapter(Context mContext, List<ImageBean> imageBeanList) {
        this.mContext = mContext;
        this.imageBeanList = imageBeanList;
    }

    @Override
    public MyGalleryGridVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyGalleryGridVH(LayoutInflater.from(mContext).inflate(R.layout.gallery_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyGalleryGridVH holder, int position) {
        Glide.with(mContext).load(imageBeanList.get(position).getPath()).into(holder.iv);

    }

    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }

    class MyGalleryGridVH extends RecyclerView.ViewHolder{

        ImageView iv;
        AppCompatCheckBox checkBox;
        public MyGalleryGridVH(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_img);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.cb_choose);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (b){
                        imageBeanList.get(getAdapterPosition()).setChecked(b);
                    Log.d(TAG, "选中的位置为: " + getAdapterPosition());
//                    }
                }
            });
        }
    }
}

package com.zxdeng.gallerychooser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zxdeng.gallerychooser.adapter.GalleryGridAdapter;
import com.zxdeng.gallerychooser.bean.ImageBean;
import com.zxdeng.gallerychooser.bean.ImageFolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zengxudeng on 2017/3/19.
 */

public class GalleryGridFragment extends Fragment implements View.OnClickListener {

    private String TAG = "Gallery";

    private RecyclerView recyclerView;
    private List<ImageBean> images;
    private Map<String,ImageFolderBean> mImagesGroup;
    private TextView mTvFolderBtn,mTvPreviewBtn;

    private PopupWindow mPopupWindow;

    private GalleryGridAdapter mAdapter;
    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608
    private Context mContext;
    private View mContentView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContentView = inflater.inflate(R.layout.fragment_gallery_grid,container,false);
        recyclerView = (RecyclerView) mContentView.findViewById(R.id.rcv_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mTvFolderBtn = (TextView) mContentView.findViewById(R.id.tv_folder_btn);
        mTvPreviewBtn = (TextView) mContentView.findViewById(R.id.tv_preview_btn);
        mTvFolderBtn.setOnClickListener(this);
        mTvPreviewBtn.setOnClickListener(this);
        images = new ArrayList<>();
        mImagesGroup = new HashMap<>();
        getImages();
        return mContentView;
    }

    private void getImages() {
        mImagesGroup.clear();
        Observable.create(new ObservableOnSubscribe<Map<String,ImageFolderBean>>() {
            @Override
            public void subscribe(ObservableEmitter<Map<String,ImageFolderBean>> e) throws Exception {
                Cursor mCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_PROJECTION,null,null,IMAGE_PROJECTION[6]+" DESC");
                while (mCursor.moveToNext()){
                    String name = mCursor.getString(mCursor.getColumnIndex(IMAGE_PROJECTION[0]));
                    String path = mCursor.getString(mCursor.getColumnIndex(IMAGE_PROJECTION[1]));
                    long size = mCursor.getLong(mCursor.getColumnIndex(IMAGE_PROJECTION[2]));
                    int width = mCursor.getInt(mCursor.getColumnIndex(IMAGE_PROJECTION[3]));
                    int height = mCursor.getInt(mCursor.getColumnIndex(IMAGE_PROJECTION[4]));
                    String mimeType = mCursor.getString(mCursor.getColumnIndex(IMAGE_PROJECTION[5]));
                    long date = mCursor.getLong(mCursor.getColumnIndex(IMAGE_PROJECTION[6]));

                    ImageBean imageBean = new ImageBean();
                    imageBean.setAddTime(date);
                    imageBean.setHeight(height);
                    imageBean.setMimeType(mimeType);
                    imageBean.setName(name);
                    imageBean.setPath(path);
                    imageBean.setSize(size);
                    imageBean.setWidth(width);

                    images.add(imageBean);
                    File file = new File(path);
                    String folderName = file.getParentFile().getName();
                    String folderPath = file.getParentFile().getAbsolutePath();
                    if (!mImagesGroup.containsKey(folderName)){
                        ImageFolderBean imageFolder = new ImageFolderBean();
                        imageFolder.setName(folderName);
                        imageFolder.setPath(folderPath);
                        imageFolder.setCoverImage(imageBean);
                        List<ImageBean> imgs = new ArrayList<>();
                        imgs.add(imageBean);
                        imageFolder.setImages(imgs);
                        mImagesGroup.put(folderName,imageFolder);
                    }else {
                        mImagesGroup.get(folderName).getImages().add(imageBean);
                    }
                }

                e.onNext(mImagesGroup);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<String,ImageFolderBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map<String,ImageFolderBean> imageFolders) {

                        for (Map.Entry<String, ImageFolderBean> entry : imageFolders.entrySet()) {
                            Log.d(TAG, "文件夹: " + entry.getKey() +"，文件个数为："+entry.getValue().getImages().size());
                        }
                        setUpData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {


                    }
                });
    }

    private void setUpData() {
        if (null == mAdapter){
            mAdapter = new GalleryGridAdapter(getContext(),images);
            recyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_folder_btn:
                showPopupWindow();
                break;
            case R.id.tv_preview_btn:
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        if (null == mPopupWindow){
            mPopupWindow = new PopupWindow(getContext());
//            mPopupWindow.sho
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_choose_folder,null);
            mPopupWindow.setContentView(contentView);
        }

//        mPopupWindow.showAsDropDown();
    }
}

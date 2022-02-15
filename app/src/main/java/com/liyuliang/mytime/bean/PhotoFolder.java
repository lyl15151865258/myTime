package com.liyuliang.mytime.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 照片文件夹实体类
 * Created at 2019/6/17 13:41
 *
 * @author LiYuliang
 * @version 1.0
 */

public class PhotoFolder implements Serializable {

    /* 文件夹名 */
    private String name;
    /* 文件夹路径 */
    private String dirPath;
    /* 该文件夹下图片列表 */
    private List<com.liyuliang.mytime.bean.Photo> photoList;
    /* 标识是否选中该文件夹 */
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public List<com.liyuliang.mytime.bean.Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<com.liyuliang.mytime.bean.Photo> photoList) {
        this.photoList = photoList;
    }
}

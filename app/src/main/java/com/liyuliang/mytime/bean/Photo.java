package com.liyuliang.mytime.bean;

import java.io.Serializable;

/**
 * 照片实体类
 * Created at 2019/6/17 13:20
 *
 * @author LiYuliang
 * @version 1.0
 */

public class Photo implements Serializable {

    private int id;
    private String path;  //路径
    private boolean isCamera;

    public Photo(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setIsCamera(boolean isCamera) {
        this.isCamera = isCamera;
    }
}

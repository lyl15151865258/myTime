package com.liyuliang.mytime.bean;

import java.util.List;

/**
 * 软件历史版本更新日志
 * Created by LiYuliang on 2017/11/8.
 *
 * @author LiYuliang
 * @version 2017/11/08
 */

public class VersionLog {

    private List<Version> versionInfoList;

    public List<Version> getVersionInfoList() {
        return versionInfoList;
    }

    public void setVersionInfoList(List<Version> versionInfoList) {
        this.versionInfoList = versionInfoList;
    }
}

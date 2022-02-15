package com.liyuliang.mytime.sqlite.dbupdate;

import android.database.sqlite.SQLiteDatabase;

import com.liyuliang.mytime.sqlite.DbHelper;
import com.liyuliang.mytime.sqlite.Upgrade;
import com.liyuliang.mytime.sqlite.VersionCode;
import com.liyuliang.mytime.utils.LogUtils;

@VersionCode(3)
public class VersionThird extends Upgrade {
    @Override
    public void update(SQLiteDatabase db) {
        //数据库版本升级时会执行这个方法
        //第一步将表A重命名为temp_A
        //第二步创建新表A,此时表结构已加了2列
        //第三步讲temp_A表中的数据插入到表A
        //第四步删除临时表temp_A
        DbHelper.createTableForCitySearch(db);
        LogUtils.d("dbversion" + "执行了2版本更新");
    }
}

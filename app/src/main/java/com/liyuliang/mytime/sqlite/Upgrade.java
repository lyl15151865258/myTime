package com.liyuliang.mytime.sqlite;

import android.database.sqlite.SQLiteDatabase;

public abstract class Upgrade {
    public abstract void update(SQLiteDatabase db);
}
package com.liyuliang.mytime.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author LiYuliang
 * @version 1.0
 * @description 倒计时实体类
 * Created at 2021-12-29 14:44
 */

public class Countdown implements Parcelable {

    private int id;

    private String name;

    private String datetime;

    private String dateType;

    private int top;

    private int repeat;

    private int repeatType;

    private int remind;

    private int remindType;

    private String remindTime;

    private String wallpaper;

    private String remark;

    private int unit;

    private int animation;

    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public int getRemindType() {
        return remindType;
    }

    public void setRemindType(int remindType) {
        this.remindType = remindType;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected Countdown(Parcel in) {
        id = in.readInt();
        name = in.readString();
        datetime = in.readString();
        dateType = in.readString();
        top = in.readInt();
        repeat = in.readInt();
        repeatType = in.readInt();
        remind = in.readInt();
        remindType = in.readInt();
        remindTime = in.readString();
        wallpaper = in.readString();
        remark = in.readString();
        unit = in.readInt();
        animation = in.readInt();
        type = in.readInt();
    }

    public static final Creator<Countdown> CREATOR = new Creator<Countdown>() {
        @Override
        public Countdown createFromParcel(Parcel in) {
            return new Countdown(in);
        }

        @Override
        public Countdown[] newArray(int size) {
            return new Countdown[size];
        }
    };

    public static Creator<Countdown> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(datetime);
        dest.writeString(dateType);
        dest.writeInt(top);
        dest.writeInt(repeat);
        dest.writeInt(repeatType);
        dest.writeInt(remind);
        dest.writeInt(remindType);
        dest.writeString(remindTime);
        dest.writeString(wallpaper);
        dest.writeString(remark);
        dest.writeInt(unit);
        dest.writeInt(animation);
        dest.writeInt(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Countdown)) {
            return false;
        } else {
            try {
                Countdown that = (Countdown) o;
                return id == that.id;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "Countdown{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", datetime='" + datetime + '\'' +
                ", dateType='" + dateType + '\'' +
                ", top=" + top +
                ", repeat=" + repeat +
                ", repeatType=" + repeatType +
                ", remind=" + remind +
                ", remindType=" + remindType +
                ", remindTime='" + remindTime + '\'' +
                ", wallpaper='" + wallpaper + '\'' +
                ", remark='" + remark + '\'' +
                ", unit=" + unit +
                ", animation=" + animation +
                ", type=" + type +
                '}';
    }
}
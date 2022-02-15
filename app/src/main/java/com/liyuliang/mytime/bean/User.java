package com.liyuliang.mytime.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * 用户实体类
 * Created at 2019/5/31 9:15
 *
 * @author LiYuliang
 * @version 1.0
 */

public class User implements Parcelable {

    private int id;
    private String phone;
    private String password;
    private String username;
    private String gender;
    private String mail;
    private String icon;
    private int effective;
    private String registerTime;
    private String registerIp;

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeString(username);
        dest.writeString(gender);
        dest.writeString(mail);
        dest.writeString(icon);
        dest.writeInt(effective);
        dest.writeString(registerTime);
        dest.writeString(registerIp);
    }

    public User(Parcel source) {
        id = source.readInt();
        phone = source.readString();
        password = source.readString();
        username = source.readString();
        gender = source.readString();
        mail = source.readString();
        icon = source.readString();
        effective = source.readInt();
        registerTime = source.readString();
        registerIp = source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getEffective() {
        return effective;
    }

    public void setEffective(int effective) {
        this.effective = effective;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public User() {

    }

    public User(int id, String phone, String password, String username, String gender, String mail, String icon,
                int effective, String registerTime, String registerIp) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.username = username;
        this.gender = gender;
        this.mail = mail;
        this.icon = icon;
        this.effective = effective;
        this.registerTime = registerTime;
        this.registerIp = registerIp;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", mail='" + mail + '\'' +
                ", icon='" + icon + '\'' +
                ", effective=" + effective +
                ", registerTime='" + registerTime + '\'' +
                ", registerIp='" + registerIp + '\'' +
                '}';
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        } else {
            try {
                User that = (User) o;
                return id == that.id;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}

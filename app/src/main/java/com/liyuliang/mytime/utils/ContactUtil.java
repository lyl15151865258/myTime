package com.liyuliang.mytime.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Intents.Insert;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactUtil {

    /**
     * 添加到手机联系人
     *
     * @param context   Context对象
     * @param name      姓名
     * @param phone     手机号
     * @param workPhone 工作电话
     * @param company   公司名
     * @param position  职位
     * @param email     电子邮箱
     * @param address   地址
     */
    public static void addContact(Context context, String name, String phone, String workPhone, String company, String position, String email, String address) {
        Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
        Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
        intent.putExtra(Insert.NAME, name);
        intent.putExtra(Insert.PHONE, phone);
        intent.putExtra(Insert.PHONE_TYPE, Phone.TYPE_MOBILE);
        intent.putExtra(Insert.SECONDARY_PHONE, workPhone);
        intent.putExtra(Insert.SECONDARY_PHONE_TYPE, Phone.TYPE_COMPANY_MAIN);
        intent.putExtra(Insert.COMPANY, company);
        intent.putExtra(Insert.JOB_TITLE, position);
        intent.putExtra(Insert.EMAIL, email);
        intent.putExtra(Insert.POSTAL, address);
        context.startActivity(intent);
    }

    /**
     * 保存至已有联系人
     *
     * @param context   Context对象
     * @param phone     手机号
     * @param workPhone 工作电话
     * @param company   公司名
     * @param position  职位
     * @param email     电子邮箱
     * @param address   地址
     */
    public static void saveExistContact(Context context, String phone, String workPhone, String company, String position, String email, String address) {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType("vnd.android.cursor.item/person");
        intent.setType("vnd.android.cursor.item/contact");
        intent.setType("vnd.android.cursor.item/raw_contact");
        //    intent.putExtra(Insert.NAME, name);
        intent.putExtra(Insert.PHONE, phone);
        intent.putExtra(Insert.PHONE_TYPE, Phone.TYPE_MOBILE);
        intent.putExtra(Insert.SECONDARY_PHONE, workPhone);
        intent.putExtra(Insert.SECONDARY_PHONE_TYPE, Phone.TYPE_COMPANY_MAIN);
        intent.putExtra(Insert.COMPANY, company);
        intent.putExtra(Insert.JOB_TITLE, position);
        intent.putExtra(Insert.EMAIL, email);
        intent.putExtra(Insert.POSTAL, address);
        context.startActivity(intent);
    }

    /**
     * 读取手机联系人列表
     *
     * @param mContext
     * @return
     */
    public static List<HashMap<String, String>> getAllContactInfo(Context mContext) {
        SystemClock.sleep(3000);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        // 1.获取内容解析者
        ContentResolver resolver = mContext.getContentResolver();
        // 2.获取内容提供者的地址:com.android.contacts
        // raw_contacts表的地址 :raw_contacts
        // view_data表的地址 : data
        // 3.生成查询地址
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri date_uri = Uri.parse("content://com.android.contacts/data");
        // 4.查询操作,先查询raw_contacts,查询contact_id
        // projection : 查询的字段
        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"}, null, null, null);
        // 5.解析cursor
        while (cursor != null && cursor.moveToNext()) {
            // 6.获取查询的数据
            String contact_id = cursor.getString(0);
            // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
            // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
            // 判断contact_id是否为空
            if (!TextUtils.isEmpty(contact_id)) {//null   ""
                // 7.根据contact_id查询view_data表中的数据
                // selection : 查询条件
                // selectionArgs :查询条件的参数
                // sortOrder : 排序
                // 空指针: 1.null.方法 2.参数为null
                Cursor c = resolver.query(date_uri, new String[]{"data1",
                                "mimetype"}, "raw_contact_id=?",
                        new String[]{contact_id}, null);
                HashMap<String, String> map = new HashMap<String, String>();
                // 8.解析c
                while (c.moveToNext()) {
                    // 9.获取数据
                    String data1 = c.getString(0);
                    String mimetype = c.getString(1);
                    // 10.根据类型去判断获取的data1数据并保存
                    if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        // 电话
                        map.put("phone", data1);
                    } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                        // 姓名
                        map.put("name", data1);
                    }
                }
                // 11.添加到集合中数据
                list.add(map);
                // 12.关闭cursor
                c.close();
            }
        }
        // 12.关闭cursor
        cursor.close();
        return list;
    }
}

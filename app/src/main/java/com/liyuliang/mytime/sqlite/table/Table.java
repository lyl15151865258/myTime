package com.liyuliang.mytime.sqlite.table;

public class Table {

    /**
     * 保存超级管理员选择公司记录的表
     * Created at 2019/6/10 13:23
     *
     * @author LiYuliang
     * @version 1.0
     */
    public class CompanySearchTable {
        public static final String TABLE_NAME = "Company_record";

        public static final String ID = "id";
        public static final String NAME = "name";

    }

    /**
     * 保存城市选择记录的表
     * Created at 2019/6/10 13:23
     *
     * @author LiYuliang
     * @version 1.0
     */
    public class CitySearchTable {
        public static final String TABLE_NAME = "City_record";

        public static final String ID = "id";
        public static final String NAME = "name";

    }

}

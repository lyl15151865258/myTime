package com.liyuliang.mytime.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 汉字转换位汉语拼音，英文字符不变
 * Created by LiYuliang on 2018/3/26.
 */

public class Cn2Spell {

    public static StringBuffer sb = new StringBuffer();

    /**
     * 获取汉字字符串的首字母，英文字符不变
     * 例如：阿飞→af
     */
    public static String getPinYinHeadChar(String chines) {
        chines = cleanChar(chines);
        sb.setLength(0);
        char[] chars = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chars) {
            if (c > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    public static String getPinYinFirstLetter(String chines) {
        chines = cleanChar(chines);
        sb.setLength(0);
        char c = chines.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     * 例如：阿飞→afei
     */
    public static String getPinYin(String chines) {
        chines = cleanChar(chines);
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : nameChar) {
            if (c > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 清理特殊字符以便得到
     *
     * @param chines
     * @return
     */
    public static String cleanChar(String chines) {
        chines = chines.replaceAll("[\\p{Punct}\\p{Space}]+", ""); // 正则去掉所有字符操作
        // 正则表达式去掉所有中文的特殊符号
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}<>《》【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(chines);
        chines = matcher.replaceAll("").trim();
        return chines;
    }

}

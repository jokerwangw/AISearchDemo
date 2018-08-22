package com.cmcc.cmvideo.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author lhluo
 * @description 数字转大写
 * @date 2018/8/22
 */
public class DigitUtil {
    //截取数字  【读取字符串中第一个连续的字符串，不包含后面不连续的数字】
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        List<Integer> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(Integer.valueOf(matcher.group(0)));
            Log.d("DigitUtil", matcher.group(0));
            return matcher.group(0);
        }
        return "";
    }
}

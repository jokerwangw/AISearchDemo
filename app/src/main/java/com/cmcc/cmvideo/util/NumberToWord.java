package com.cmcc.cmvideo.util;

/**
 * @Author lhluo
 * @description 数字转换类
 * @date 2018/8/21
 */
public class NumberToWord {
    public static String toChinese(String string) {
        String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String[] s2 = { "", "", "", "", "", "", "", "", "", "", "" };

        String result = "";

        int n = string.length();
        for (int i = 0; i < n; i++) {

            int num = string.charAt(i) - '0';

            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
            System.out.println("  "+result);
        }
        return result;

    }
}

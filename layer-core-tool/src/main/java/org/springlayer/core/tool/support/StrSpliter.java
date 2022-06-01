package org.springlayer.core.tool.support;

import java.util.List;

/**
 * 字符串切分器
 */
public class StrSpliter {


    //---------------------------------------------------------------------------------------------- Split by length

    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     */
    public static String[] splitByLength(String str, int len) {
        int partCount = str.length() / len;
        int lastPartCount = str.length() % len;
        int fixPart = 0;
        if (lastPartCount != 0) {
            fixPart = 1;
        }

        final String[] strs = new String[partCount + fixPart];
        for (int i = 0; i < partCount + fixPart; i++) {
            if (i == partCount + fixPart - 1 && lastPartCount != 0) {
                strs[i] = str.substring(i * len, i * len + lastPartCount);
            } else {
                strs[i] = str.substring(i * len, i * len + len);
            }
        }
        return strs;
    }

    //---------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 将字符串加入List中
     *
     * @param list        列表
     * @param part        被加入的部分
     * @param isTrim      是否去除两端空白符
     * @param ignoreEmpty 是否略过空字符串（空字符串不做为一个元素）
     * @return 列表
     */
    private static List<String> addToList(List<String> list, String part, boolean isTrim, boolean ignoreEmpty) {
        part = part.toString();
        if (isTrim) {
            part = part.trim();
        }
        if (false == ignoreEmpty || false == part.isEmpty()) {
            list.add(part);
        }
        return list;
    }

    /**
     * List转Array
     *
     * @param list List
     * @return Array
     */
    private static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }
    //---------------------------------------------------------------------------------------------------------- Private method end
}

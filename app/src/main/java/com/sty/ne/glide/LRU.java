package com.sty.ne.glide;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: ShiTianyi
 * Time: 2021/6/3 0003 19:27
 * Description:
 */
public class LRU {
    public static void main(String[] args) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>(0, 0.75F, true);
        map.put("一", 1); //最先添加的，他的LRU算法移除概率是最高的
        map.put("二", 2);
        map.put("三", 3);
        map.put("四", 4);
        map.put("五", 5); //最后添加的，他的LRU算法移除概率是最低的

        //使用了某个元素
        map.get("三"); //使用了一次，就越不能被回收了

        for(Map.Entry<String, Integer> l : map.entrySet()) {
            System.out.print(l.getValue() + " ");
        }
        System.out.println();
    }
}

package com.sty.ne.glide.resource;

import com.sty.ne.glide.Tool;

/**
 * Author: ShiTianyi
 * Time: 2021/4/21 0021 20:58
 * Description: 唯一的描述
 */
public class Key {
    private String key; // 例如：900d489a4c4c31235dc856b6a982c4e0ae1f53f14dbf04733107b51553562c0a

    /**
     * http://www.ttmd5.com/hash.php?type=9
     *
     * sha256（https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg ）
     * = 900d489a4c4c31235dc856b6a982c4e0ae1f53f14dbf04733107b51553562c0a
     * @param key
     */
    public Key(String key) {
        //this.key = key;
        key = Tool.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

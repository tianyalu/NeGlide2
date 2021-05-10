# 手写Glide框架
[TOC]

## 一、`Glide`手实现之资源封装

* `key` --> 对`value`的唯一性描述
* `value` --> `Bitmap`的封装(+1, -1, 释放)

## 二、`Glide`手实现之活动缓存

* 回收机制：`GC`扫描的时候回收(弱引用)（被动移除）
* 管理方式：资源的封装 `key` （弱引用<`Value`>)
* 手动移除的区分
* 关闭线程

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/active_cache.png)

## 三、`Glide`手实现之内存缓存



## 四、`Glide`手实现之磁盘缓存



## 五、`Glide`手实现之什么周期



## 六、`Glide`手实现之加载图片
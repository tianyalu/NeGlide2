# 手写Glide框架
[TOC]

## 一、`Glide`手写实现之资源封装

* `key` --> 对`value`的唯一性描述
* `value` --> `Bitmap`的封装(+1, -1, 释放)

## 二、`Glide`手写实现之活动缓存

* 回收机制：`GC`扫描的时候回收(弱引用)（被动移除）
* 管理方式：资源的封装 `key` （弱引用<`Value`>)
* 手动移除的区分
* 关闭线程
* 加入`Value`监听

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/active_cache.png)

## 三、`Glide`手写实现之内存缓存

`LRU`算法：最近没有使用的元素，会自动被移除

`LruCache`  `v4`包 <-- `LinkedHashMap<K, V>`  --> 拥有访问排序的功能

职责：

> 活动缓存：给正在使用的资源存储的，弱引用；
>
> 内存缓存：为第二次缓存服务，`LRU`算法

`put`：

> 1. 如果是重复的`key`，会被移除掉一个 --> `entryRemoved`
> 2. `trimToSize`移除那些最近没有使用的元素 --> `entryRemoved`

### 3.1 内存缓存

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/memory_cache.png)

### 3.2 内存缓存和活动缓存的关系

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/memory_active_cache.png)

## 四、`Glide`手写实现之磁盘缓存

磁盘缓存：保存时间比较长，保存在磁盘中，以文件的形式存储。

`LRU`算法：最近没有使用的元素，会自动被移除

> 1. `LruCache`  `v4`包 <-- `LinkedHashMap<K, V>`  --> 拥有访问排序的功能;
> 2. `DiskLruCache` -> `Android` 中没有提供 --> [`DiskLruCache`](https://github.com/JakeWharton/DiskLruCache)

### 4.1 磁盘缓存

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/disk_cache.png)

### 4.2 缓存结构

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/cache_structure.png)

## 五、`Glide`手写实现之生命周期

生命周期的管理：`Application`不能去管理，`FragmentActivity`可以去管理，`Activity`也可以去管理。

管理的方式：在`Activity`组件上附加`Fragment`，通过`Fragment`监听组件的生命周期。

> `Activity --> app Fragment`
>
> `AppCompatActivity --> v4包`

为什么要发送一次`Handler`？：我们的`Android`是基于`Handler`消息的，`LAUNCH_ACTIVITY`，为了让我们的`fragment`不用排队列中，为了下次可以取到。

移除`Handler`。

## 六、`Glide`手写实现之加载图片

组装拼接之前的所有内容（缓存） --> `Glide`

加载资源 --> 缓存 --> 网络/`SD`卡 加载资源 --> 成功后保存到缓存中

![image](https://github.com/tianyalu/NeGlide2/raw/master/show/image_load_process.png)


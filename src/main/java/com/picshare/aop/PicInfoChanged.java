package com.picshare.aop;

/**
 * description:图片的某个资源改变，记录到redis，以便定时任务能将其更新到数据库
 *
 * @author Tao Zheng
 * @date 2020-01-18 0:24
 */
public @interface PicInfoChanged {
}

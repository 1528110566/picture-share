package com.hf6z.controller.config;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-13 0:10
 */
public class Constant {
    public static final String WELCOME_TEACHER = "欢迎您:";
    public static final String WELCOME_DEFAULT = "欢迎你:";
    public static final String WELCOME_ADMIN = "管理员:";
    public static final int HOT_PIC_NUM = 5;
    public static final String ZIPPED_FILE_PREFIX = "_thumbnail";
    public static final String THUMABNAIL_PIC_PREFIX = "_50p_";
    public static final String THREAD_NAME_IN_POOL = "custom_thread_pool_";

    public static final String REDIS_VALUE_KEY_READ_NUM = "pic:read:num:";
    // 图片读取数量排行
    public static final String REDIS_ZSET_KEY_READ_RANK = "pic:read:rank";
    // 图片点赞数量排行
    public static final String REDIS_ZSET_KEY_LIKE_RANK = "pic:like:rank";
    // 图片详情排行
    public static final String REDIS_HASH_KEY = "pic:hash:";
    // 一段时间内更改的图片id
    public static final String REDIS_SET_KEY_CHANGE = "pic:set:change";
    // 图片的评论，格式为pic:comment:picId:username:timestamp[:commentId] ---> detail
    public static final String REDIS_VALUE_KEY_COMMENT = "pic:comment:";
    // 置顶图片
    public static final String REDIS_SET_KEY_TOP = "pic:top";
    // 新年愿望
    public static final String REDIS_VALUE_KEY_WISH = "wish:";
    // 最新保存新年愿望的时间
    public static final String REDIS_VALUE_KEY_LAST_SAVE_DATE = "date";
    // 登录后的用户名保存在这里
    public static final String REDIS_SET_KEY_LOGIN_STATE = "login:state:";
    // 上传排行榜
    public static final String REDIS_ZSET_KEY_UPLOAD_RANK = "pic:upload:rank";
    public static final String REDIS_HASH_KEY_ZIPPED_LINK = "zippedLink";
    public static final String REDIS_HASH_KEY_SOURCE_LINK = "sourceLink";
    public static final String REDIS_HASH_KEY_READ_NUM = "readNum";
    public static final String REDIS_HASH_KEY_LIKE_NUM = "likeNum";
    public static final String REDIS_HASH_KEY_CREATE_USER = "createUser";
    public static final String REDIS_HASH_KEY_CREATE_DATE = "createDate";
    public static final String REDIS_HASH_KEY_DESCRIPTION = "description";

    public static final String REDIS_VALUE_KEY_READ_RECORD = "pic:read:record:";
    public static final String REDIS_VALUE_KEY_LIKE_RECORD = "pic:like:record:";


    public static String FDFS_IP;

    public static String UPLOAD_PIC_PATH;
    public static String ZIPPED_PIC_PATH;

    static {
        if (System.getProperty("os.name").contains("Windows")) {
            UPLOAD_PIC_PATH = "F:/test/";
            ZIPPED_PIC_PATH = "F:/test1/";
            FDFS_IP = "http://192.168.244.11/";
        } else {
            UPLOAD_PIC_PATH = "/root/upload/";
            ZIPPED_PIC_PATH = "/root/zipped/";
            FDFS_IP = "http://106.14.118.135/";
        }
    }

}

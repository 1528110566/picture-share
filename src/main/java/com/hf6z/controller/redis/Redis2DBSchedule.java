package com.hf6z.controller.redis;

import com.hf6z.service.CommentService;
import com.hf6z.service.PicService;
import com.hf6z.service.RedisService;
import com.hf6z.service.WishService;
import com.hf6z.task.CommentTask;
import com.hf6z.task.RedisScheduleTask;
import com.hf6z.task.WishTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:定时任务，将存储在redis的信息更新到数据库
 *
 * @author Tao Zheng
 * @date 2020-01-18 0:08
 */
@Controller
public class Redis2DBSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(Redis2DBSchedule.class);
    @Resource
    private RedisService redisService;
    @Resource
    private PicService picService;
    @Resource
    private CommentService commentService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private WishService wishService;

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void saveToDB() {
        LOGGER.info("开始执行更新计划(Redis:changed ---> DB)");
        threadPoolExecutor.execute(new RedisScheduleTask(0, redisService, picService));
    }

    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 5 * 60 * 1000 + 1234)
    public void saveAllToDB() {
        LOGGER.info("开始执行更新计划(Redis ---> DB)");
        threadPoolExecutor.execute(new RedisScheduleTask(1, redisService, picService));
    }

    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 2 * 60 * 1000 + 4567)
    public void saveCommentToDB() {
        LOGGER.info("开始执行更新计划(Redis:comment ---> DB)");
        threadPoolExecutor.execute(new CommentTask(commentService, redisService));
    }

    @Scheduled(fixedRate = 3 * 60 * 1000, initialDelay = 3 * 60 * 1000 + 2345)
    public void saveWishToDB() {
        LOGGER.info("开始执行更新计划(Redis:wish ---> DB)");
        threadPoolExecutor.execute(new WishTask(wishService, redisService));
    }
}

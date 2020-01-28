package com.picshare.task;

import com.picshare.model.Wish;
import com.picshare.service.RedisService;
import com.picshare.service.WishService;

import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 23:30
 */
public class WishTask implements Runnable {
    private WishService wishService;
    private RedisService redisService;

    public WishTask(WishService wishService, RedisService redisService) {
        this.wishService = wishService;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        List<Wish> wishes = redisService.getWishUnderDate();
        redisService.setLastSavedDate();
        wishService.saveToDB(wishes);
    }
}

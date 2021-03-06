package com.picshare.service.impl;

import com.picshare.dao.WishMapper;
import com.picshare.model.Wish;
import com.picshare.service.RedisService;
import com.picshare.service.WishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 23:26
 */
@Service
public class WishServiceImpl implements WishService {
    @Resource
    private WishMapper wishMapper;
    @Resource
    private RedisService redisService;

    @Override
    public void addWish(Wish wish) {
        redisService.addWish(wish);
    }

    @Override
    public void saveToDB(List<Wish> wishes) {
        for (Wish wish : wishes) {
            wishMapper.insertSelective(wish);
        }
    }

    @Override
    public List<Wish> getWish() {
        return redisService.getWish();
    }
}

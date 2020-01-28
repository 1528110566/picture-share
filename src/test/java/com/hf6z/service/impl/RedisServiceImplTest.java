package com.hf6z.service.impl;

import com.hf6z.BaseTest;
import com.hf6z.service.RedisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-17 23:08
 */
public class RedisServiceImplTest extends BaseTest {
    @Autowired
    private RedisService redisService;

    @Test
    public void addReadNum() {
        redisService.addReadNum(1, "");
    }

    @Test
    public void test() {
        redisService.getHotPicByReadNum(3);
    }

    @Test
    public void testGetAllPic() {
        System.out.println(redisService.getAllPic());
    }

    @Test
    public void test1() {
        System.out.println(redisService.getPictureVoById(321321));
    }

    @Test
    public void test2() {
        redisService.getNextId(1);
    }

    @Test
    public void refreshAllComment() {
        redisService.refreshAllComment();
        System.out.println(System.currentTimeMillis());
    }
}
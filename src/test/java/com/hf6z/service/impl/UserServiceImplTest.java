package com.hf6z.service.impl;

import com.hf6z.BaseTest;
import com.hf6z.service.UserService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-12 23:51
 */
public class UserServiceImplTest extends BaseTest {
    @Resource
    private UserService userService;

    @Test
    public void test() {
        System.out.println(userService.getLessRealName("taozheng"));
    }
}
package com.hf6z.dao;

import com.hf6z.BaseTest;
import com.hf6z.model.User;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-11 21:37
 */

public class UserMapperTest extends BaseTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void selectByPrimaryKey() {
        User user = userMapper.selectByPrimaryKey("taozheng");
        System.out.println(user);
    }
}
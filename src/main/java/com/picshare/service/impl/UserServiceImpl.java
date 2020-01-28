package com.picshare.service.impl;

import com.picshare.dao.UserMapper;
import com.picshare.model.User;
import com.picshare.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.picshare.controller.config.Constant.*;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-12 23:27
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public String getRealName(String username) {
        return userMapper.selectByPrimaryKey(username).getRealName();
    }

    @Override
    public String getLessRealName(String username) {
        String realName = userMapper.selectByPrimaryKey(username).getRealName();
        if (realName.length() > 2) {
            realName = realName.substring(realName.length() - 2);
        }
        return realName;
    }

    @Override
    public User canLogin(User user) {
        User user1 = userMapper.selectByUsernameAndPassword(user);
        if (user1 == null) {
            return null;
        }
        switch (user1.getFlag()) {
            case 0:
                user1.setWelcome(WELCOME_DEFAULT);
                break;
            case 1:
                user1.setWelcome(WELCOME_TEACHER);
                break;
            case 2:
                user1.setWelcome(WELCOME_ADMIN);
                break;
        }
        return user1;
    }
}

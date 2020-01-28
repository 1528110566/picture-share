package com.picshare.service;

import com.picshare.model.User;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-12 23:27
 */
public interface UserService {
    String getRealName(String username);

    String getLessRealName(String username);

    User canLogin(User user);
}

package com.hf6z.controller.login;

import com.hf6z.model.User;
import com.hf6z.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-13 16:40
 */
@Controller
public class LogoutController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);
    @Resource
    private RedisService redisService;

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.removeAttribute("user");
            LOGGER.info("[{}]登出", user.getUsername());
            redisService.removeLoginState(user.getUsername());
        }
        return "redirect:";
    }
}

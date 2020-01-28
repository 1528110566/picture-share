package com.hf6z.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.hf6z.controller.config.Constant;
import com.hf6z.model.User;
import com.hf6z.service.PicService;
import com.hf6z.service.RedisService;
import com.hf6z.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-13 11:40
 */
@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    @Resource
    private UserService userService;
    @Resource
    private PicService picService;
    @Resource
    private RedisService redisService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject login(@Validated User user, HttpSession session) {
        JSONObject object = new JSONObject();
//        System.out.println(user);
        User sessionUser = (User) session.getAttribute("user");
        // 如果session中没有user信息，说明没有登陆
        if (sessionUser == null) {
            User databaseUser = userService.canLogin(user);
//            System.out.println(databaseUser);
            // 如果数据库中有对应的用户名及密码
            if (databaseUser != null) {
                object.put("status", "ok");
                databaseUser.setRealName(getLessRealName(databaseUser.getRealName()));
                session.setAttribute("user", databaseUser);
                object.put("hotPic", picService.getHotPicByReadNum(Constant.HOT_PIC_NUM));
//                response.addCookie(new Cookie("common", UUID.randomUUID().toString()));
                LOGGER.info("[" + databaseUser.getUsername() + "]登录成功");
                redisService.addLoginState(databaseUser.getUsername());
            }
            // 用户名或密码错误
            else {
                object.put("status", "fail");
            }
        }
        return object;
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "redirect:";
    }

    @RequestMapping("/loginCheck")
    @ResponseBody
    public JSONObject loginCheck(HttpSession session) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("user") != null) {
            object.put("status", "true");
        } else {
            object.put("status", "false");
        }
        return object;
    }

    @RequestMapping("/loginNum")
    @ResponseBody
    public JSONObject getLoginNum(HttpSession session) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("user") != null) {
            object.put("status", "ok");
            object.put("num", redisService.getLoginState());
        } else {
            object.put("status", "fail");
        }
        return object;
    }

    private String getLessRealName(String realName) {
        if (realName.length() > 2) {
            return realName.substring(realName.length() - 2);
        }
        return realName;
    }
}

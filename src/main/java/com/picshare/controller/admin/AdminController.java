package com.picshare.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.picshare.model.User;
import com.picshare.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-19 12:24
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    @Resource
    private RedisService redisService;

    @RequestMapping("/refresh")
    @ResponseBody
    public JSONObject refreshCache(HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        // 如果是管理员
        if (user != null && user.getFlag() == 2) {
            redisService.saveAsNeeded();
            int pic = redisService.refreshAllPicVO();
            int comment = redisService.refreshAllComment();
            int top = redisService.refreshAllTopPic();
            int loginState = redisService.refreshAllLoginState();
            redisService.refreshAllUploadRank();
            object.put("status", "ok");
            object.put("picInfo", pic);
            object.put("comment", comment);
            object.put("top", top);
            object.put("loginState", loginState);
            LOGGER.info("[{}]刷新Redis缓存，共{}条图片信息，{}条评论信息，{}条置顶图片信息，{}条登录状态信息",
                    user.getUsername(), pic, comment, top, loginState);
            return object;
        }
        object.put("status", "fail");
        return object;
    }
}

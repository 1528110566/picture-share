package com.picshare.controller.wish;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.picshare.dao.WishMapper;
import com.picshare.model.User;
import com.picshare.model.Wish;
import com.picshare.service.RedisService;
import com.picshare.service.WishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 23:23
 */
@RestController
@RequestMapping("/wish")
public class WishController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WishController.class);
    @Resource
    private WishService wishService;
    @Resource
    private RedisService redisService;
    @Resource
    private WishMapper wishMapper;

    @RequestMapping("/addWish/{wish}")
    public JSONObject addWish(@PathVariable("wish") String wish, HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null && wish.length() > 0) {
            Wish wish1 = new Wish();
            wish1.setUsername(user.getUsername());
            wish1.setCreateDate(new Date());
            wish1.setDetail(wish);
            wishService.addWish(wish1);
            LOGGER.info("[{}]发布新年愿望：{}", user.getUsername(), wish);
            object.put("status", "ok");
        } else {
            object.put("status", "fail");
        }
        return object;
    }

    @RequestMapping("/getWish")
    public JSONObject getWish(HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Wish> wishList = wishService.getWish();
            JSONArray array = new JSONArray();
            array.addAll(wishList);
            object.put("wishes", array);
            object.put("status", "ok");
        } else {
            object.put("status", "fail");
        }
        return object;
    }

    @RequestMapping("/setWish")
    public JSONObject setWish(HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Wish> wishList = redisService.getWishUnderDate();
            if (wishList.size() < 2) {
                wishList.addAll(wishMapper.getTop2Wish());
            }
            wishList.addAll(wishMapper.getTeachers());
            Set<Wish> set = new HashSet<>(wishList);
            wishList = new LinkedList<>(set);
            Collections.sort(wishList);
            object.put("wishes", wishList);
            object.put("status", "ok");
        } else {
            object.put("status", "fail");
        }
        return object;
    }
}

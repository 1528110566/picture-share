package com.hf6z.controller.comment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hf6z.controller.config.Constant;
import com.hf6z.model.Comment;
import com.hf6z.model.User;
import com.hf6z.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 12:17
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    @Resource
    private CommentService commentService;

    @RequestMapping("/getComment/{id}")
    @ResponseBody
    public JSONObject getCommentByPicId(@PathVariable int id, HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            JSONArray array = new JSONArray();
            LOGGER.info("[{}]查询[{}]的评论", user.getUsername(), id);
            List<Comment> list = commentService.getCommentByPicId(id);
            for (Comment comment : list) {
                if (comment.getId() == null) {
                    comment.setId(0);
                }
                comment.setDeleteLink("/comment/delComment/" + comment.getId() + "/" + comment.getPicid() + "/" + comment.getUsername() + "/" + getDate(comment.getCreateDate()));
                // 如果是评论者或者是管理员则可以显示删除按钮
                if (comment.getUsername().equals(user.getUsername())
                        || user.getFlag() == 2) {
                    comment.setCheck(true);
                } else {
                    comment.setCheck(false);
                }
            }
            array.addAll(list);
            object.put("comments", array);
            object.put("status", "ok");
        } else {
            object.put("status", "fail");
        }
        return object;
    }

    private long getDate(Date date) {
        return date.getTime();
    }

    @RequestMapping(value = "/addComment/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addComment(@PathVariable int id, @RequestParam("comment") String comment, HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            object.put("status", "ok");
            Comment comment1 = new Comment();
            comment1.setCreateDate(new Date());
            comment1.setUsername(user.getUsername());
            comment1.setDetail(comment);
            comment1.setPicid(id);
            commentService.addComment(comment1);
            LOGGER.info("[{}]评论[{}]：{}", user.getUsername(), id, comment);
        } else {
            object.put("status", "fail");
        }
        return object;
    }

    @RequestMapping("delComment/{id}/{picId}/{username}/{timestamp}")
//    @ResponseBody
    public String delComment(@PathVariable("id") int id,
                             @PathVariable("picId") int picId,
                             @PathVariable("username") String username,
                             @PathVariable("timestamp") String timestamp,
                             HttpSession session, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (user.getUsername().equals(username) || user.getFlag() == 2) {
                // id=0说明当前评论是从redis中加载的，没有对应的id
                if (id == 0) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Long temp = Long.parseLong(timestamp);
                        String dateStr = simpleDateFormat.format(temp);
                        Date date = simpleDateFormat.parse(dateStr);
                        timestamp = timestamp.substring(0, timestamp.length() - 3);
                        String key = Constant.REDIS_VALUE_KEY_COMMENT + picId + ":" + username + ":" + timestamp;
                        commentService.delComment(key, id, picId, username, date);
                        object.put("status", "ok");
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                        LOGGER.error(Arrays.toString(e.getStackTrace()));
                        object.put("status", "fail");
                    }
                } else {
                    String key = Constant.REDIS_VALUE_KEY_COMMENT + picId + ":" + username + ":" + timestamp + ":" + id;
                    commentService.delComment(key, id, picId, username, new Date());
                    object.put("status", "ok");
                }
            } else {
                object.put("status", "fail");
            }
        } else {
            object.put("status", "fail");
        }
        String ref = request.getHeader("Referer");
        String[] temp = ref.split("/");
        return "redirect:/" + temp[3] + "/" + temp[4] + "/" + temp[5];
    }
}

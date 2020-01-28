package com.hf6z.service.impl;

import com.hf6z.dao.CommentMapper;
import com.hf6z.model.Comment;
import com.hf6z.service.CommentService;
import com.hf6z.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 11:11
 */
@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private RedisService redisService;

    @Override
    public List<Comment> getAllComment() {
        return commentMapper.getAll();
    }

    @Override
    public List<Comment> getCommentByPicId(int id) {
        List<Comment> list = redisService.getCommentByPicId(id);
        if (list.size() == 0) {
            LOGGER.warn("请求[{}]的评论，出现缓存穿透", id);
            list = commentMapper.getByPicId(id);
            if (list.size() != 0) {
                // 放入Redis中
                redisService.addComment(list);
            }
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List<Comment> getCommentByUsername(String username) {
        return commentMapper.getByUsername(username);
    }

    @Override
    public void insertSelective(Comment comment) {
        commentMapper.insertSelective(comment);
    }

    @Override
    public void insertSelective(List<Comment> list) {
        for (Comment comment : list) {
            commentMapper.insertSelective(comment);
        }
    }

    @Override
    public void addComment(Comment comment) {
        redisService.addComment(comment);
    }

    @Override
    public Integer getMaxId() {
        return commentMapper.getMaxId();
    }

    @Override
    public List<Comment> getCommentRangeById(int currentId, int maxId) {
        return commentMapper.getCommentRangeById(currentId, maxId);
    }

    @Override
    public void delComment(String key, int id, int picId, String username, Date date) {
        redisService.deleteCommentInRedis(key);
        if (id != 0) {
            commentMapper.deleteByPrimaryKey(id);
        } else {
            commentMapper.deleteByOther(picId, username, date);
        }
    }
}

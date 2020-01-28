package com.hf6z.task;

import com.hf6z.model.Comment;
import com.hf6z.service.CommentService;
import com.hf6z.service.RedisService;

import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 11:55
 */
public class CommentTask implements Runnable {
    private CommentService commentService;
    private RedisService redisService;

    public CommentTask(CommentService commentService, RedisService redisService) {
        this.commentService = commentService;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        Integer currentId = commentService.getMaxId();
        if (currentId == null) {
            currentId = 0;
        }
        List<Comment> list = redisService.getAllCommentOnlyInRedis();
        // 将这些评论存到数据库中
        commentService.insertSelective(list);
        Integer maxId = commentService.getMaxId();
        if (maxId == null) {
            maxId = 0;
        }
        // 这些是新存入的评论，将这些再存入Redis中
        List<Comment> list1 = commentService.getCommentRangeById(currentId, maxId);
        redisService.addComment(list1);
    }
}

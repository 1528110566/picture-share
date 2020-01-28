package com.picshare.service;

import com.picshare.model.Comment;

import java.util.Date;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 11:09
 */
public interface CommentService {
    List<Comment> getAllComment();

    List<Comment> getCommentByPicId(int id);

    List<Comment> getCommentByUsername(String username);

    void insertSelective(Comment comment);

    void insertSelective(List<Comment> list);

    void addComment(Comment comment);

    Integer getMaxId();

    List<Comment> getCommentRangeById(int currentId, int maxId);

    void delComment(String key, int id, int picId, String username, Date date);
}

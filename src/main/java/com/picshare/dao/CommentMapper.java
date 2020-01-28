package com.picshare.dao;

import com.picshare.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    List<Comment> getAll();

    List<Comment> getByPicId(int id);

    List<Comment> getByUsername(String username);

    Integer getMaxId();

    List<Comment> getCommentRangeById(int currentId, int maxId);

    void deleteByOther(int picId, String username, Date date);
}
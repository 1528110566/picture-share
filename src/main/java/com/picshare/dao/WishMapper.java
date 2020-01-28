package com.picshare.dao;

import com.picshare.model.Wish;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Wish record);

    int insertSelective(Wish record);

    Wish selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Wish record);

    int updateByPrimaryKey(Wish record);

    List<Wish> getTop2Wish();

    List<Wish> getTeachers();
}
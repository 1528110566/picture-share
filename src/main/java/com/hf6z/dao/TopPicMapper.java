package com.hf6z.dao;

import com.hf6z.model.TopPic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopPicMapper {
    int insert(TopPic record);

    int insertSelective(TopPic record);

    List<TopPic> getAll();
}
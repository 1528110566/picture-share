package com.hf6z.dao;

import com.hf6z.model.LikeRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRecordMapper {
    int insert(LikeRecord record);

    int insertSelective(LikeRecord record);
}
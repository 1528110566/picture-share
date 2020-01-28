package com.picshare.dao;

import com.picshare.model.LikeRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRecordMapper {
    int insert(LikeRecord record);

    int insertSelective(LikeRecord record);
}
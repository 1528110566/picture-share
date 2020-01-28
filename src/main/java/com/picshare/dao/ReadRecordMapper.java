package com.picshare.dao;

import com.picshare.model.ReadRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadRecordMapper {
    int insert(ReadRecord record);

    int insertSelective(ReadRecord record);
}
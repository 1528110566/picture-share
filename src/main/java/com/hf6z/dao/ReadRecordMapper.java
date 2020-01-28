package com.hf6z.dao;

import com.hf6z.model.ReadRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadRecordMapper {
    int insert(ReadRecord record);

    int insertSelective(ReadRecord record);
}
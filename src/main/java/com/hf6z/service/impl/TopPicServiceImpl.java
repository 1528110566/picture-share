package com.hf6z.service.impl;

import com.hf6z.dao.TopPicMapper;
import com.hf6z.model.TopPic;
import com.hf6z.service.TopPicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 19:22
 */
@Service
public class TopPicServiceImpl implements TopPicService {
    @Resource
    private TopPicMapper topPicMapper;

    @Override
    public List<TopPic> getAll() {
        return topPicMapper.getAll();
    }
}

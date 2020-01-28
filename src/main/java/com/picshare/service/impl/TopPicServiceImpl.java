package com.picshare.service.impl;

import com.picshare.dao.TopPicMapper;
import com.picshare.model.TopPic;
import com.picshare.service.TopPicService;
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

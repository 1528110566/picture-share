package com.hf6z.task;

import com.hf6z.service.PicService;
import com.hf6z.service.RedisService;
import com.hf6z.vo.PictureVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-18 14:03
 */
public class RedisScheduleTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisScheduleTask.class);
    // 0表示更新change部分，1表示更新全部
    private int flag;
    private RedisService redisService;
    private PicService picService;

    public RedisScheduleTask(int flag, RedisService redisService, PicService picService) {
        this.flag = flag;
        this.redisService = redisService;
        this.picService = picService;
    }

    @Override
    public void run() {
        if (flag == 0) {
            saveChanged();
        } else if (flag == 1) {
            saveAll();
        }
    }

    private void saveChanged() {
        Set<String> ids = redisService.getAllChangedPicId();
        // 获取后立即删除
        redisService.clearChangedSet();
        if (ids == null || ids.size() == 0) {
//            LOGGER.info("当前时间段没有更新的图片数据");
        } else {
            for (String id : ids) {
                PictureVO pictureVO = redisService.getPictureVoById(Integer.parseInt(id));
                picService.updateByVO(pictureVO);
            }
            LOGGER.info("更新完成，共更新{}条pic信息", ids.size());
        }
    }

    private void saveAll() {
        List<PictureVO> list = redisService.getAllPic();
        for (PictureVO pictureVO : list) {
            picService.updateByVO(pictureVO);
        }
//        LOGGER.info("图片信息全部更新完成");
    }
}

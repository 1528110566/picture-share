package com.hf6z.service.impl;

import com.hf6z.dao.PictureMapper;
import com.hf6z.fdfs.FastDFSClientService;
import com.hf6z.model.Picture;
import com.hf6z.service.PicService;
import com.hf6z.service.RedisService;
import com.hf6z.task.PicTask;
import com.hf6z.vo.PictureVO;
import com.hf6z.vo.UploadRankVO;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.hf6z.controller.config.Constant.*;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-14 22:55
 */
@Service
public class PicServiceImpl implements PicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PicServiceImpl.class);

    public PicServiceImpl(PictureMapper pictureMapper) {
        this.pictureMapper = pictureMapper;
    }

    @Override
    public void zip(File file) throws IOException {
        thumbnail(file.getAbsolutePath(), "F:\\a编程\\hf6z\\test", 1.0f);
    }

    @Override
    public String zip(String filePath) throws IOException {
        String filename = filePath.substring(UPLOAD_PIC_PATH.length(), filePath.lastIndexOf("."));
        String zippedFilePath = ZIPPED_PIC_PATH + filename + ZIPPED_FILE_PREFIX + ".jpg";
        thumbnail(filePath, zippedFilePath, 1.0f);
        LOGGER.info("压缩成功：" + filePath + "--->" + zippedFilePath);
        return zippedFilePath;
    }

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private RedisService redisService;
    @Resource
    private FastDFSClientService service;

    @Override
    public void saveToDB(List<String> files, HttpSession session) {
        threadPoolExecutor.execute(new PicTask(files, this, session, service, redisService));
    }

    private final PictureMapper pictureMapper;

    @Override
    public void insertSelective(Picture picture) {
        pictureMapper.insertSelective(picture);
    }

    @Override
    public List<PictureVO> getHotPicByReadNum(int num) {
        List<PictureVO> topPicInRedis = redisService.getTopPicVO();
        Collections.sort(topPicInRedis);
        List<PictureVO> listInRedis = redisService.getHotPicByReadNum(num);
        Set<PictureVO> set = new LinkedHashSet<>();
        set.addAll(topPicInRedis);
        set.addAll(listInRedis);
        List<PictureVO> listInDB = new LinkedList<>();
        // 如果redis没有数据或者数据的数量不够，则从数据库中取
        if (set.size() < HOT_PIC_NUM + topPicInRedis.size()) {
            listInDB = pictureMapper.getHotPicVOByReadNum(num);
            if (listInDB.size() != 0) {
                redisService.putVO2Redis(listInDB);
            }
        }
        set.addAll(listInDB);
        return new LinkedList<>(set);
    }

    @Override
    public Picture getPictureById(int id) {
        return pictureMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PictureVO> getAllPic() {
        return pictureMapper.getAllPic();
    }

    @Override
    public List<PictureVO> getAllPicByUserName(String username) {
        return pictureMapper.getAllPicByUserName(username);
    }

    @Override
    public void updateByVO(PictureVO pictureVO) {
        pictureMapper.updateByVO(pictureVO);
    }

    @Override
    public String getIdByZippedLink(String zippedLink) {
        return pictureMapper.getIdByZippedLink(zippedLink);
    }

    @Override
    public PictureVO getPicVOById(int id) {
        return pictureMapper.getPicVOById(id);
    }

    @Override
    public int getNextId(int id) {
        Integer nextId = redisService.getNextId(id);
        if (nextId == 0) {
            nextId = pictureMapper.getNextId(id);
            if (nextId == null) {
                nextId = pictureMapper.getMaxId();
                if (nextId == null) {
                    return 0;
                }
            }
        }
        return nextId;
    }

    @Override
    public int getPreId(int id) {
        Integer preId = redisService.getPreId(id);
        if (preId == 0) {
            preId = pictureMapper.getPreId(id);
            if (preId == null) {
                preId = pictureMapper.getMinId();
                if (preId == null) {
                    return 0;
                }
            }
        }
        return preId;
    }

    @Override
    public boolean deleteById(int id) {
        Boolean bool = redisService.deleteVOById(id);
        return (bool != null && bool) && pictureMapper.deleteByPrimaryKey(id) == 1;
    }

    @Override
    public List<UploadRankVO> getUploadRank() {
        return pictureMapper.getUploadRank();
    }


    /**
     * 压缩图片
     *
     * @param srcImagePath 源图片路径
     * @param desImagePath 目标路径
     * @param scale        压缩率
     * @throws IOException the io exception
     */
    public static void thumbnail(String srcImagePath, String desImagePath, double scale) throws IOException {
        Thumbnails.of(srcImagePath).scale(scale).toFile(desImagePath);
    }
}

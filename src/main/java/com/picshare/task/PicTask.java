package com.picshare.task;

import com.picshare.fdfs.FastDFSClientService;
import com.picshare.model.Picture;
import com.picshare.model.User;
import com.picshare.service.PicService;
import com.picshare.service.RedisService;
import com.picshare.service.impl.PicServiceImpl;
import com.picshare.vo.PictureVO;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.picshare.controller.config.Constant.FDFS_IP;
import static com.picshare.controller.config.Constant.UPLOAD_PIC_PATH;

/**
 * description:将用户上传的图片压缩并将相关信息存放至数据库
 *
 * @author Tao Zheng
 * @date 2020-01-15 21:27
 */
public class PicTask implements Runnable {
    private List<String> files;
    private PicService picService;
    private HttpSession session;
    private FastDFSClientService fastDFSClientService;
    private RedisService redisService;

    public PicTask(List<String> files, PicServiceImpl picService,
                   HttpSession session, FastDFSClientService fastDFSClientService,
                   RedisService redisService) {
        this.files = files;
        this.picService = picService;
        this.session = session;
        this.fastDFSClientService = fastDFSClientService;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        List<Picture> list = new ArrayList<>(files.size());
        for (String filePath : files) {
            try {
                String zippedFilePath = picService.zip(filePath);
                User user = (User) session.getAttribute("user");
                String description = getDescription(filePath);
                assert user != null;
                Picture picture = new Picture();
                picture.setCreateUser(user.getUsername());
                picture.setSourceLocation(filePath);
                picture.setZippedLocation(zippedFilePath);
                picture.setFlag(1);
                picture.setDescription(description);
//                picService.insertSelective(picture);
                String sourceLink = FDFS_IP + fastDFSClientService.uploadImage(user.getUsername(), filePath);
                String zippedLink = FDFS_IP + fastDFSClientService.uploadImage(user.getUsername(), zippedFilePath);
                picture.setSourceLink(sourceLink);
                picture.setZippedLink(zippedLink);
//                System.out.println(picture);
                picService.insertSelective(picture);
                list.add(picture);
                redisService.addUploadRank(user.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        putToRedis(list);
    }

    private String getDescription(String filePath) {
        String oriDescription = filePath.substring(UPLOAD_PIC_PATH.length());
        String ext = FilenameUtils.getExtension(oriDescription);
        String temp = oriDescription.substring(0, oriDescription.lastIndexOf("."));
        return oriDescription.substring(0, temp.lastIndexOf("_")) + "." + ext;
    }

    private void putToRedis(List<Picture> pictures) {
        List<PictureVO> pictureVOList = new ArrayList<>(pictures.size());
        for (Picture picture : pictures) {
            PictureVO pictureVO = new PictureVO();
            String id = picService.getIdByZippedLink(picture.getZippedLink());
            pictureVO.setId(id);
            pictureVO.setCreateUser(picture.getCreateUser());
            pictureVO.setCreateDate(getDate());
            pictureVO.setDescription(picture.getDescription());
            pictureVO.setZippedLink(picture.getZippedLink());
            pictureVO.setSourceLink(picture.getSourceLink());
            pictureVO.setReadNum(String.valueOf(picture.getReadNum()));
            pictureVO.setLikeNum(String.valueOf(picture.getLikeNum()));
            pictureVOList.add(pictureVO);
        }
        redisService.putVO2Redis(pictureVOList);
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}

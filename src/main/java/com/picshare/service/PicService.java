package com.picshare.service;

import com.picshare.model.Picture;
import com.picshare.vo.PictureVO;
import com.picshare.vo.UploadRankVO;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-14 22:55
 */
public interface PicService {
    void zip(File file) throws IOException;

    String zip(String filePath) throws IOException;

    void saveToDB(List<String> files, HttpSession session);

    void insertSelective(Picture picture);

    List<PictureVO> getHotPicByReadNum(int num);

    Picture getPictureById(int id);

    List<PictureVO> getAllPic();

    List<PictureVO> getAllPicByUserName(String username);

    void updateByVO(PictureVO pictureVO);

    String getIdByZippedLink(String zippedLink);

    PictureVO getPicVOById(int id);

    int getNextId(int id);

    int getPreId(int id);

    boolean deleteById(int id);

    List<UploadRankVO> getUploadRank();
}

package com.picshare.dao;

import com.picshare.model.Picture;
import com.picshare.vo.PictureVO;
import com.picshare.vo.UploadRankVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Picture record);

    int insertSelective(Picture record);

    Picture selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Picture record);

    int updateByPrimaryKey(Picture record);

    List<Picture> getHotPicByReadNum(int num);

    List<PictureVO> getHotPicVOByReadNum(int num);

    List<Picture> getHotPicByLikeNum(int num);

    List<PictureVO> getHotPicVOByLikeNum(int num);

    List<PictureVO> getAllPic();

    void updateByVO(PictureVO pictureVO);

    String getIdByZippedLink(String zippedLink);

    PictureVO getPicVOById(int id);

    List<PictureVO> getAllPicByUserName(String username);

    Integer getNextId(int id);

    Integer getPreId(int id);

    Integer getMaxId();

    Integer getMinId();

    List<UploadRankVO> getUploadRank();
}
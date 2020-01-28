package com.hf6z.service;

import com.hf6z.model.Comment;
import com.hf6z.model.Wish;
import com.hf6z.vo.PictureVO;
import com.hf6z.vo.UploadRankVO;

import java.util.List;
import java.util.Set;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-17 8:29
 */
public interface RedisService {
    void addReadNum(int picId, String username);

    void addLikeNum(int picId, String username);

    String getPicZippedLink(int picId);

    String getPicSourceLink(int picId);

    List<PictureVO> getHotPicByReadNum(int num);

    void addChangedPic(int picId);

    Set<String> getAllChangedPicId();

    PictureVO getPictureVoById(int picId);

    List<PictureVO> getTopPicVO();

    void clearChangedSet();

    void putVO2Redis(List<PictureVO> list);

    List<PictureVO> getAllPic();

    int refreshAllPicVO();

    int refreshAllComment();

    int refreshAllTopPic();

    void refreshAllUploadRank();

    List<String> getAllReadRecord();

    List<String> getAllLikeRecord();

    void updateVO(int id, String desc);

    int getNextId(int id);

    int getPreId(int id);

    Boolean deleteVOById(int id);

    List<Comment> getCommentByPicId(int id);

    void deleteCommentInRedis(String key);

    List<Comment> getAllCommentOnlyInRedis();

    void addComment(Comment comment);

    void addComment(List<Comment> list);

    void saveAsNeeded();

    void addWish(Wish wish);

    List<Wish> getWishUnderDate();

    void setLastSavedDate();

    List<Wish> getWish();

    void removeLoginState(String username);

    void addLoginState(String username);

    Integer getLoginState();

    int refreshAllLoginState();

    void addUploadRank(String username);

    List<UploadRankVO> getUploadRank();
}

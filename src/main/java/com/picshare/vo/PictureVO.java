package com.picshare.vo;

import lombok.Data;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-17 8:36
 */
@Data
public class PictureVO implements Comparable<PictureVO> {
    private String id;
    private String createUser;
    private String createDate;
    private String zippedLink;
    private String sourceLink;
    private String likeNum;
    private String readNum;
    private String description;
    public static final int argumentNum = 7;

    @Override
    public int compareTo(PictureVO o) {
        return Integer.parseInt(o.getReadNum()) - Integer.parseInt(this.getReadNum());
    }
}

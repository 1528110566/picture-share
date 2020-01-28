package com.picshare.vo;

import lombok.Data;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 13:08
 */
@Data
public class CommentVO {
    private String id;

    private String username;

    private Integer picid;

    private String detail;

    private String createDate;
}

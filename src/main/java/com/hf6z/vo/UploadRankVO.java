package com.hf6z.vo;

import lombok.Data;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-21 13:38
 */
@Data
public class UploadRankVO implements Comparable<UploadRankVO> {
    private String username;
    private int uploadNum;

    @Override
    public int compareTo(UploadRankVO o) {
        return o.getUploadNum() - this.uploadNum;
    }
}

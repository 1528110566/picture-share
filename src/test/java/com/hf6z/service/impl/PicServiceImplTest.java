package com.hf6z.service.impl;

import com.hf6z.BaseTest;
import com.hf6z.controller.config.Constant;
import com.hf6z.service.PicService;
import com.hf6z.vo.PictureVO;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-14 23:02
 */
public class PicServiceImplTest extends BaseTest {
    @Resource
    private PicService picService;

    @Test
    public void zip() throws IOException {
        picService.zip(new File("20141231_161858.jpg"));
    }

    @Test
    public void test() throws IOException {
        picService.zip("F:/test/20141231_161858_1579092959863.jpg");
    }

    @Test
    public void test1() {
        List<PictureVO> list = picService.getHotPicByReadNum(Constant.HOT_PIC_NUM);
        for (PictureVO picture : list) {
            System.out.print(picture.getId() + "\t");
            System.out.print(picture.getDescription() + "\t");
            System.out.println(picture.getLikeNum());
        }
    }
}

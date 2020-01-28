package com.hf6z.fdfs;

import com.hf6z.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-15 23:51
 */
public class FastDFSClientServiceTest extends BaseTest {
    @Autowired
    private FastDFSClientService service;

    @Test
    public void upload() throws FileNotFoundException {
        service.upload(new File("F:/test/20141231_161858.jpg"), "taozheng");
    }

    @Test
    public void test() {
        service.uploadImage("taozheng", "F:/test/20141231_161858.jpg");
    }
}
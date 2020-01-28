package com.hf6z.utils;

import org.junit.Test;

import java.io.IOException;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-14 23:16
 */
public class ImageUtilsTest {
    @Test
    public void test() throws IOException {
        ImageUtils.generateDirectoryThumbnail("E:\\test");
    }
}
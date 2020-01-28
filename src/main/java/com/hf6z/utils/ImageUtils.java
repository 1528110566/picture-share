package com.hf6z.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description:图片压缩类
 * 来源:https://www.cnblogs.com/zuidongfeng/p/9853335.html
 *
 * @author Tao Zheng
 * @date 2020-01-14 23:13
 */
public class ImageUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);
    // 图片默认缩放比率
    private static final double DEFAULT_SCALE = 1d;

    // 缩略图后缀
    private static final String SUFFIX = "-thumbnail";


    /**
     * 生成缩略图到指定的目录
     *
     * @param path  目录
     * @param files 要生成缩略图的文件列表
     */
    public static List<String> generateThumbnail2Directory(String path, String... files) throws IOException {
        return generateThumbnail2Directory(DEFAULT_SCALE, path, files);
    }

    /**
     * 生成缩略图到指定的目录
     *
     * @param scale    图片缩放率
     * @param pathname 缩略图保存目录
     * @param files    要生成缩略图的文件列表
     */
    public static List<String> generateThumbnail2Directory(double scale, String pathname, String... files) throws IOException {
        Thumbnails.of(files)
                // 图片缩放率，不能和size()一起使用
                .scale(scale)
                // 缩略图保存目录,该目录需存在，否则报错
                .toFiles(new File(pathname), Rename.SUFFIX_HYPHEN_THUMBNAIL);
        LOGGER.info(files[0] + "压缩成功");
        List<String> list = new ArrayList<>(files.length);
        for (String file : files) {
            list.add(appendSuffix(file, SUFFIX));
        }
        return list;
    }

    /**
     * 将指定目录下所有图片生成缩略图
     *
     * @param pathname 文件目录
     */
    public static void generateDirectoryThumbnail(String pathname) throws IOException {
        generateDirectoryThumbnail(pathname, DEFAULT_SCALE);
    }

    /**
     * 将指定目录下所有图片生成缩略图
     *
     * @param pathname 文件目录
     */
    public static void generateDirectoryThumbnail(String pathname, double scale) throws IOException {
        File[] files = new File(pathname).listFiles();
        assert files != null;
        compressRecurse(files, pathname);
    }

    /**
     * 文件追加后缀
     *
     * @param fileName 原文件名
     * @param suffix   文件后缀
     */
    public static String appendSuffix(String fileName, String suffix) {
        String newFileName;

        int indexOfDot = fileName.lastIndexOf('.');

        if (indexOfDot != -1) {
            newFileName = fileName.substring(0, indexOfDot);
            newFileName += suffix;
            newFileName += fileName.substring(indexOfDot);
        } else {
            newFileName = fileName + suffix;
        }

        return newFileName;
    }


    private static void compressRecurse(File[] files, String pathname) throws IOException {
        for (File file : files) {
            // 目录
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                assert subFiles != null;
                compressRecurse(subFiles, pathname + File.separator + file.getName());
            } else {
                // 文件包含压缩文件后缀或非图片格式，则不再压缩
                String extension = getFileExtention(file.getName());
                if (!file.getName().contains(SUFFIX) && isImage(extension)) {
                    generateThumbnail2Directory(pathname, file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 根据文件扩展名判断文件是否图片格式
     */
    public static boolean isImage(String extension) {
        String[] imageExtension = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};
        for (String e : imageExtension) {
            if (extension.toLowerCase().equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得文件的拓展名
     */
    public static String getFileExtention(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

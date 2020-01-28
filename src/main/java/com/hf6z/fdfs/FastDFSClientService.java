package com.hf6z.fdfs;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.upload.FastImageFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.hf6z.controller.config.Constant;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-15 23:03
 */
@Component
public class FastDFSClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSClientService.class);
    private final FastFileStorageClient storageClient;

    public FastDFSClientService(FastFileStorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Deprecated
    public void upload(File file, String username) throws FileNotFoundException {
        InputStream in = new FileInputStream(file);
        Set<MetaData> metaDataSet = createMetaData(username);
        String fileExtName = FilenameUtils.getExtension(file.getName());

        FastImageFile fastImageFile = new FastImageFile.Builder()
                .withFile(in, file.length(), fileExtName)
                .withMetaData(metaDataSet)
                .withThumbImage(50)
                .build();
        storageClient.uploadImage(fastImageFile);

    }

    private Set<MetaData> createMetaData(String username) {
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("Author", username));
        metaDataSet.add(new MetaData("CreateDate", getCurrentDate()));
        return metaDataSet;
    }

    private String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public String uploadImage(String username, String filePath) {
        LOGGER.info("[{}]上传文件{}至FDFS", username, filePath);
        Set<MetaData> metaData = createMetaData(username);
        StorePath path = uploadImage(filePath, metaData);
        assert path != null;
//        LOGGER.info("path:{}---->link:{}", filePath, path.getFullPath());
        return path.getFullPath();
    }

    private StorePath uploadImage(String filePath, Set<MetaData> metaDataSet) {
        InputStream in = null;
        File file = new File(filePath);
        String fileExtName = FilenameUtils.getExtension(file.getName());
//        LOGGER.info("fileExtName = " + fileExtName);
//        long fileSize = file.length();
//        LOGGER.info("fileSize = " + fileSize);
        try {
            FastImageFile fastImageFile;
            in = new FileInputStream(file);
            // 只对压缩图片生成缩略图
            if (filePath.contains(Constant.ZIPPED_FILE_PREFIX)) {
                fastImageFile = new FastImageFile.Builder()
                        .withFile(in, file.length(), fileExtName)
                        .withMetaData(metaDataSet)
                        .withThumbImage(0.5)
                        .build();
            } else {
                fastImageFile = new FastImageFile.Builder()
                        .withFile(in, file.length(), fileExtName)
                        .withMetaData(metaDataSet)
                        .build();
            }
            return storageClient.uploadImage(fastImageFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

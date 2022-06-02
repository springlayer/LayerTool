package org.springlayer.core.oss.helper;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import org.springlayer.core.oss.object.OssObject;
import org.springlayer.core.tool.snow.SnowFlake;
import org.springlayer.core.tool.utils.StringUtil;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Hzhi
 * @Date 2022-05-30 15:06
 * @description
 **/
@Component
@RequiredArgsConstructor
public class OssHelper {

    @Resource
    private MinioClient minioClient;

    /**
     * 查看存储bucket是否存在
     *
     * @param bucketName 桶名
     * @return Boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *
     * @param bucketName 存储bucket名称
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 批量上传文件
     *
     * @param files      多文件
     * @param bucketName 桶名
     * @param anonymous  是否匿名文件上传,不匿名上传，相同文件名会覆盖原文件
     * @return List<OssObject>
     */
    public List<OssObject> uploadBatch(MultipartFile[] files, String bucketName, Boolean anonymous) {
        List<OssObject> ossObjectList = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                OssObject upload = upload(files[i], bucketName, anonymous);
                ossObjectList.add(upload);
            }
            return ossObjectList;
        } catch (Exception e) {
            e.printStackTrace();
            return ossObjectList;
        }
    }

    /**
     * ⽂件上传
     *
     * @param file       ⽂件
     * @param bucketName 存储bucket
     * @param anonymous  是否匿名文件上传,不匿名上传，相同文件名会覆盖原文件
     * @return Boolean
     */
    public OssObject upload(MultipartFile file, String bucketName, Boolean anonymous) {
        try {
            String objectName = StringUtil.NULLSTR;
            if (anonymous) {
                objectName = SnowFlake.nextId() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            } else {
                objectName = file.getOriginalFilename();
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(objectArgs);
            return OssObject.builder().objectName(objectWriteResponse.object()).originalName(file.getOriginalFilename()).fileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))).bucketName(bucketName).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName 桶名
     * @param fileName   文件名称
     * @param res
     */
    public void download(String bucketName, String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                //设置强制下载不打开
                res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览图片
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return String
     */
    public String preview(String bucketName, String fileName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(bucketName).object(fileName).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    /**
     * 移除文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return boolean
     */
    public boolean remove(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 批量删除文件对象
     *
     * @param bucketName 桶名
     * @param objects    对象集合
     * @return Iterable<Result < DeleteError>>
     */
    public Iterable<Result<DeleteError>> removeBatch(String bucketName, List<String> objects) {
        List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
        return results;
    }
}
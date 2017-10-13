package com.skg.smodel.core.util;

import com.aliyun.oss.OSSClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/8/31.
 */
public class MultipartUpload {

    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "maTnALCpSvWjxyAy";
    private static String accessKeySecret = "0Ou6P67WhuSHESKrwJClFqCKo5BuBf";
    private static String bucketName = "diich-resource";

    protected static OSSClient client = null;

    public static boolean fileUpload(MultipartFile file, String key) {

        // 创建一个可重用固定线程数的线程池。若同一时间线程数大于10，则多余线程会放入队列中依次执行
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //String key = "video/project/" + file.getOriginalFilename(); // 获取上传文件的名称，作为在OSS上的文件名
        // 创建OSSClient实例
        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            String uploadId = UploadPart.claimUploadId(bucketName, key);
            // 设置每块为 5M(除最后一个分块以外，其他的分块大小都要大于5MB)
            final long partSize = 5 * 1024 * 1024L;
            // 计算分块数目
            long fileLength = file.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }

            // 分块 号码的范围是1~10000。如果超出这个范围，OSS将返回InvalidArgument的错误码。
            if (partCount > 10000) {
                throw new RuntimeException("文件过大(分块大小不能超过10000)");
            }

            /**
             * 将分好的文件块加入到list集合中
             */
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;

                // 线程执行。将分好的文件块加入到list集合中
                executorService.execute(new UploadPart(file, startPos, curPartSize, i + 1, uploadId, key, bucketName));
            }

            /**
             * 等待所有分片完毕
             */
            // 关闭线程池（线程池不马上关闭），执行以前提交的任务，但不接受新任务。
            executorService.shutdown();
            // 如果关闭后所有任务都已完成，则返回 true。
            while (!executorService.isTerminated()) {
                try {
                    // 用于等待子线程结束，再继续执行下面的代码
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /**
             * partETags(上传块的ETag与块编号（PartNumber）的组合) 如果校验与之前计算的分块大小不同，则抛出异常
             */
            System.out.println(UploadPart.partETags.size()  +" -----   "+partCount);
            if (UploadPart.partETags.size() != partCount) {
                throw new IllegalStateException("OSS分块大小与文件所计算的分块大小不一致");
            }

            /*
             * 列出文件所有的分块清单并打印到日志中，该方法仅仅作为输出使用
             */
            //UploadPart.listAllParts(uploadId);

            /*
             * 完成分块上传
             */
            UploadPart.completeMultipartUpload(uploadId);

            // 返回上传文件的URL地址
            //return endpoint + "/" + bucketName + "/" + client.getObject(bucketName, key).getKey();
            return true;
        } catch (Exception e) {
            //logger.error("上传失败！", e);
            return false;
        } finally {
            UploadPart.partETags.clear();
            if (client != null) {
                client.shutdown();
            }
        }
    }

}

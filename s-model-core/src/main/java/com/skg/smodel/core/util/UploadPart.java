package com.skg.smodel.core.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/8/31.
 */
public class UploadPart implements Runnable {
    private MultipartFile localFile;
    private long startPos;

    private long partSize;
    private int partNumber;
    private String uploadId;
    private static String key;
    private static String bucketName;

    // 新建一个List保存每个分块上传后的ETag和PartNumber
    protected static List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());

    protected static OSSClient client = null;

    /**
     * 创建构造方法
     *
     * @param localFile
     *            要上传的文件
     * @param startPos
     *            每个文件块的开始
     * @param partSize
     * @param partNumber
     * @param uploadId
     *            作为块的标识
     * @param key
     *            上传到OSS后的文件名
     */
    public UploadPart(MultipartFile localFile, long startPos, long partSize, int partNumber, String uploadId, String key , String bucketName) {
        this.localFile = localFile;
        this.startPos = startPos;
        this.partSize = partSize;
        this.partNumber = partNumber;
        this.uploadId = uploadId;
        UploadPart.key = key;
        UploadPart.bucketName = bucketName;
    }

    /**
     * 分块上传核心方法(将文件分成按照每个5M分成N个块，并加入到一个list集合中)
     */
    @Override
    public void run() {
        InputStream instream = null;
        try {
            // 获取文件流
            instream = localFile.getInputStream();
            // 跳到每个分块的开头
            instream.skip(this.startPos);

            // 创建UploadPartRequest，上传分块
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(key);
            uploadPartRequest.setUploadId(this.uploadId);
            uploadPartRequest.setInputStream(instream);
            uploadPartRequest.setPartSize(this.partSize);
            uploadPartRequest.setPartNumber(this.partNumber);

            UploadPartResult uploadPartResult = MultipartUpload.client.uploadPart(uploadPartRequest);
            synchronized (partETags) {
                // 将返回的PartETag保存到List中。
                partETags.add(uploadPartResult.getPartETag());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (instream != null) {
                try {
                    // 关闭文件流
                    instream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化分块上传事件并生成uploadID，用来作为区分分块上传事件的唯一标识
     *
     * @return
     */
    protected static String claimUploadId(String bucketName, String key) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult result = MultipartUpload.client.initiateMultipartUpload(request);
        return result.getUploadId();
    }

    /**
     * 将文件分块进行升序排序并执行文件上传。
     *
     * @param uploadId
     */
    protected static void completeMultipartUpload(String uploadId) {
        // 将文件分块按照升序排序
        Collections.sort(partETags, new Comparator<PartETag>() {
            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });

        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName,
                key, uploadId, partETags);
        // 完成分块上传
        MultipartUpload.client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * 列出文件所有分块的清单
     *
     * @param uploadId
     */
    protected static void listAllParts(String uploadId) {
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
        // 获取上传的所有分块信息
        PartListing partListing = MultipartUpload.client.listParts(listPartsRequest);

        // 获取分块的大小
        int partCount = partListing.getParts().size();
        // 遍历所有分块
        for (int i = 0; i < partCount; i++) {
            PartSummary partSummary = partListing.getParts().get(i);
            //logger.info("分块编号 " + partSummary.getPartNumber() + ", ETag=" + partSummary.getETag());
        }
    }

}

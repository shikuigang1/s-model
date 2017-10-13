package com.skg.smodel.core.util;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.*;
import com.aliyun.oss.ClientException;
import com.diich.core.support.cache.JedisHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-11-19
 * Time: 下午3:04
 * To change this template use File | Settings | File Templates.
 */
public class SimpleUpload {

    //存放文件下载进度
    public static Map<String, String> processMap = new HashMap<String, String>();


    private static String accessKeyId = "maTnALCpSvWjxyAy";
    private static String accessKeySecret = "0Ou6P67WhuSHESKrwJClFqCKo5BuBf";

    public static Boolean uploadFile(MultipartFile multipartFile, String key) throws IOException {
        OSSClient client = new OSSClient(PropertiesUtil.getString("img_Server_Path"),PropertiesUtil.getString("accessKeyId") , PropertiesUtil.getString("accessKeySecret"));

        String bucketName = PropertiesUtil.getString("img_bucketName");

        // 获取Bucket的存在信息
        boolean exists = client.doesBucketExist(bucketName);
        if (!exists) {
            // 新建一个Bucket
            client.createBucket(bucketName);
            //CannedAccessControlList是枚举类型，包含三个值： Private 、 PublicRead 、 PublicReadWrite
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
        }

        // 获取指定文件的输入流
        //File file = new File(filePath);
        //InputStream content = multipartFile.getInputStream();
        ByteArrayInputStream content = new ByteArrayInputStream(IOUtils.toByteArray(multipartFile.getInputStream()));
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(multipartFile.getSize());

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        return true;
    }


    /**
     * 删除附件
     */

    public static void deleteFile(String bucketName, String uploadName) throws IOException {
        OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
        client.deleteObject(bucketName, uploadName);

        //To change body of implemented methods use File | Settings | File Templates.
    }



    public static Long calculateFileSize(String bucketName, List<String[]> keysList) {

        Long fileSize = 0L;

        OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
        for (String[] keys : keysList) {
            String key = keys[0];
            OSSObject ossObject = client.getObject(bucketName, key);
            fileSize = fileSize + ossObject.getObjectMetadata().getContentLength();
        }

        return fileSize;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public static List listObjectByPath(String bucket, String path) {

        OSSClient client = new OSSClient(PropertiesUtil.getString("img_Server_Path"), accessKeyId, accessKeySecret);

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket);

        // 递归列出fun目录下的所有文件
        listObjectsRequest.setPrefix(path);

        ObjectListing listing = client.listObjects(listObjectsRequest);

        return listing.getCommonPrefixes();
    }


    public static Boolean uploadFile(InputStream inputStream, String bucketName, String uploadName, long length) throws IOException {
        OSSClient client = new OSSClient(PropertiesUtil.getString("img_Server_Path"), accessKeyId, accessKeySecret);

        // 获取Bucket的存在信息
        boolean exists = client.doesBucketExist(bucketName);
        if (!exists) {
            // 新建一个Bucket
            client.createBucket(bucketName);
            //CannedAccessControlList是枚举类型，包含三个值： Private 、 PublicRead 、 PublicReadWrite
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
        }

        // 获取指定文件的输入流
//        File file = new File(filePath);
        //InputStream content = multipartFile.getInputStream();
        ByteArrayInputStream content = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(length);
        meta.setContentType("text/html");
        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, uploadName, content, meta);

        // 打印ETag
        System.out.println(result.getETag());

        return true;
        //To change body of implemented methods use File | Settings | File Templates.
    }
    //request,file, "diich-resource", url.toString());
    public static void uploadProcessFile(final JedisHelper jedisHelper, final List<MultipartFile> uploadFiles, final String bucketName,
                                         final  List<String> filenames)
            throws OSSException, ClientException, FileNotFoundException,IOException {

       //final OSSClient client = new OSSClient(PropertiesUtil.getString("img_Server_Path"), accessKeyId, accessKeySecret);
        final OSSClient client = new OSSClient("http://oss-cn-beijing.aliyuncs.com", accessKeyId, accessKeySecret);
        // 获取Bucket的存在信息
        boolean exists = client.doesBucketExist(bucketName.trim());

        if (!exists) {
            // 新建一个Bucket
            client.createBucket(bucketName);
            //CannedAccessControlList是枚举类型，包含三个值： Private 、 PublicRead 、 PublicReadWrite
            client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
        }

        for(int i=0;i<uploadFiles.size();i++){
            final MultipartFile uploadFile = uploadFiles.get(i);
            final String filename = filenames.get(i);
            ObjectMetadata objectMeta = new ObjectMetadata();
            objectMeta.setContentLength(uploadFile.getSize());
            // 可以在metadata中标记文件类型
            //objectMeta.setContentType("application/pdf");
            //对object进行服务器端加密，目前服务器端只支持x-oss-server-side-encryption加密
            //objectMeta.setHeader("x-oss-server-side-encryption", "AES256");
            //final InputStream input = new FileInputStream(uploadFile.getInputStream());
            final InputStream input =uploadFile.getInputStream();
            final long length = uploadFile.getSize();
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    InputStream tmpInput = null;
                    while(true){
                        //将input缓存在tmpInput中，防止在调用available()方法是异常导致上传失败
                        tmpInput = input;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            if(input!=null && tmpInput.available()!=0){
                                System.out.println(uploadFile.getName()+"process:"+(float)(uploadFile.getSize()-tmpInput.available())/uploadFile.getSize());

                                //图片下载状态暂时存redis
                                jedisHelper.set(filename,(float)(uploadFile.getSize()-tmpInput.available())/uploadFile.getSize(),120);
                            }else{
                                //System.out.println("this is null!");
                                //request.getSession().setAttribute(filename,1.0);
                                jedisHelper.set(filename,1.0,120);
                                break;
                            }
                        } catch (IOException e) {
                            break;
                        }


                    }

                    jedisHelper.set(filename,1.0,120);
                    //request.getSession().setAttribute(filename,1.0);


                }
            });
            t.start();
            client.putObject(bucketName, filename, input, objectMeta);
        }







    }

}

package com.skg.smodel.core.util;

/**
 * Created by Administrator on 2017/5/28.
 *
 * 根据文件名判断文件类型 图片,文档,视频,音乐
 */
public class FileType {

    public static String fileType(String fileName) {
        if (fileName == null) {
            fileName = "文件名为空！";
            return fileName;

        } else {
            // 获取文件后缀名并转化为写，用于后续比较
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            // 创建图片类型数组
            String img[] = { "bmp", "jpg", "jpeg", "png", "tiff", "gif", "pcx", "tga", "exif", "fpx", "svg", "psd",
                    "cdr", "pcd", "dxf", "ufo", "eps", "ai", "raw", "wmf" };
            for (int i = 0; i < img.length; i++) {
                if (img[i].equals(fileType)) {
                    return "image";
                }
            }


            // 创建文档类型数组
            String document[] = { "txt", "doc", "docx", "xls", "htm", "html", "jsp", "rtf", "wpd", "pdf", "ppt" };
            for (int i = 0; i < document.length; i++) {
                if (document[i].equals(fileType)) {
                    return "doc";
                }
            }
            // 创建视频类型数组
            String video[] = { "mp4", "avi", "mov", "wmv", "asf", "navi", "3gp", "mkv", "f4v", "rmvb", "webm", "mpg" };
            for (int i = 0; i < video.length; i++) {
                if (video[i].equals(fileType)) {
                    return "vedio";
                }
            }
            // 创建音乐类型数组
            String music[] = { "mp3", "wma", "wav", "mod", "ra", "cd", "md", "asf", "aac", "vqf", "ape", "mid", "ogg",
                    "m4a", "vqf" };
            for (int i = 0; i < music.length; i++) {
                if (music[i].equals(fileType)) {
                    return "music";
                }
            }


        }
        return "other";
    }

    public static void main(String[] args) {
        String fileName = "";
        String type = fileType(fileName);
        System.out.println(type);
    }
}

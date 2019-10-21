package com.yuyue.boss.utils;

import com.yuyue.boss.enums.Variables;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GetImageFromVideoUtil {

    public  static String getImage() throws FrameGrabber.Exception {

        //    randomGrabberFFmpegImage("C:/videotest\\小姐姐视频.mp4", "C:\\videotest", "111");
        //"http:\\101.37.252.177:82/image/"
        //String targerFilePath = "http:" + File.separator +  "101.37.252.177:82" + File.separator + "videoImage" ;
        String path = System.getProperty("user.dir").replace("bin", "webapps");
        File upload= new File(path,"/videoImage");
        if(!upload.exists()){
            upload.mkdirs();
        }
        String targerFilePath = path + "/videoImage";
        String s = randomGrabberFFmpegImage(Variables.ip_home+"/group1/M00/00/08/rBDoeV2lhdCARsu8AGh51FLK7-A759.mp4", targerFilePath, "1236");
        return s;
    }

    public static String randomGrabberFFmpegImage(String filePath, String targerFilePath, String targetFileName)
            throws FrameGrabber.Exception {

        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(filePath);
        ff.start();
        String rotate =ff.getVideoMetadata("rotate");
        Frame f;
        String imagePath = null;
        int i = 0;
        while (i <1) {
            f =ff.grabImage();
            opencv_core.IplImage src = null;
            if(null !=rotate &&rotate.length() > 1) {
                OpenCVFrameConverter.ToIplImage converter =new OpenCVFrameConverter.ToIplImage();
                src =converter.convert(f);
                f =converter.convert(rotate(src, Integer.valueOf(rotate)));
            }
            imagePath = doExecuteFrame(f, targerFilePath, targetFileName);

            i++;
           
        }
       
        ff.stop();
        return imagePath;
    }

    /*
     * 旋转角度的
     */
    public static opencv_core.IplImage rotate(opencv_core.IplImage src, int angle) {
        opencv_core.IplImage img = opencv_core.IplImage.create(src.height(), src.width(), src.depth(), src.nChannels());
        opencv_core.cvTranspose(src, img);
        opencv_core.cvFlip(img, img, angle);
        return img;
    }

    public static String doExecuteFrame(Frame f, String targerFilePath, String targetFileName) {

        if (null ==f ||null ==f.image) {
            return null;
        }
        Java2DFrameConverter converter =new Java2DFrameConverter();
        String imageMat ="jpg";
        String FileName =targerFilePath + File.separator +targetFileName +"." +imageMat;
        System.out.println(FileName);
        BufferedImage image =converter.getBufferedImage(f);
        System.out.println("width:" + image.getWidth());
        System.out.println("height:" + image.getHeight());

        File output =new File(FileName);
        try {
            ImageIO.write(image,imageMat,output);
            return output.getPath();
        }catch (IOException e) {
            e.printStackTrace();
            return "生成视频第一帧图片失败！！";
        }
    }
}

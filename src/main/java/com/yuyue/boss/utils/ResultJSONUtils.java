package com.yuyue.boss.utils;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.boss.annotation.MyException;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.enums.CodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class ResultJSONUtils {
    private static Logger log = LoggerFactory.getLogger(ResultJSONUtils.class);

//    public static JSONObject getJSONObjectBean(ReturnResult returnResult){
//        return JSONObject.parseObject(JSONObject.toJSON(returnResult).toString());
//    }

    /**
     * 加减钱，收益
     * @param user
     * @param money
     * @return
     */
    public synchronized static BigDecimal updateTotalMoney(AppUser user, BigDecimal money, String symbol){
        if(StringUtils.isNull(user)){
            throw new MyException(CodeEnum.PARAM_ERROR.getCode(),"加钱失败！用户为空！");
        } else if (money == null){
            throw new MyException(CodeEnum.PARAM_ERROR.getCode(),"钱为空！");
        } else if (money.compareTo(BigDecimal.ZERO) == -1){
            throw new MyException(CodeEnum.PARAM_ERROR.getCode(),"钱不能为负！");
        }
        if("+".equals(symbol)){
            return user.getIncome().add(money);
        }
        return user.getIncome().subtract(money);
    }

    /**
     * 获取hash值分表，物理分表
     * @param tableName
     * @param tableId
     * @return
     */
    public static String getHashValue(String tableName, String tableId){
        StringBuilder name = new StringBuilder(tableName);
        int temp = tableId.hashCode() % 2 == 0 ? 0 : 1; //几张表，就是几
        String append = name.append(temp).toString();
        return append;
    }

    /**
     * 格式化 文件大小
     * @param fileSize
     * @return
     */
    public static String getSize(Double fileSize){
        System.out.println("获取文件大小======="+fileSize);
        //数据转化   B-->KB-->MB
        String size = "";
        DecimalFormat df = new DecimalFormat("#.00");
        if(1024 > fileSize){
            size = fileSize+"B";
        }else if (1024 < fileSize && 1048576 > fileSize){
            size = df.format(fileSize/1024)+"KB";
        }else {
            size = df.format(fileSize/1024/1024)+"MB";
        }
        return size;
    }

    /**
     * @throws IOException
     * 网络文件转换为本地文件
     * @Title: toByteArray
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param url
     * @param @return
     * @param @throws IOException    设定文件
     * @return byte[]    返回类型
     * @throws
     */
    public static void toBDFile(String urlStr, String bdUrl) throws Exception{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        DataInputStream in = new DataInputStream(conn.getInputStream());
        byte[] data=toByteArray(in);
        in.close();
        FileOutputStream out=new FileOutputStream(bdUrl);
        out.write(data);
        out.close();
    }

    /**
     * 网络文件转换为byte二进制
     * @Title: toByteArray
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param url
     * @param @return
     * @param @throws IOException    设定文件
     * @return byte[]    返回类型
     * @throws
     */
    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024*4];
        int n=0;
        while ( (n=in.read(buffer)) !=-1) {
            out.write(buffer,0,n);
        }
        return out.toByteArray();
    }
}

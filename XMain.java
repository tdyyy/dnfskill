package com.example.demo;

import java.io.*;
import java.math.BigDecimal;

/**
 * @Author: tangdy
 * @Date: 2022/11/4 10:27
 * @Vision: 1.0
 */
public class XMain {
    public static void main(String[] args) throws Exception {

        String fileName = "swordman\\swordman_vajra.skl";

        // 读取文件内容
        String read = read(fileName);

        // 获取技能数据
        String levelInfo = getLevelInfoByType(read, true);

        //修改技能数据 可做多次
        String newLevelInfo = changeInfo(levelInfo, 1, 0.2);
        //newLevelInfo = changeInfo(newLevelInfo, 2, 2);

        //对比查看
        System.out.println(outLevelInfo(levelInfo,19));
        System.out.println(outLevelInfo(newLevelInfo,19));

        // 替换技能信息
        String replace = read.replace(levelInfo.trim(), newLevelInfo.trim());

        // 输出文件 注意文件路径对比无问题再替换到原来的路径
        write(replace,fileName);

    }

    static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3       ) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) <<  8) |
                ((b0 & 0xff)      ));
    }

    /**
     * 输出指定等级数据
     * @param str levelInfo 内容
     * @param nub 等级
     * @return 等级数据
     */
    static String outLevelInfo(String str,int nub) {
        str = str.trim();
        int len = Integer.parseInt(str.substring(0,str.indexOf("\t")));
        String[] split = str.split("\t");
        StringBuilder sb = new StringBuilder();
        for (int i = (nub-1)*len+1; i < nub*len+1; i++) {
            sb.append(split[i]).append("\t");
        }
        return sb.toString();
    }

    /**
     * 修改的内容
     * @param str levelInfo 信息
     * @param off 修改值的位置 从1开始
     * @param imp 修改系数 0.2 为增强20%
     * @return 修改后的levelInfo
     */
    static String changeInfo(String str,int off,double imp) {
        str = str.trim();
        int len = Integer.parseInt(str.substring(0,str.indexOf("\t")));
        String[] split = str.split("\t");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if((i-off+len)%len==0){
                int d = new BigDecimal(split[i]).multiply(new BigDecimal(1+imp)).intValue();
                sb.append(d).append("\t");
            }else {
                sb.append(split[i]).append("\t");
            }
        }
        return sb.toString();
    }

    /**
     * 读取文件内容
     * @param fileName 文件相对skill路径的地址
     * @return 文件内容
     * @throws Exception
     */
    static String read(String fileName) throws Exception {
        String prePath = "D:\\dnfskill\\dnfskill\\skill\\";
        String path = prePath + fileName;
        File skillFile = new File(path);
        if (!skillFile.exists()) {
            System.out.printf("文件路径有误！");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(skillFile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 文件复写
     * @param fileStr 文件内容
     * @param fileName 文件名
     * @throws Exception
     */
    static void write(String fileStr,String fileName) throws Exception {
        String prePath = "D:\\dnfskill\\dnfskill\\backup\\";
        String path = prePath + fileName;
        File preFile = new File(path.substring(0,path.lastIndexOf("\\")));
        File skillFile = new File(path);

        if(!preFile.exists()){
            preFile.mkdirs();
        }

        if (!skillFile.exists()) {
            skillFile.createNewFile();
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(skillFile))) {
            br.write(fileStr,0,fileStr.length());
        }catch (Exception e){
            System.out.println("文件写入失败，e"+e.getMessage());
        }
        return;
    }

    /**
     * 获取技能的静态数据还是动态数据 由于不同技能标识不同，所以需要自行对照
     * @param str 文件内容
     * @param type true：动态内容 false：静态内容  ps：目前只支持动态数据 因为静态数据就那么两条 自己改吧
     * @return levelInfo内容
     */
    static String getLevelInfoByType(String str,boolean type){
        String levelTagStart = type?"[level info]":"[static data]";
        String levelTagEnd = type?"[/level info]":"[/static data]";
        String dungeon = str.substring(str.indexOf("[dungeon]"),str.indexOf("[/dungeon]")+10);
        String levelInfo = dungeon.substring(dungeon.indexOf(levelTagStart)+levelTagStart.length(),dungeon.indexOf(levelTagEnd));
        return levelInfo;
    }
}

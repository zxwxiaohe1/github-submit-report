package com.si;

import com.sinux.core.utils.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @ContentDiscrible: 本地文件检查及本地目录拼接
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/13.
 */
public class LocalFileUtils {

    /**
     * 判断文件项目是否存在
     **/
    public Boolean directoryCheck(String proDir)throws IOException,FileNotFoundException,GitAPIException {
        Boolean orFile=false;
        //得到预操作的目录
        StringBuffer dir=new StringBuffer(proDir);
        File file=new File(dir.toString());
        if(file.exists()) {//判断目录是否存在
            if(file.isDirectory()){
                File fileGit=new File(dir.append(File.separator).append(".git").toString());//判断该目录是否是git项目目录
                if(fileGit.exists()){
                    orFile=true;
                }else{
                    FileUtils.deleteDirectory(proDir);//删除目录
                }
            }
        }
        return orFile;
    }
    /**
     * 将字符串尾部特殊标点符号替换
     * @param objectString 对象串
     *   例如：去掉字符串首尾特殊标点符号、
     *   返回：去掉字符串首尾特殊标点符号。
     */
    public static String delStrMark(String str,String end){
        String mark="、· ~ @ # ￥ % … ^ &";
        int len=str.trim().length();
        String str1="";
        if(mark.trim().indexOf(str.charAt(len))!=-1){
            str1=str.trim().substring(0,len-1);
        }
        StringBuilder strb = new StringBuilder();
        return strb.append(str1).append(end).toString();
    }
}

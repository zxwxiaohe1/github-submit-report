package com.si;

import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @ContentDiscrible: 执行mvn命令生成测试报告
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/13.
 */

public class MvnCommandExec {

    /**
     * 执行mvn命令
     *
     * @throws IOException
     **/
    public  int commandExecute(String path, String[] commands) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        int result = -1;
        try {
            String os  = System.getProperty("os.name");
            String mvn = "mvn.cmd ";
            if(os!=null && os.contains("inux")){
                mvn = "mvn  ";
            }
            for (String cmd : commands) {
                Process process = runtime.exec(mvn + cmd, null, new File(path).getAbsoluteFile());
                StreamGobbler in = new StreamGobbler(process.getInputStream(),"std");
                StreamGobbler err = new StreamGobbler(process.getErrorStream(),"err");

                in.start();
                err.start();
                process.waitFor();
                result=0;
            }
        }catch (Exception e){result = -1;}
        return result;
    }

//    public static void main(String [] args)throws Exception {
//
//        String path="J:\\service\\apache-tomcat-8.5.20\\bin\\jfusionci\\jfusion-gen\\";
//        String cmd="J:\\service\\apache-maven-3.2.5-zip\\apache-maven-3.2.5\\bin\\mvn.bat  test";
//        StringBuilder exec = new StringBuilder();
//        Runtime runtime = Runtime.getRuntime();
//        int result = 0;
//            Process process = runtime.exec(cmd, null, new File(path).getAbsoluteFile());
//
//            StreamGobbler in = new StreamGobbler(process.getInputStream(),"std");
//            StreamGobbler err = new StreamGobbler(process.getErrorStream(),"err");
//
//            in.start();
//            err.start();
//
//            process.waitFor();
//    }
}
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
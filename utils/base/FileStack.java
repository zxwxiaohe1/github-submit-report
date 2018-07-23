package com.si;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @ContentDiscrible:
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/23.
 */
public class FileStack {

    /**
     * 遍历文件夹下指定文件名的文件
     * @param srcDir    根目录
     * @param  files    模式，可以多个，比如"testng-results.xml" "TEST-TestSuite.xml"
     */
    public   List<File> getTargetFiles(String srcDir,String file) {
        List<File> targetFiles=new ArrayList<File>();
        Stack<Dir> stack = new Stack<Dir>();
        Dir root = new Dir(new File(srcDir).getAbsoluteFile());   //创建自定义目录
        stack.push(root);                 //将根目录压入栈中

        String[] files=file.split(",");

        while (!stack.empty()) {            //循环处理栈顶目录
            Dir currentDir = stack.peek(); //取出栈顶元素但不删除它

            if (currentDir.peekNum > 0) {         //若栈顶目录之前peek过，则略过该目录(因为其子文件已全部被处理过)
                stack.pop();
                continue;
            }

            File[] children = currentDir.dir.listFiles();

            if (children == null || children.length == 0) {   //若该目录是空目录，则略过该目录
                stack.pop();
                continue;
            } else {
                int countDir = 0;                       //定义统计子目录数的变量
                for (File f : children) {

                    if (f.isDirectory()) {              //对于子目录，不处理，直接压入栈中，然后重新执行循环
                        stack.push(new Dir(f));
                        countDir++;
                    } else {

                        for (String p : files) {             //匹配模式，只要匹配一种模式，则退出匹配
                            if (f.getName().equals(p)) {
                                targetFiles.add(f);
                                break;
                            }
                        }
                    }
                }
                if (countDir == 0) {                 //如果子目录数为0，则该目录是叶子节点，将其剔出栈
                    stack.pop();
                    continue;
                } else {
                    currentDir.peekNum++;         //如果子目录数不为0，则标示下该目录已经被peek过
                }
            }
        }
        return targetFiles;
    }
//    public static void main(String [] args){
//        FileStack stackTest=new FileStack();
//        List<File> targetFiles=stackTest.getTargetFiles("D:\\jfusion\\jfusion-gen\\target\\surefire-reports","testng-results.xml,TEST-TestSuite.xml");
//        for(int i=0;i<targetFiles.size();i++){
//            System.out.println("targetFiles.name="+targetFiles.get(i).getName());
//        }
//    }

}

class Dir {
    File dir;
    int peekNum;

    Dir(File dir){
        this.dir = dir;
        this.peekNum = 0;
    }
}
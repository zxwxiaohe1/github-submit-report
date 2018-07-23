package com.si;

import com.sinux.addon.ci.service.GithubService;
import com.sinux.core.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ContentDiscrible: github代码上传触发控制器，发送邮件通知开发人员代码更新影响的测试用列
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/15.
 */

@Controller
@ConditionalOnProperty(prefix = "git-report", name = "enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping(value = "/conn/git")
public class GitEventControllor {

    @Autowired
    private GithubService githubService;
    // 创建一个线程池对象，创建几个线程对象。
    private ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);

    @ResponseBody
    @RequestMapping(value = "report")
    public String openThread(String proModel, HttpServletRequest request) {
        // 可以执行Runnable对象或者Callable对象代表的线程
        if (proModel!=null) {
            pool.submit(new TaskRunable(proModel));
        }
        System.out.println("线程池中线程数目："+pool.getPoolSize()+"，已执行玩别的任务数目："+pool.getCompletedTaskCount());
        return "测试报告稍后发送";
    }

    class TaskRunable implements Runnable{
        private String proModel;
        public TaskRunable(String proModel) {
            this.proModel=proModel;
        }
        @Override
        public void run() {
            try { //处理获取报告逻辑
                Thread.currentThread();
                githubService.getReport(proModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
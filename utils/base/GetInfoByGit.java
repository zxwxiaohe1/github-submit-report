package com.si;

import com.sinux.addon.ci.cfg.CIConfiguration;
import com.sinux.core.condition.ConditionalOnProperty;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @ContentDiscrible: 通过代码执行git命令获取github信息
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/13.
 */

@Component
@ConditionalOnProperty(prefix = "git-report", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GetInfoByGit {

    @Autowired
    private CIConfiguration githubComponent;

    /**
     * 克隆远程仓库
     * @throws IOException
     * @throws GitAPIException
     **/
    public Git cloneRepository(String model,String localPath) throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(githubComponent.getGitconfig().get("acountName"), githubComponent.getGitconfig().get("password"));
        //克隆代码库命令
        CloneCommand cloneCommand = Git.cloneRepository();
        Git git= cloneCommand.setURI(githubComponent.getComponents().get(model)) //设置远程URI
                .setBranch(githubComponent.getGitconfig().get("branch")) //设置clone下来的分支
                .setDirectory(new File(localPath)) //设置下载存放路径
                .setCredentialsProvider(usernamePasswordCredentialsProvider) //设置权限验证
                .call();
        return git;
    }

    /**
     * 本地存在项目时拉取远程仓库内容到本地
     * @throws IOException
     * @throws GitAPIException
     * */
    public Git PullRepository(String model) throws IOException, GitAPIException {
        StringBuffer gitAdr=new StringBuffer();
        gitAdr.append(githubComponent.getLocalAddress()).append(File.separator).append(model).append(File.separator).append(".git");
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(githubComponent.getGitconfig().get("acountName"), githubComponent.getGitconfig().get("password"));
        //本地仓库地址
        Git git = new Git(new FileRepository(gitAdr.toString()));
        git.pull()
           .setRemoteBranchName(githubComponent.getGitconfig().get("branch"))
           .setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        return git;
    }

    /**
     * 获取github历史
     * @throws IOException
     * @throws GitAPIException
     **/
   public Iterable<RevCommit> getLogHistory(Git git)throws IOException, GitAPIException{
           //得到提交历史
           LogCommand logCommand=git.log();
           Iterable<RevCommit> logList=logCommand.call();
            return logList;
   }
    /**
     * 排除指定模块，将其他模块的远程地址读取为可遍历集合
     * @throws FileNotFoundException
     **/
    public Set<String> getTestModules()throws FileNotFoundException{
        String[] excludes = githubComponent.getDependencies().get("exclude").trim().split(",");
        Map<String,String> jfusions= githubComponent.getComponents();

        for (String m: excludes
             ) {
            if (jfusions.containsKey(m)){
                jfusions.remove(m);
            }
        }

        return jfusions.keySet();
    }
}

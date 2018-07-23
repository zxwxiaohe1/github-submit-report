package com.si;

import com.sinux.addon.ci.cfg.CIConfiguration;
import com.sinux.addon.ci.entity.TestReport;
import com.sinux.addon.ci.utils.base.*;
import com.sinux.addon.ci.web.GitEventControllor;
import com.sinux.core.condition.ConditionalOnProperty;
import com.sinux.core.mail.MailBody;
import com.sinux.core.mail.MailSendUtils;
import com.sinux.core.utils.FileUtils;
import com.sinux.core.utils.SpringContextHolder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.testng.util.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @ContentDiscrible: ithub代码上传触发服务类，下载模块代码、执行mvn命令、发送测试邮件
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/13.
 */

@Service
@ConditionalOnProperty(prefix = "git-report", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GithubService {

    @Autowired
    private GetInfoByGit getInfoByGit;
    @Autowired
    private CIConfiguration githubComponent;
    @Autowired
    public GenerateReport generateReport;

    public void getReport(String proModel)throws Exception {
        String basepath= githubComponent.getLocalAddress();
        if (Strings.isNullOrEmpty(basepath)){
            return;
        }

        Git git=null; //git仓库变量
        boolean result=false;
        String upstreamModules = githubComponent.getDependencies().get("center");

        boolean judgCenter = false;
        boolean judgDependency = false;
        if(!Strings.isNullOrEmpty(upstreamModules)){
            String[] modules = upstreamModules .split(",");

            for (String m: modules) {//
                if(proModel.equals(m)){
                    judgCenter = true;
                    break;
                }
            }
            for (String c: getInfoByGit.getTestModules()) {//
                if(proModel.equals(c)){
                    judgDependency = true;
                    break;
                }
            }
        }

        StringBuffer mailContent=new StringBuffer();
        if(judgCenter&&judgDependency){
            for(String str:getInfoByGit.getTestModules()){
                try {
                    git= getRepository(str);
                } catch (Exception e) {continue;}//如果当前项目仓库拉取异常则执行下一个项目拉取


                if(!ObjectUtils.isEmpty(git)){
                    String targetName =  new StringBuffer().append(basepath).append(File.separator)
                            .append(str).append("/target").toString();
                    File target = new File(targetName);
                    try {
                        synchronized (SpringContextHolder.getBean("generateReport")){
                            FileUtils.delFile(targetName);
                        }
                    }catch (Exception e){}

                    String workingDir = new StringBuffer().append(basepath).append(File.separator).append(str).toString();
                    String mvnTestCommand = githubComponent.getMvn().get("command");
                    String reportXml = new StringBuffer().append(basepath).append(File.separator).append(str).toString();
                    mailContent.append(generateReport.readAndSend(reportXml,proModel));

                }
            }
            if(!ObjectUtils.isEmpty(mailContent)){
                result=sendReportToMail(new StringBuffer().append(basepath).append(File.separator).append(proModel).toString(),mailContent.toString());
            }
        }else if (!judgCenter&&judgDependency){
            git=getRepository(proModel);

            if(!ObjectUtils.isEmpty(git)){

                String targetName =  new StringBuffer().append(basepath).append(File.separator)
                        .append(proModel).append("/target").toString();
                File target = new File(targetName);
                try {
                    synchronized (SpringContextHolder.getBean("generateReport")){
                        FileUtils.delFile(targetName);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String modulePath=new StringBuffer().append(basepath).append(File.separator).append(proModel).toString();
                    mailContent.append(generateReport.readAndSend(modulePath,proModel));
                    if(!ObjectUtils.isEmpty(mailContent)){
                        result= sendReportToMail(modulePath,mailContent.toString());
                    }
            }
        }
        if(result){
            System.out.println("已发送");
        }
    }

    /**
     * 将远程仓库下载到本地
     * */
    public Git getRepository(String proModel)throws IOException,GitAPIException {
        LocalFileUtils localAdr=new LocalFileUtils();
        Git git=null;
        String localPath= githubComponent.getLocalAddress()+File.separator+proModel;
        if(localAdr.directoryCheck(localPath)){
            git=getInfoByGit.PullRepository(proModel);
        }else{
            git=getInfoByGit.cloneRepository(proModel,localPath);
        }
        return  git;
    }

    /**
     * 获取最近提交代码人的邮箱账号
     * */
    public String  getEmailAccount(String proDir)throws IOException,FileNotFoundException,GitAPIException {
        String[] str=null;
        Git git=Git.open(new File(proDir));
        for(RevCommit revCommit:getInfoByGit.getLogHistory(git)) {
            if (!ObjectUtils.isEmpty(revCommit.getAuthorIdent())) {
                str = revCommit.getAuthorIdent().toString().split(",");
                break;
            }
        }
        return str[1];
    }

    /**
     * 创建mvn执行盘符、目录及命令，调用执行mvn命令方法
     * */
    public Boolean execMvnText(String localPath,String cmds){
        Boolean error = false;

        MvnCommandExec exec=new MvnCommandExec();
        String[] cmd=cmds.split(",");
        try {
            error = exec.commandExecute(localPath,cmd)==0?true:false;

        } catch (Exception e) {
            e.printStackTrace();
            error=false;
        }

        return error;
    }

    /**
     * 发送测试报告到邮箱
     * */
    public boolean sendReportToMail(String proDir,String mailContent)throws Exception{
        String accountMail=getEmailAccount(proDir).trim();
        MailBody mailBody=new MailBody();
        mailBody.setMailServerHost(githubComponent.getMail().get("serverhost"));
        mailBody.setMailServerPort(githubComponent.getMail().get("serverport"));
        mailBody.setFromAddress(githubComponent.getMail().get("FromAddress"));
        mailBody.setToAddress(accountMail);//发往账户accountMail
        mailBody.setUserName(githubComponent.getMail().get("account"));
        mailBody.setPassword(githubComponent.getMail().get("password"));
        mailBody.setValidate(true);
        mailBody.setSubject(githubComponent.getMail().get("theme"));
        mailBody.setContent(mailContent);
        MailSendUtils msu=new MailSendUtils();
        return  msu.sendHtmlMail(mailBody);
//        return  true;
    }
}
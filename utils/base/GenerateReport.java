package com.si;

import com.sinux.addon.ci.cfg.CIConfiguration;
import com.sinux.addon.ci.entity.TestReport;
import com.sinux.addon.ci.service.GithubService;
import com.sinux.core.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @ContentDiscrible:
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/22.
 */
@Component
@ConditionalOnProperty(prefix = "git-report", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GenerateReport {
    @Autowired
    private CIConfiguration githubComponent;

    @Autowired
    private GithubService githubService;

    String proModule;

    public GenerateReport(){
        this.proModule=null;
    }

    public synchronized String readAndSend(String modulePath,String proModel)throws Exception{

        SaxParseXml domReadXml=new SaxParseXml();

        StringBuffer mailContent=new StringBuffer();

        if( githubService.execMvnText(modulePath, githubComponent.getMvn().get("command"))){

            TestReport testReport =domReadXml.readXML(modulePath,githubComponent.getParserxml());

            mailContent.append(testReport.getMailContent(proModel));
        }

        return mailContent.toString();
    }
}

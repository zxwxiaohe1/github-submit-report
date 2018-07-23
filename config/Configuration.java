package com.si;

import com.sinux.addon.ci.service.GithubService;
import com.sinux.addon.ci.utils.base.GetInfoByGit;
import com.sinux.addon.ci.web.GitEventControllor;
import com.sinux.core.boot.context.properties.ConfigurationProperties;
import com.sinux.core.condition.ConditionalOnBean;
import com.sinux.core.condition.ConditionalOnClass;
import com.sinux.core.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.Map;

/**
 * @ContentDiscrible: 控制github代码触发发送邮件配置，起到插件的启用与关闭
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/15.
 */

@Configuration
@ConditionalOnProperty(prefix = "git-report", name = "enabled", havingValue = "true", matchIfMissing = false)
@ConfigurationProperties(prefix="git-report")
public class CIConfiguration {
    private String localAddress;
    private String parserxml;
    private Map<String,String> mail;
    private Map<String,String> dependencies;
    private Map<String,String> gitconfig;
    private Map<String,String> mvn;
    private Map<String,String> components;

    public String getParserxml() {
        return parserxml;
    }

    public void setParserxml(String parserxml) {
        this.parserxml = parserxml;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public void setMail(Map<String, String> mail) {
        this.mail = mail;
    }

    public void setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

    public void setGitconfig(Map<String, String> gitconfig) {
        this.gitconfig = gitconfig;
    }

    public void setMvn(Map<String, String> mvn) {
        this.mvn = mvn;
    }

    public void setComponents(Map<String, String> components) {
        this.components = components;
    }

    public String getLocalAddress() {

        return localAddress;
    }

    public Map<String, String> getMail() {
        return mail;
    }

    public Map<String, String> getDependencies() {
        return dependencies;
    }

    public Map<String, String> getGitconfig() {
        return gitconfig;
    }

    public Map<String, String> getMvn() {
        return mvn;
    }

    public Map<String, String> getComponents() {
        return components;
    }
}

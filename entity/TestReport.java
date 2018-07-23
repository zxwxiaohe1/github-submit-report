package com.si;

import java.util.List;
import java.util.Map;

/**
 * @ContentDiscrible: 测试简略报告信息
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/14.
 */

public class TestReport {

    private Map<String,Integer> summation;//测试报告汇总
    private List<FailureCaseInfo> failureCases;

    public Map<String, Integer> getSummation() {
        return summation;
    }

    public void setSummation(Map<String, Integer> summation) {
        this.summation = summation;
    }

    public List<FailureCaseInfo> getFailureCases() {
        return failureCases;
    }

    public void setFailureCases(List<FailureCaseInfo> failureCases) {
        this.failureCases = failureCases;
    }


    public String getMailContent(String model){
        StringBuffer st=new StringBuffer();
        boolean sequence=false;
        st.append("</br><h4>项目"+model+"模块汇总:</h4></br><h5><strong>摘要：")
                .append("total:<font color='yellow'>").append(this.getSummation().get("total")).append(" </font>")
                .append("failed:<font color='red'>").append(this.getSummation().get("failed")).append(" </font>")
                .append("passed:<font color='blue'>").append(this.getSummation().get("passed")).append(" </font>")
                .append("skipped:<font color='gray'>").append(this.getSummation().get("skipped")).append(" </font>");
        st.append("</strong></h5></br>");
        for(int i=0;i<this.failureCases.size();i++){
            st.append("<h>自动化测试未通过的测试类:<font color='red'>"+this.failureCases.get(i).getClassName()+"</font>该类未通过的测试方法：<font color='red'>"+this.failureCases.get(i).getErrorMethod()+"</font></h5></br>");
        }
        st.append("<h5><strong>是否改动了以往的类及方法，改动了联系相关测试以便重新生成测试用例</strong></h5>");
        return st.toString();
    }
}
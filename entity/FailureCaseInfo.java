package com.si;

/**
 * @ContentDiscrible: 测试失败类的信息
 * @Author: Created by heyong.
 * @CreateTime: on 2017/9/14.
 */

public class FailureCaseInfo {
    private String className;
    private String ErrorMethod;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getErrorMethod() {
        return ErrorMethod;
    }

    public void setErrorMethod(String errorMethod) {
        ErrorMethod = errorMethod;
    }
}

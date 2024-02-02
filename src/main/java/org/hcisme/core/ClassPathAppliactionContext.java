package org.hcisme.core;

public class ClassPathAppliactionContext implements AppliactionContext{
    String classPath;

    public ClassPathAppliactionContext(String classPath) {
        this.classPath = classPath;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }
}

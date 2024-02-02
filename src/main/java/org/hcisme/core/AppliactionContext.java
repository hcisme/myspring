package org.hcisme.core;

/**
 * mySpring 框架应用上下文
 */
public interface AppliactionContext {


    /**
     * 获取对应的bean对象
     */
    Object getBean(String beanName);
}

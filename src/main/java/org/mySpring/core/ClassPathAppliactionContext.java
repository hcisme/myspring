package org.mySpring.core;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathAppliactionContext implements AppliactionContext {
    private Map<String, Object> singletonObjects = new HashMap<>();

    public ClassPathAppliactionContext(String configLocation) throws DocumentException {
        // 通过路径 解析 xml 文件
        SAXReader reader = new SAXReader();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(configLocation);
        Document document = reader.read(is);

        List<Node> nodes = document.selectNodes("//bean");
        nodes.forEach((node) -> {
            try {
                Element ele = (Element) node;
                String id = ele.attributeValue("id");
                String classPath = ele.attributeValue("class");
                // 通过反射获取 class
                Class<?> aClass = Class.forName(classPath);
                // 获取默认构造方法
                Constructor<?> declaredCon = aClass.getDeclaredConstructor();
                // 得到实例化bean
                Object bean = declaredCon.newInstance();
                singletonObjects.put(id, bean);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        // 注入set方法
        nodes.forEach((node) -> {
            try {
                Element ele = (Element) node;
                List<Element> propertyList = ele.elements("property");
                propertyList.forEach((property) -> {
                    try {
                        String id = ele.attributeValue("id");
                        String classPath = ele.attributeValue("class");
                        // 通过反射获取 class
                        Class<?> aClass = Class.forName(classPath);
                        String name = property.attributeValue("name");
                        Field field = aClass.getDeclaredField(name);
                        String value = property.attributeValue("value");
                        String refValue = property.attributeValue("ref");
                        String methodName = "set" + name.toUpperCase().charAt(0) + name.substring(1);
                        // 输入类型
                        Method setMethod = aClass.getDeclaredMethod(methodName, field.getType());
                        // 调用 set 方法
                        if (value != null) {
                            // 得到
                            String simpleName = field.getType().getSimpleName();
                            if (simpleName.equals("int")) {
                                setMethod.invoke(getBean(id), Integer.parseInt(value));
                            } else {
                                setMethod.invoke(getBean(id), value);
                            }
                        }
                        if (refValue != null) {
                            setMethod.invoke(getBean(id), getBean(refValue));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                });
            } catch (Exception e) {
            }
        });
    }

    @Override
    public Object getBean(String beanName) {
        return singletonObjects.get(beanName);
    }
}
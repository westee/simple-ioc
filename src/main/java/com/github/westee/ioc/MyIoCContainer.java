package com.github.westee.ioc;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

public class MyIoCContainer {
    HashMap<String, Object> beans = new HashMap<>();

    // 实现一个简单的IoC容器，使得：
    // 1. 从beans.properties里加载bean定义
    // 2. 自动扫描bean中的@Autowired注解并完成依赖注入
    public static void main(String[] args) {
        MyIoCContainer container = new MyIoCContainer();
        container.start();
        OrderService orderService = (OrderService) container.getBean("orderService");
        orderService.createOrder();
    }

    // 启动该容器
    public void start() {
        // 1. 加载beans.properties文件
        Properties properties = new Properties();
        try {
            properties.load(MyIoCContainer.class.getClassLoader().getResourceAsStream("beans.properties"));
            for (Object key : properties.keySet()) {
                Class<?> aClass = Class.forName((String) properties.get(key));
                Object o = aClass.getConstructor().newInstance();
                beans.put((String) key, o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 遍历beans
        for (Object key : beans.keySet()) {
            //  获取bean的注解
            Class<?> aClass = beans.get(key).getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Field declaredField = declaredFields[i];
                if (Objects.nonNull(declaredField.getAnnotation(Autowired.class))) {
                    Object o = beans.get(key);
                    declaredField.setAccessible(true);
                    String name = declaredField.getName();
                    try {
                        declaredField.set(o, beans.get(name));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    // 从容器中获取一个bean
    public Object getBean(String beanName) {
        return beans.get(beanName);
    }
}

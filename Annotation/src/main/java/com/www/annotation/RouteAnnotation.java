package com.www.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
*@data:2021/6/9
*@author:yulai
 * 自定义注解
*/

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RouteAnnotation {
    String name() default "MainActivity";
}

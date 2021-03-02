package com.cube.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 某些方法需要组合注解的用这个注解标注 put和del返回必然是int型，操作影响行数 取消组合缓存操作，简化逻辑
 * 
 * @author phoenix
 * @date 2020年3月4日
 */
@Deprecated
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MyCache {

	CacheDel[] del() default {};// 删除对象，需要删除该对象的所有缓存数据

}

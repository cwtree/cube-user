package com.cube.aop.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName:Auth <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年11月15日 上午10:07:52 <br/>
 *
 * @author chiwei
 * @see
 * @since JDK 1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

	/**
	 * @return the Spring-EL expression to be evaluated before invoking the
	 *         protected method
	 */
	String[] permissions() default "";

}

/* BindingParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/15 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify the name of a parameter of a method.<br/>
 * The value of this parameter is getting from current binding argument.
 * 
 * @see Init
 * @see Command
 * @author dennis
 * @since 6.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindingParam {
	/**
	 * name of the parameter.
	 * Since 9.2.0, it can be omitted if name is the same as the annotated parameter.
	 * @return name of the parameter
	 */
	String value() default "";
}

/* WestTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/11/08  09:38:51 , auto generated by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkex.zul.West;
import org.zkoss.jsp.zul.impl.BranchTag;

/**
 * The JSP tag of {@link West}.
 * @author Ian Tsai
 *
 */
public class WestTag extends BranchTag {
	/**
	 * 
	 * @param use must provide defult constractor
	 * @return new instance constracted by use class 
	 * @throws Exception
	 */
	protected Component newComponent(Class use) throws Exception{
		return (Component) (use==null?new West():use.newInstance());
	}

}

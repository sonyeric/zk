/* F91_ZK_4552_1Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 12:03:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class F91_ZK_4552_1Test extends WebDriverTestCase {
	@Test
	public void testScroll() {
		connect("/test2/F91-ZK-4552-scroll.zul");
		waitResponse();
		Assert.assertTrue(jq("$l2").positionTop() >= 0);

		click(jq("$sToTopBtn"));
		waitResponse();
		Assert.assertEquals(0, jq("$vm").scrollTop(), 10);

		click(jq("$sToList"));
		waitResponse();
		JQuery list = jq("$list").eq(0);
		JQuery last = list.children("div").last();
		Assert.assertTrue(last.positionTop() > list.height());
	}

	@Test
	public void testFocus() {
		connect("/test2/F91-ZK-4552-focus.zul");
		waitResponse();
		Assert.assertTrue(jq("$tb").is(":focus"));

		click(jq("$fIntoList"));
		waitResponse();
		Assert.assertTrue(jq("@textbox").last().is(":focus"));
	}
}

/* ListitemDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Listitem;

/**
 * {@link Listitem}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListitemDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Listitem self = (Listitem)comp;
		
		if(self.getMold().equals("select")){
			wh.write("<option id=\"").write(self.getUuid()).write("\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
			RenderFns.getOut(out).setMaxlength(self.getMaxlength()).setValue(self.getLabel()).render();
			wh.write("</option>");
		}else{
			wh.write("<tr id=\"").write(self.getUuid()).write("\" z.type=\"Lit\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			wh.write("</tr>");
		}
		//${self.listbox.mold == 'select' ? '~./zul/html/listitem-select.dsp': '~./zul/html/listitem.dsp'}
		/*
		 select:
		<option id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><c:out value="${self.label}" maxlength="${self.maxlength}"/></option>
		 default:
		 <tr id="${self.uuid}" z.type="Lit"${self.outerAttrs}${self.innerAttrs}>
			<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
			</c:forEach>
		</tr>
		*/
	}

}

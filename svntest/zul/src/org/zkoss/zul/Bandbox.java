/* Bandbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:14:46     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zul.impl.Utils;

/**
 * A band box. A bank box consists of an input box ({@link Textbox} and
 * a popup window {@link Bandpopup}.
 * It is similar to {@link Combobox} except the popup window could have
 * any kind of children. For example, you could place a textbox in
 * the popup to let user search particular items.
 *
 * <p>Default {@link #getMoldSclass}: z-bandbox.(since 3.5.0)
 *
 * <p>Events: onOpen<br/>
 * Developers can listen to the onOpen event and initializes it
 * when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or
 * clean up if false.
 *
 * <p>Note: to have better performance, onOpen is sent only if
 * a non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 *
 * @author tomyeh
 */
public class Bandbox extends Textbox {
	private transient Bandpopup _drop;
	private boolean _autodrop, _btnVisible = true;

	public Bandbox() {
	}
	public Bandbox(String value) throws WrongValueException {
		this();
		setValue(value);
	}
	
	/** Returns the dropdown window belonging to this band box.
	 */
	public Bandpopup getDropdown() {
		return _drop;
	}
	/** Closes the popup ({@link #getDropdown}).
	 */
	public void closeDropdown() {
		response("close", new AuInvoke(this, "cbclose"));
	}

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}
	/** Sets whether to automatically drop the list if users is changing
	 * this text box.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("z.adr", autodrop);
		}
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}
	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("z.btnVisi", visible);
		}
	}

	/** Returns the image URI that is displayed as the button to open
	 * {@link Bandpopup}.
	 * Default: null. (since 3.5.0)
	 * @deprecated As of release 3.5.0
	 */
	public String getImage() {
		return null;
	}
	/** Sets the image URI that is displayed as the button to open
	 * {@link Bandpopup}.
	 *
	 * @param img the image URI.
	 * @deprecated As of release 3.5.0
	 */
	public void setImage(String img) {
	}

	/** Drops down or closes the child.
	 *
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (open) open();
		else close();
	}
	/** Drops down the child.
	 * The same as setOpen(true).
	 *
	 * @since 3.0.1
	 */
	public void open() {
		response("dropdn", new AuInvoke(this, "dropdn", true));
	}
	/** Closes the child if it was dropped down.
	 * The same as setOpen(false).
	 *
	 * @since 3.0.1
	 */
	public void close() {
		response("dropdn", new AuInvoke(this, "dropdn", false));
	}

	/**
	 * Bandbox can't be enabled the multiline functionality.
	 */
	public void setMultiline(boolean multiline) {
		if (multiline)
			throw new UnsupportedOperationException("Bandbox doesn't support multiline");
	}

	/**
	 * Bandbox can't be enabled the rows functionality.
	 */
	public void setRows(int rows) {
		if (rows != 1)
			throw new UnsupportedOperationException("Bandbox doesn't support multiple rows, "+rows);
	}

	// super
	public String getMoldSclass() {
		return _moldSclass == null ? "z-bandbox" : super.getMoldSclass();
	}
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super.getOuterAttrs());
		final boolean adr = isAutodrop();
		if (!isAsapRequired(Events.ON_OPEN) && !adr)
			return sb.toString();

		appendAsapAttr(sb, Events.ON_OPEN);
		if (adr) HTMLs.appendAttribute(sb, "z.adr", "true");
		return sb.toString();
	}
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final String style = getInnerStyle();
		return style.length() > 0 ? attrs+" style=\""+style+'"': attrs;
	}
	private String getInnerStyle() {
		final StringBuffer sb = new StringBuffer(32)
			.append(HTMLs.getTextRelevantStyle(getRealStyle()));
		HTMLs.appendStyle(sb, "width", getWidth());
		HTMLs.appendStyle(sb, "height", getHeight());
		return sb.toString();
	}
	/** Returns RS_NO_WIDTH|RS_NO_HEIGHT.
	 */
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags()|RS_NO_WIDTH|RS_NO_HEIGHT;
	}

	//-- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		if (!(newChild instanceof Bandpopup))
			throw new UiException("Unsupported child for Bandbox: "+newChild);
		if (_drop != null)
			throw new UiException("At most one bandpopup is allowed, "+this);
		if (super.insertBefore(newChild, refChild)) {
			invalidate();
			_drop = (Bandpopup)newChild;
			return true;
		}
		return false;
	}
	/** Childable. */
	public boolean isChildable() {
		return true;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (child == _drop) _drop = null; //just in case
	}

	//Cloneable//
	public Object clone() {
		final Bandbox clone = (Bandbox)super.clone();
		if (clone._drop != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		_drop = (Bandpopup)getFirstChild();
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (!getChildren().isEmpty()) afterUnmarshal();
	}
}

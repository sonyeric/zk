/* ComponentDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.lang.Classes;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.Evaluator;

/**
 * A component definition.
 * Like class in Java, a {@link ComponentDefinition} defines the behavior
 * of a component.
 *
 * @author tomyeh
 * @see LanguageDefinition
 */
public class ComponentDefinition implements Cloneable, java.io.Serializable {
	private String _name;
	private Milieu _mill;
	private transient LanguageDefinition _langdef;
	/** Either String or Class. */
	private Object _cls;
	private Map _molds, _params;
	private List _props;
	private String _macroURI;

	/** A special definition representing the zk component. */
	public final static ComponentDefinition ZK =
		new ComponentDefinition(null, "zk", Component.class);;

	/** Constructs a native component, i.e., a component implemented by
	 * a Java class.
	 *
	 * @param langdef the language definition, or null if this is a temporary
	 * definition, such as components defined in a page,
	 * doesn't belong to any language.
	 * @param cls the implementation class.
	 */
	public ComponentDefinition(LanguageDefinition langdef, String name,
	Class cls) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (cls != null && !Component.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(cls+" must implement "+Component.class);
			//cls might be assigned later

		_langdef = langdef;
		_name = name;
		_cls = cls;
	}
	/** Constructs a macro component, i.e., a component implemented by
	 * a macro.
	 *
	 * <p>After calling this method, the caller MUST invoke
	 * {@link LanguageDefinition#initMacroDefinition}.
	 *
	 * @param langdef the language definition, or null if this is a temporary
	 * definition doesn't belong to any language.
	 */
	public ComponentDefinition(LanguageDefinition langdef, String name,
	String macroURI) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (macroURI == null || macroURI.length() == 0)
			throw new IllegalArgumentException("empty macroURI");
		_langdef = langdef;
		_name = name;
		_macroURI = macroURI;
	}
	 

	/** Used by deriving class to contruct a 'virtual' definition that
	 * depends on other definitions.
	 * It is currently used only by {@link InstanceDefinition}.
	 */
	protected ComponentDefinition(LanguageDefinition langdef, String name) {
		if (name == null)
			throw new IllegalArgumentException("null name");

		_langdef =langdef;
		_name = name;
	}

	/** Returns the language definition, or null if it is temporty definition
	 * belonging to a page.
	 *
	 * <p>It is never null if this is a page definition ({@link PageDefinition}).
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	/** Sets the language definition.
	 */
	public void setLanguageDefinition(LanguageDefinition langdef) {
		_langdef = langdef;
	}

	/** Returns name of this component definition (never null).
	 * It is unique in the same language, {@link LanguageDefinition}.
	 */
	public String getName() {
		return _name;
	}

	/** Returns the millieu representing this definition.
	 */
	public Milieu getMilieu() {
		if (_mill == null) //no need to synchronized since harmless
			_mill = new Milieu(this);
		return _mill;
	}

	/** Returns whether this is a macro component.
	 * @see #getMacroURI
	 */
	public boolean isMacro() {
		return _macroURI != null;
	}
	/** Returns the macro URI (might be an EL expression),
	 * or null if not a macro.
	 */
	/*package*/ String getMacroURI() {
		return _macroURI;
	}

	/** Returns the class (Class) or the class name (String) that
	 * implements the component.
	 */
	public Object getImplementationClass() {
		return _cls;
	}
	/** Sets the class to implements the component.
	 *
	 * <p>Note: currently, classes specified in lang.xml or lang-addon.xml
	 * must be resolved when loading the files.
	 * However, classes specified in a page (by use of class or use attributes)
	 * might be resolved later because it might be defined by zscript.
	 */
	public void setImplementationClass(Class cls) {
		if (!Component.class.isAssignableFrom(cls))
			throw new UiException(Component.class.getName()+" must be implemented by "+cls);
		_cls = cls;
	}
	/** Sets the class name to implements the component.
	 * Unlike {@link #setImplementationClass(Class)}, the class won't
	 * be resolved until {@link InstanceDefinition#newInstance} or {@link #getImplementationClass}
	 * is used. In other words, the class can be provided later
	 * (thru, usually, zscript).
	 */
	public void setImplementationClass(String clsnm) {
		if (clsnm == null || clsnm.length() == 0)
			throw new UiException("Non-empty class name is required");
		_cls = clsnm;
	}

	/** Creates an component of this definition.
	 *
	 * <p>Note: this method doesn't invoke {@link Milieu#applyProperties}.
	 * It is caller's job to apply these properties if necessary.
	 * Since the value of a property might depend on the component tree,
	 * it is better to assign the component with a proper parent
	 * before calling {@link Milieu#applyProperties}.
	 *
	 * <p>In addition, an application developer can invoke
	 * {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}
	 * instead of {@link #newInstance}, since it handles the initialization
	 * of properties and attributes.
	 * Notice that an application developer can customize
	 * {@link org.zkoss.zk.ui.sys.UiFactory} to provide a different way
	 * to instantiate a component.
	 *
	 * @param page the page the new component belongs to (never null)
	 * @return the new component (never null)
	 */
	public Component newInstance(Page page) {
		final Milieu mill = getMilieu();
		Milieu.setCurrent(mill);
		final Component comp;
		try {
			comp = (Component)
				mill.resolveImplementationClass(page).newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			//theorectically, it shall be reset by AbstractComponent, but..
			Milieu.setCurrent(null);
		}
		return comp;
	}

	/** Adds a mold.
	 */
	public void addMold(String name, String moldUri) {
		if (name == null || moldUri == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || moldUri.length() == 0)
			throw new IllegalArgumentException("empty");
		if (_molds == null) {
			synchronized (this) {
				if (_molds == null) {
					final Map molds = new HashMap(5);
					molds.put(name, moldUri);
					_molds = molds;
					return;
				}
			}
		}
		synchronized (_molds) {
			_molds.put(name, moldUri);
		}
	}
	/** Returns a map of molds, or null if no one is defined.
	 *
	 * <p>Note: to access the returned, you have to use synchronized to
	 * synchronized the returned list (if not null).
	 * Also, a mold might be an EL expression
	 */
	/*package*/ Map getMolds() {
		return _molds;
	}

	/** Adds a parameter.
	 * It is not public because we don't synchronize the access
	 * (so it is called when booting).
	 */
	/*package*/ void addParam(String name, String value) {
		if (name == null || value == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || value.length() == 0)
			throw new IllegalArgumentException("empty");
		if (_params == null) _params = new HashMap(5);
		_params.put(name, value);
	}
	/** Returns the list of parameters, or null if not available.
	 *
	 * <p>Note: to access the returned, you have to use synchronized to
	 * synchronized the returned list (if not null).
	 * Also, a pamater might be an EL expression
	 */
	/*package*/ Map getParams() {
		return _params;
	}

	/** Adds a property initializer.
	 * It will initialize a component when created with is definition.
	 * @param name the member name. The component must have a valid setter
	 * for it.
	 * @param value the value. It might contain expressions (${}).
	 */
	public void addProperty(String name, String value, Condition cond) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");
		final Property prop = new Property(name, value, cond);
		if (_props == null) {
			synchronized (this) {
				if (_props == null) {
					final List props = new LinkedList();
					props.add(prop);
					_props = props;
					return;
				}
			}
		}
		synchronized (_props) {
			_props.add(prop);
		}
	}
	/** Returns the list of properties, or null if no properties at all.
	 *
	 * <p>Note: to access the returned, you have to use synchronized to
	 * synchronized the returned list (if not null).
	 */
	/*package*/ final List getProperties() {
	//Note: we don't allow InstanceDefinition to override it!!
	//Reason: there are two set of properties and it's Milieu's job to handle
		return _props;
	}

	/** Clones this definition and assins with the specified name.
	 */
	public ComponentDefinition clone(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty");

		final ComponentDefinition cd = (ComponentDefinition)clone();
		cd._name = name;
		return cd;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);
	}

	//Object//
	public String toString() {
		return "[ComponentDefinition: "+_name+']';
	}
	public Object clone() {
		try {
			final ComponentDefinition compdef =
				(ComponentDefinition)super.clone();
			if (_props != null) compdef._props = new LinkedList(_props);
			if (_molds != null) compdef._molds = new HashMap(_molds);
			if (_params != null) compdef._params = new HashMap(_params);
			return compdef;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}
}

/**
 * Copyright (c) 2011 - 2014, Lunifera GmbH (Gross Enzersdorf), Loetz KG (Heidelberg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 		Florian Pirchner - Initial implementation
 */
package org.lunifera.xtext.builder.ui.access;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.resource.XtextResourceSet;

public interface IXtextUtilService {

	/**
	 * Returns the project for the given eClass and qualified name that contains
	 * the xtext model. Returns <code>null</code> if no java project is
	 * involved.
	 * 
	 * @param eClass
	 * @param qualifiedName
	 * @return
	 */
	IProject getProject(EClass eClass, String qualifiedName);

	/**
	 * Returns a fully configured XtextResourceSet for the uri defined by eClass
	 * and qualifiedName
	 * 
	 * @param eClass
	 * @param qualifiedName
	 * @return
	 */
	XtextResourceSet getConfiguredXtextResourceFor(EClass eClass,
			String qualifiedName);

	/**
	 * Loads the class for the given eClass and qualifiedName.
	 * 
	 * @param eClass
	 * @param qualifiedName
	 * @return
	 */
	Class<?> loadClass(EClass eClass, String qualifiedName);

	/**
	 * Reloads the class using a new class loader.
	 * 
	 * @param eClass
	 * @param qualifiedName
	 * @return
	 */
	Class<?> reloadClass(EClass eClass, String qualifiedName);
}

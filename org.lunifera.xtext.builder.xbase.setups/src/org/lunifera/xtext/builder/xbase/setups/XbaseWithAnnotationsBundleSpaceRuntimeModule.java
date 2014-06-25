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
package org.lunifera.xtext.builder.xbase.setups;

import org.eclipse.xtext.xbase.annotations.XbaseWithAnnotationsRuntimeModule;
import org.lunifera.dsl.xtext.types.bundles.BundleSpaceTypeProviderFactory;
import org.lunifera.dsl.xtext.types.bundles.BundleSpaceTypeScopeProvider;


@SuppressWarnings("restriction")
public class XbaseWithAnnotationsBundleSpaceRuntimeModule extends
		XbaseWithAnnotationsRuntimeModule {

	// contributed by org.eclipse.xtext.generator.types.TypesGeneratorFragment
	public Class<? extends org.eclipse.xtext.common.types.access.IJvmTypeProvider.Factory> bindIJvmTypeProvider$Factory() {
		return BundleSpaceTypeProviderFactory.class;
	}

	// contributed by org.eclipse.xtext.generator.types.TypesGeneratorFragment
	public Class<? extends org.eclipse.xtext.common.types.xtext.AbstractTypeScopeProvider> bindAbstractTypeScopeProvider() {
		return BundleSpaceTypeScopeProvider.class;
	}

}
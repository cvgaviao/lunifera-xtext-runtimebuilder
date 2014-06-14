package org.lunifera.dsl.xtext.builder.participant.jvmtypes.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.knowhowlab.osgi.testing.assertions.OSGiAssert.setDefaultBundleContext;
import static org.knowhowlab.osgi.testing.assertions.ServiceAssert.assertServiceUnavailable;
import static org.knowhowlab.osgi.testing.utils.BundleUtils.findBundle;
import static org.knowhowlab.osgi.testing.utils.ServiceUtils.getService;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.common.types.JvmType;
import org.junit.Before;
import org.junit.Test;
import org.lunifera.dsl.xtext.builder.participant.jvmtypes.IJvmTypeMetadataService;
import org.lunifera.xtext.builder.metadata.services.IMetadataBuilderService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class JvmTypesBuilderParticipantTest {

	private Bundle extenderBundle;
	private Bundle builderBundle;

	@Before
	public void setup() throws BundleException {

		setDefaultBundleContext(Activator.context);

		extenderBundle = findBundle(Activator.context,
				"org.lunifera.xtext.builder.participant.jvmtypes.extender.tests");
		extenderBundle.stop();

		// restart the metadata service
		builderBundle = findBundle(Activator.context,
				"org.lunifera.xtext.builder.metadata.services");
		builderBundle.stop();
		builderBundle.start();
		getService(Activator.context, IMetadataBuilderService.class, 2000);
	}

	@Test
	public void test_cacheReset() {

		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(service);
		assertNotNull(builderService);

		JvmType type = service.getJvmType(String.class);
		assertEquals("java.lang.String", type.getQualifiedName());

		JvmType testClass = service.getJvmType(getClass());
		assertNull(testClass);

		// the new bundle resets the cache
		builderService.addToBundleSpace(Activator.context.getBundle());
		testClass = service.getJvmType(getClass());
		assertNotNull(testClass);
	}

	@Test
	public void test_getJvmType_WithManuallyAddedBundleSpace() {

		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(service);
		assertNotNull(builderService);

		// add this test bundle to bundle space
		builderService.addToBundleSpace(Activator.context.getBundle());

		JvmType testClass = service.getJvmType(getClass());
		assertNotNull(testClass);

	}

	@Test
	public void test_LoadClass_ThenRemoveParticipant() {
		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(builderService);
		assertNotNull(service);

		// add this test bundle to bundle space
		builderService.addToBundleSpace(Activator.context.getBundle());

		JvmType testClass = service.getJvmType(Class1ToLoad.class);
		assertNotNull(testClass);

		// remove the bundle again from bundle space
		builderService.removeFromBundleSpace(Activator.context.getBundle());
		// then load class2
		JvmType testClass2 = service.getJvmType(Class2ToLoad.class);
		assertNull(testClass2);

		// load the class1 again -> Needs to be cached
		testClass = service.getJvmType(Class1ToLoad.class);
		assertNotNull(testClass);

		// add this test bundle to bundle space again
		builderService.addToBundleSpace(Activator.context.getBundle());

		testClass2 = service.getJvmType(Class2ToLoad.class);
		assertNotNull(testClass2);
	}

	@Test
	public void test_bundleSpace_extenderBundle() throws BundleException,
			InterruptedException {
		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(builderService);
		assertNotNull(service);

		// then load class
		JvmType testClass = service
				.getJvmType("org.lunifera.dsl.xtext.builder.participant.jvmtypes.extender.tests.ExtenderClassToLoad");
		assertNull(testClass);
		JvmType testClass2 = service
				.getJvmType("org.lunifera.dsl.xtext.builder.participant.jvmtypes.extender.tests.ExtenderClass2ToLoad");
		assertNull(testClass2);

		// now start the extender bundle
		assertEquals(Bundle.RESOLVED, extenderBundle.getState());
		extenderBundle.start();
		// wait 500 ms since bundle start is async
		Thread.sleep(500);

		testClass = service
				.getJvmType("org.lunifera.dsl.xtext.builder.participant.jvmtypes.extender.tests.ExtenderClassToLoad");
		assertNotNull(testClass);

		// now stop the extender bundle again
		extenderBundle.stop();
		// wait 500 ms since bundle stop is async
		Thread.sleep(500);

		// test class1 is still available, since cached
		testClass = service
				.getJvmType("org.lunifera.dsl.xtext.builder.participant.jvmtypes.extender.tests.ExtenderClassToLoad");
		assertNotNull(testClass);

		// test class2 can not be loaded, since not available in bundle space
		testClass2 = service
				.getJvmType("org.lunifera.dsl.xtext.builder.participant.jvmtypes.extender.tests.ExtenderClass2ToLoad");
		assertNull(testClass2);
	}

	@Test
	public void test_deactivateService() throws Exception {
		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(service);
		assertNotNull(builderService);

		JvmType type = service.getJvmType(String.class);
		assertEquals("java.lang.String", type.getQualifiedName());

		builderBundle.stop();
		Thread.sleep(500);

		assertServiceUnavailable(IJvmTypeMetadataService.class);
		assertServiceUnavailable(IMetadataBuilderService.class);

		builderBundle.start();
		Thread.sleep(500);

		service = getService(Activator.context, IJvmTypeMetadataService.class,
				1000);
		builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(service);
		assertNotNull(builderService);

		JvmType type2 = service.getJvmType(String.class);
		assertEquals("java.lang.String", type.getQualifiedName());

		assertNotSame(type, type2);

		// since the resource was unloaded, the type loaded with old service is
		// an EProxy now
		InternalEObject iType = (InternalEObject) type;
		assertTrue(iType.eIsProxy());
		assertFalse(EcoreUtil.equals(type, type2));
	}

	@Test
	public void test_cache() throws Exception {
		IJvmTypeMetadataService service = getService(Activator.context,
				IJvmTypeMetadataService.class, 1000);
		IMetadataBuilderService builderService = getService(Activator.context,
				IMetadataBuilderService.class, 1000);
		assertNotNull(service);
		assertNotNull(builderService);

		JvmType type = service.getJvmType(String.class);
		assertEquals("java.lang.String", type.getQualifiedName());

		JvmType type2 = service.getJvmType(String.class);
		assertEquals("java.lang.String", type.getQualifiedName());

		InternalEObject iType = (InternalEObject) type;
		assertFalse(iType.eIsProxy());
		assertSame(type, type2);
	}
}
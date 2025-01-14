/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.cdi.tck.interceptors.tests.bindings.aroundConstruct;

import static org.jboss.cdi.tck.interceptors.InterceptorsSections.BINDING_INT_TO_COMPONENT;
import static org.jboss.cdi.tck.interceptors.InterceptorsSections.INT_BINDING_TYPES;
import static org.jboss.cdi.tck.interceptors.InterceptorsSections.INT_RESOLUTION;
import static org.jboss.cdi.tck.interceptors.InterceptorsSections.INVOCATIONCONTEXT;
import static org.jboss.cdi.tck.util.ActionSequence.assertSequenceDataEquals;

import jakarta.enterprise.inject.Instance;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.util.ActionSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * <p>
 * This test was originally part of the Weld test suite.
 * <p>
 *
 * @author Jozef Hartinger
 * @author Martin Kouba
 */
@SpecVersion(spec = "interceptors", version = "1.2")
public class ConstructorInterceptionTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
                .withTestClassPackage(ConstructorInterceptionTest.class)
                .build();
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertion(section = INT_BINDING_TYPES, id = "a")
    @SpecAssertion(section = INT_BINDING_TYPES, id = "b")
    @SpecAssertion(section = BINDING_INT_TO_COMPONENT, id = "ac")
    public void testConstructorLevelBinding(Instance<BeanWithConstructorLevelBinding> instance) {
        ActionSequence.reset();
        instance.get();
        assertSequenceDataEquals(AlphaInterceptor2.class, BeanWithConstructorLevelBinding.class);
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertion(section = BINDING_INT_TO_COMPONENT, id = "bc")
    @SpecAssertion(section = INT_RESOLUTION, id = "da")
    @SpecAssertion(section = INT_RESOLUTION, id = "db")
    @SpecAssertion(section = INT_RESOLUTION, id = "dc")
    public void testMultipleConstructorLevelBinding(Instance<BeanWithMultipleConstructorLevelBinding> instance) {
        ActionSequence.reset();
        instance.get();
        assertSequenceDataEquals(AlphaInterceptor2.class, BravoInterceptor.class, BeanWithMultipleConstructorLevelBinding.class);
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertion(section = INT_BINDING_TYPES, id = "a")
    @SpecAssertion(section = INT_BINDING_TYPES, id = "b")
    public void testTypeLevelBinding(Instance<BeanWithTypeLevelBinding> instance) {
        ActionSequence.reset();
        instance.get();
        assertSequenceDataEquals(AlphaInterceptor1.class, BeanWithTypeLevelBinding.class);
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertion(section = BINDING_INT_TO_COMPONENT, id = "c")
    @SpecAssertion(section = INVOCATIONCONTEXT, id = "i")
    public void testTypeLevelAndConstructorLevelBinding(Instance<BeanWithConstructorLevelAndTypeLevelBinding> instance) {
        ActionSequence.reset();
        instance.get();
        assertSequenceDataEquals(AlphaInterceptor1.class, BravoInterceptor.class, BeanWithConstructorLevelAndTypeLevelBinding.class);
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertion(section = BINDING_INT_TO_COMPONENT, id = "da")
    public void testOverridingTypeLevelBinding(Instance<BeanOverridingTypeLevelBinding> instance) {
        ActionSequence.reset();
        instance.get();
        assertSequenceDataEquals(AlphaInterceptor2.class, BeanOverridingTypeLevelBinding.class);
    }
}

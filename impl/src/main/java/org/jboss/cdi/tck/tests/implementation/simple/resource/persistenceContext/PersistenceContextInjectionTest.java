/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.implementation.simple.resource.persistenceContext;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.TestGroups.LIFECYCLE;
import static org.jboss.cdi.tck.TestGroups.PERSISTENCE;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Injection of persistence related objects.
 * 
 * @author David Allen
 * @author Martin Kouba
 */
@Test(groups = { INTEGRATION, PERSISTENCE, LIFECYCLE })
@SpecVersion(spec = "cdi", version = "20091101")
public class PersistenceContextInjectionTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(PersistenceContextInjectionTest.class).withBeansXml("beans.xml")
                .withDefaultPersistenceXml().build();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "3.6.1", id = "cc"), @SpecAssertion(section = "7.3.6", id = "lb"),
            @SpecAssertion(section = "7.3.6", id = "mc") })
    public void testInjectionOfPersistenceContext() {
        ServiceBean serviceBean = getInstanceByType(ServiceBean.class);
        ManagedBean managedBean = serviceBean.getManagedBean();
        assert managedBean.getPersistenceContext() != null : "Persistence context was not injected into bean";
        assert serviceBean.validateEntityManager();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "3.6.1", id = "dd"), @SpecAssertion(section = "7.3.6", id = "lc"),
            @SpecAssertion(section = "7.3.6", id = "me") })
    public void testInjectionOfPersistenceUnit() {
        Bean<ManagedBean> managedBeanBean = getBeans(ManagedBean.class).iterator().next();
        CreationalContext<ManagedBean> managedBeanCc = getCurrentManager().createCreationalContext(managedBeanBean);
        ManagedBean managedBean = managedBeanBean.create(managedBeanCc);
        assert managedBean.getPersistenceUnit() != null : "Persistence unit was not injected into bean";
        assert managedBean.getPersistenceUnit().isOpen() : "Persistence unit not open injected into bean";
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "7.3.6", id = "md") })
    public void testPassivationOfPersistenceContext() throws Exception {
        Bean<ManagedBean> managedBeanBean = getBeans(ManagedBean.class).iterator().next();
        CreationalContext<ManagedBean> managedBeanCc = getCurrentManager().createCreationalContext(managedBeanBean);
        ManagedBean managedBean = managedBeanBean.create(managedBeanCc);
        managedBean = (ManagedBean) deserialize(serialize(managedBean));
        assert managedBean.getPersistenceContext() != null : "Persistence context was not injected into bean";
        assert managedBean.getPersistenceContext().getDelegate() != null : "Persistence context not deserialized correctly";
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "7.3.6", id = "lc"), @SpecAssertion(section = "7.3.6", id = "mf") })
    public void testPassivationOfPersistenceUnit() throws Exception {
        Bean<ManagedBean> managedBeanBean = getBeans(ManagedBean.class).iterator().next();
        CreationalContext<ManagedBean> managedBeanCc = getCurrentManager().createCreationalContext(managedBeanBean);
        ManagedBean managedBean = managedBeanBean.create(managedBeanCc);
        managedBean = (ManagedBean) deserialize(serialize(managedBean));
        assert managedBean.getPersistenceUnit() != null : "Persistence unit was not injected into bean";
        assert managedBean.getPersistenceUnit().isOpen() : "Persistence unit not open injected into bean";
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "3.6.1", id = "hh"), @SpecAssertion(section = "3.6.2", id = "ab") })
    public void testBeanTypesAndBindingTypesOfPersistenceContext() {
        Bean<EntityManager> manager = getBeans(EntityManager.class, new AnnotationLiteral<Database>() {
        }).iterator().next();
        assert manager.getTypes().size() == 2;
        assert rawTypeSetMatches(manager.getTypes(), EntityManager.class, Object.class);
        assert manager.getQualifiers().size() == 2;
        assert annotationSetMatches(manager.getQualifiers(), Any.class, Database.class);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "3.6.2", id = "ac") })
    public void testBeanTypesOfPersistenceUnit() {
        Bean<EntityManagerFactory> factory = getBeans(EntityManagerFactory.class, new AnnotationLiteral<Database>() {
        }).iterator().next();
        assert factory.getTypes().size() == 2;
        assert rawTypeSetMatches(factory.getTypes(), EntityManagerFactory.class, Object.class);
    }
}
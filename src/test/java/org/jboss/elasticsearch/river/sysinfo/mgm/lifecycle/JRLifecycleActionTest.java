/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle;

import org.elasticsearch.client.internal.InternalClusterAdminClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link JRLifecycleAction}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class JRLifecycleActionTest {

	@Test
	public void constructor() {
		Assert.assertEquals(JRLifecycleAction.NAME, JRLifecycleAction.INSTANCE.name());
	}

	@Test
	public void newRequestBuilder() {
		InternalClusterAdminClient client = Mockito.mock(InternalClusterAdminClient.class);

		JRLifecycleRequestBuilder rb = JRLifecycleAction.INSTANCE.newRequestBuilder(client);
		Assert.assertNotNull(rb);
	}

	@Test
	public void newResponse() {
		JRLifecycleResponse rb = JRLifecycleAction.INSTANCE.newResponse();
		Assert.assertNotNull(rb);
	}
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.riverslist;

import org.elasticsearch.client.ClusterAdminClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link ListRiversAction}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class ListRiversActionTest {

	@Test
	public void constructor() {
		Assert.assertEquals(ListRiversAction.NAME, ListRiversAction.INSTANCE.name());
	}

	@Test
	public void newRequestBuilder() {
		ClusterAdminClient client = Mockito.mock(ClusterAdminClient.class);

		ListRiversRequestBuilder rb = ListRiversAction.INSTANCE.newRequestBuilder(client);
		Assert.assertNotNull(rb);
	}

	@Test
	public void newResponse() {
		ListRiversResponse rb = ListRiversAction.INSTANCE.newResponse();
		Assert.assertNotNull(rb);
	}
}

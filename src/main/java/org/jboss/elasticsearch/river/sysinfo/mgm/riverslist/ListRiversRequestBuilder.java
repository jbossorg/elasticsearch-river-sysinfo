/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.riverslist;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.nodes.NodesOperationRequestBuilder;
import org.elasticsearch.client.ClusterAdminClient;

/**
 * Request builder to get lit of all Sysinfo rivers in cluster.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class ListRiversRequestBuilder extends
		NodesOperationRequestBuilder<ListRiversRequest, ListRiversResponse, ListRiversRequestBuilder> {

	public ListRiversRequestBuilder(ClusterAdminClient client) {
		super(client, new ListRiversRequest());
	}

	@Override
	protected void doExecute(ActionListener<ListRiversResponse> listener) {
		client.execute(ListRiversAction.INSTANCE, request, listener);
	}

}

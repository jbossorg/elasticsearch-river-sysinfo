/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.riverslist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.support.nodes.TransportNodesOperationAction;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.jboss.elasticsearch.river.sysinfo.SysinfoRiver;

/**
 * Transport action to list all Sysinfo Rivers running in ES cluster.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
@SuppressWarnings("rawtypes")
public class TransportListRiversAction extends
    TransportNodesOperationAction<ListRiversRequest, ListRiversResponse, NodeListRiversRequest, NodeListRiversResponse> {

  protected final static ESLogger logger = Loggers.getLogger(TransportListRiversAction.class);

  @Inject
  public TransportListRiversAction(Settings settings, ClusterName clusterName, ThreadPool threadPool,
      ClusterService clusterService, TransportService transportService) {
    super(settings, clusterName, threadPool, clusterService, transportService);
  }

  @Override
  protected String executor() {
    return ThreadPool.Names.MANAGEMENT;
  }

  @Override
  protected ListRiversResponse newResponse(ListRiversRequest request, AtomicReferenceArray responses) {
    final List<NodeListRiversResponse> nodesInfos = new ArrayList<NodeListRiversResponse>();
    for (int i = 0; i < responses.length(); i++) {
      Object resp = responses.get(i);
      if (resp instanceof NodeListRiversResponse) {
        nodesInfos.add((NodeListRiversResponse) resp);
      }
    }
    return new ListRiversResponse(clusterName, nodesInfos.toArray(new NodeListRiversResponse[nodesInfos.size()]));
  }

  @Override
  protected boolean accumulateExceptions() {
    return false;
  }

  @Override
  protected NodeListRiversResponse nodeOperation(NodeListRiversRequest nodeRequest) throws ElasticSearchException {
    logger.debug("Go to look for Sysinfo rivers on this node");
    return new NodeListRiversResponse(clusterService.localNode(), SysinfoRiver.getRunningInstances());
  }

  @Override
  protected String transportAction() {
    return ListRiversAction.NAME;
  }

  @Override
  protected ListRiversRequest newRequest() {
    return new ListRiversRequest();
  }

  @Override
  protected NodeListRiversRequest newNodeRequest() {
    return new NodeListRiversRequest();
  }

  @Override
  protected NodeListRiversRequest newNodeRequest(String nodeId, ListRiversRequest request) {
    return new NodeListRiversRequest(nodeId);
  }

  @Override
  protected NodeListRiversResponse newNodeResponse() {
    return new NodeListRiversResponse(clusterService.localNode());
  }

}

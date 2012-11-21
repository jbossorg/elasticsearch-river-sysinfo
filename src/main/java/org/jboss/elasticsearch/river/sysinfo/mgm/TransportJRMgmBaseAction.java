/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.support.nodes.TransportNodesOperationAction;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.jboss.elasticsearch.river.sysinfo.IRiverMgm;
import org.jboss.elasticsearch.river.sysinfo.SysinfoRiver;

/**
 * Base abstract transport action for River management operations.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
@SuppressWarnings("rawtypes")
public abstract class TransportJRMgmBaseAction<Request extends JRMgmBaseRequest, Response extends JRMgmBaseResponse, NodeRequest extends NodeJRMgmBaseRequest<Request>, NodeResponse extends NodeJRMgmBaseResponse>
    extends TransportNodesOperationAction<Request, Response, NodeRequest, NodeResponse> {

  protected final ESLogger logger;

  @Inject
  public TransportJRMgmBaseAction(Settings settings, ClusterName clusterName, ThreadPool threadPool,
      ClusterService clusterService, TransportService transportService) {
    super(settings, clusterName, threadPool, clusterService, transportService);
    logger = Loggers.getLogger(getClass());
  }

  @Override
  protected String executor() {
    return ThreadPool.Names.MANAGEMENT;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Response newResponse(Request request, AtomicReferenceArray responses) {
    final List<NodeResponse> nodesInfos = new ArrayList<NodeResponse>();
    for (int i = 0; i < responses.length(); i++) {
      Object resp = responses.get(i);
      if (resp instanceof NodeJRMgmBaseResponse) {
        nodesInfos.add((NodeResponse) resp);
      }
    }
    return newResponse(clusterName, nodesInfos.toArray(newNodeResponseArray(nodesInfos.size())));
  }

  protected abstract NodeResponse[] newNodeResponseArray(int len);

  protected abstract Response newResponse(ClusterName clusterName, NodeResponse[] array);

  @Override
  protected boolean accumulateExceptions() {
    return false;
  }

  @Override
  protected NodeResponse nodeOperation(NodeRequest nodeRequest) throws ElasticSearchException {
    Request req = nodeRequest.getRequest();
    logger.debug("Go to look for Sysinfo river '{}' on this node", req.getRiverName());
    IRiverMgm river = SysinfoRiver.getRunningInstance(req.getRiverName());
    if (river == null) {
      logger.debug("Sysinfo River {} not found on this node", req.getRiverName());
      return newNodeResponse();
    } else {
      logger.debug("Sysinfo River {} found on this node, go to call mgm operation on it {}", req.getRiverName(), req);
      try {
        return performOperationOnJiraRiver(river, req, clusterService.localNode());
      } catch (Exception e) {
        throw new ElasticSearchException(e.getMessage(), e);
      }
    }
  }

  /**
   * Implement in subclass to perform necessary management operation on river instance. This method is called only on
   * node where river really runs.
   * 
   * @param river instance to perform operation on, never null
   * @param req request for operation to process
   * @param node this operation runs on (used to construct response etc.)
   * @return node response with operation result
   * @throws Exception if something is wrong
   */
  protected abstract NodeResponse performOperationOnJiraRiver(IRiverMgm river, Request req, DiscoveryNode node)
      throws Exception;
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm;

import java.util.concurrent.atomic.AtomicReferenceArray;

import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.transport.DummyTransportAddress;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.threadpool.ThreadPool;
import org.jboss.elasticsearch.river.sysinfo.IRiverMgm;
import org.jboss.elasticsearch.river.sysinfo.SysinfoRiver;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.JRLifecycleCommand;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.JRLifecycleRequest;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.JRLifecycleResponse;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.NodeJRLifecycleRequest;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.NodeJRLifecycleResponse;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.TransportJRLifecycleAction;
import org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.TransportJRLifecycleActionTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link TransportJRMgmBaseAction}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class TransportJRMgmBaseActionTest {

  protected static final ClusterName clusterName = TransportJRLifecycleActionTest.clusterName;
  DiscoveryNode dn = new DiscoveryNode("aa", DummyTransportAddress.INSTANCE);
  DiscoveryNode dn2 = new DiscoveryNode("aa2", DummyTransportAddress.INSTANCE);

  @Test
  public void constructor() {
    TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
    Assert.assertNotNull(tested.logger);
    Assert.assertEquals(
        "org.elasticsearch.org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle.TransportJRLifecycleAction",
        tested.logger.getName());
  }

  @Test
  public void newResponse() {
    TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);

    {
      AtomicReferenceArray<NodeJRLifecycleResponse> responses = new AtomicReferenceArray<NodeJRLifecycleResponse>(0);
      JRLifecycleResponse res = tested.newResponse(null, responses);
      Assert.assertEquals(clusterName.value(), res.getClusterName());
      Assert.assertEquals(0, res.nodes().length);
    }

    {
      AtomicReferenceArray<NodeJRLifecycleResponse> responses = new AtomicReferenceArray<NodeJRLifecycleResponse>(
          new NodeJRLifecycleResponse[] { new NodeJRLifecycleResponse(dn), new NodeJRLifecycleResponse(dn2) });
      JRLifecycleResponse res = tested.newResponse(null, responses);
      Assert.assertEquals(clusterName.value(), res.getClusterName());
      Assert.assertEquals(2, res.nodes().length);
    }

  }

  @Test
  public void executor() {
    TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
    Assert.assertEquals(ThreadPool.Names.MANAGEMENT, tested.executor());
  }

  @Test
  public void accumulateExceptions() {
    TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
    Assert.assertEquals(false, tested.accumulateExceptions());
  }

  @Test
  public void nodeOperation() {
    SysinfoRiver.clearRunningInstances();

    IRiverMgm jiraRiverMock = Mockito.mock(IRiverMgm.class);
    RiverName riverName = new RiverName("sysinfo", "myRiver");
    Mockito.when(jiraRiverMock.riverName()).thenReturn(riverName);
    SysinfoRiver.addRunningInstance(jiraRiverMock);
    TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);

    Mockito.reset(jiraRiverMock);
    {
      NodeJRLifecycleRequest nodeRequest = new NodeJRLifecycleRequest("ndid", new JRLifecycleRequest("unknownRiver",
          JRLifecycleCommand.STOP));
      NodeJRLifecycleResponse resp = tested.nodeOperation(nodeRequest);
      Assert.assertNotNull(resp);
      Assert.assertFalse(resp.riverFound);
      Mockito.verifyZeroInteractions(jiraRiverMock);
    }

    Mockito.reset(jiraRiverMock);
    {
      NodeJRLifecycleRequest nodeRequest = new NodeJRLifecycleRequest("ndid", new JRLifecycleRequest("myRiver",
          JRLifecycleCommand.STOP));
      NodeJRLifecycleResponse resp = tested.nodeOperation(nodeRequest);
      Assert.assertNotNull(resp);
      Assert.assertTrue(resp.riverFound);
      Mockito.verify(jiraRiverMock).stop();
      Mockito.verifyNoMoreInteractions(jiraRiverMock);
    }

  }

}

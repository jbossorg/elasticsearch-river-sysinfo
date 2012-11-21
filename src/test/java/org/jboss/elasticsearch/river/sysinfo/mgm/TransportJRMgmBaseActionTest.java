/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm;

import org.junit.Test;

/**
 * Unit test for {@link TransportJRMgmBaseAction}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class TransportJRMgmBaseActionTest {

  @Test
  public void nothing_for_now() {

  }

  //
  // protected static final ClusterName clusterName = TransportJRLifecycleActionTest.clusterName;
  // DiscoveryNode dn = new DiscoveryNode("aa", DummyTransportAddress.INSTANCE);
  // DiscoveryNode dn2 = new DiscoveryNode("aa2", DummyTransportAddress.INSTANCE);
  //
  // @Test
  // public void constructor() {
  // ListRiversAction tested = TransportListRiversActionTest.prepareTestedInstance(clusterName);
  // Assert.assertNotNull(tested.logger);
  // Assert.assertEquals(
  // "org.elasticsearch.org.jboss.elasticsearch.river.jira.mgm.lifecycle.TransportJRLifecycleAction",
  // tested.logger.getName());
  // }
  //
  // @Test
  // public void newResponse() {
  // TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
  //
  // {
  // AtomicReferenceArray<NodeJRLifecycleResponse> responses = new AtomicReferenceArray<NodeJRLifecycleResponse>(0);
  // JRLifecycleResponse res = tested.newResponse(null, responses);
  // Assert.assertEquals(clusterName.value(), res.getClusterName());
  // Assert.assertEquals(0, res.nodes().length);
  // }
  //
  // {
  // AtomicReferenceArray<NodeJRLifecycleResponse> responses = new AtomicReferenceArray<NodeJRLifecycleResponse>(
  // new NodeJRLifecycleResponse[] { new NodeJRLifecycleResponse(dn), new NodeJRLifecycleResponse(dn2) });
  // JRLifecycleResponse res = tested.newResponse(null, responses);
  // Assert.assertEquals(clusterName.value(), res.getClusterName());
  // Assert.assertEquals(2, res.nodes().length);
  // }
  //
  // }
  //
  // @Test
  // public void executor() {
  // TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
  // Assert.assertEquals(ThreadPool.Names.MANAGEMENT, tested.executor());
  // }
  //
  // @Test
  // public void accumulateExceptions() {
  // TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
  // Assert.assertEquals(false, tested.accumulateExceptions());
  // }
  //
  // @Test
  // public void nodeOperation() {
  // IRiverMgm jiraRiverMock = Mockito.mock(IRiverMgm.class);
  // RiverName riverName = new RiverName("sysinfo", "myRiver");
  // Mockito.when(jiraRiverMock.riverName()).thenReturn(riverName);
  // SysinfoRiver.addRunningInstance(jiraRiverMock);
  // TransportJRLifecycleAction tested = TransportJRLifecycleActionTest.prepareTestedInstance(clusterName);
  //
  // Mockito.reset(jiraRiverMock);
  // {
  // NodeJRLifecycleRequest nodeRequest = new NodeJRLifecycleRequest("ndid", new JRLifecycleRequest("unknownRiver",
  // JRLifecycleCommand.STOP));
  // NodeJRLifecycleResponse resp = tested.nodeOperation(nodeRequest);
  // Assert.assertNotNull(resp);
  // Assert.assertFalse(resp.riverFound);
  // Mockito.verifyZeroInteractions(jiraRiverMock);
  // }
  //
  // Mockito.reset(jiraRiverMock);
  // {
  // NodeJRLifecycleRequest nodeRequest = new NodeJRLifecycleRequest("ndid", new JRLifecycleRequest("myRiver",
  // JRLifecycleCommand.STOP));
  // NodeJRLifecycleResponse resp = tested.nodeOperation(nodeRequest);
  // Assert.assertNotNull(resp);
  // Assert.assertTrue(resp.riverFound);
  // Mockito.verify(jiraRiverMock).stop(true);
  // Mockito.verifyNoMoreInteractions(jiraRiverMock);
  // }
  //
  // }

}

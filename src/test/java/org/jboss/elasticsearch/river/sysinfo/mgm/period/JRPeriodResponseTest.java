/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.period;

import java.io.IOException;

import junit.framework.Assert;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.transport.DummyTransportAddress;
import org.jboss.elasticsearch.river.sysinfo.mgm.JRMgmBaseResponse;
import org.junit.Test;

/**
 * Unit test for {@link JRPeriodResponse} and {@link JRMgmBaseResponse}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class JRPeriodResponseTest {

	@Test
	public void constructor_filling() {
		ClusterName cn = new ClusterName("mycluster");

		NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[0];
		JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);

		Assert.assertEquals(cn, tested.getClusterName());
		Assert.assertEquals(nodes, tested.getNodes());
	}

	@Test
	public void serialization() throws IOException {
		ClusterName cn = new ClusterName("mycluster");

		DiscoveryNode dn = new DiscoveryNode("aa", DummyTransportAddress.INSTANCE, Version.CURRENT);
		DiscoveryNode dn2 = new DiscoveryNode("aa2", DummyTransportAddress.INSTANCE, Version.CURRENT);
		DiscoveryNode dn3 = new DiscoveryNode("aa3", DummyTransportAddress.INSTANCE, Version.CURRENT);

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] {};
			JRPeriodResponse testedSrc = new JRPeriodResponse(cn, nodes);
			performSerializationAndBasicAsserts(testedSrc);

		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] { new NodeJRPeriodResponse(dn, false, false),
					new NodeJRPeriodResponse(dn2, false, false), new NodeJRPeriodResponse(dn3, true, true) };
			JRPeriodResponse testedSrc = new JRPeriodResponse(cn, nodes);
			JRPeriodResponse testedTarget = performSerializationAndBasicAsserts(testedSrc);

			Assert.assertEquals(testedSrc.getNodes()[0].getNode().getId(), testedTarget.getNodes()[0].getNode().getId());
			Assert.assertEquals(testedSrc.getNodes()[1].getNode().getId(), testedTarget.getNodes()[1].getNode().getId());
			Assert.assertEquals(testedSrc.getNodes()[2].getNode().getId(), testedTarget.getNodes()[2].getNode().getId());
		}

	}

	private JRPeriodResponse performSerializationAndBasicAsserts(JRPeriodResponse testedSrc) throws IOException {
		BytesStreamOutput out = new BytesStreamOutput();
		testedSrc.writeTo(out);
		JRPeriodResponse testedTarget = new JRPeriodResponse();
		testedTarget.readFrom(new BytesStreamInput(out.bytes()));

		Assert.assertEquals(testedSrc.getClusterName(), testedTarget.getClusterName());
		Assert.assertEquals(testedSrc.getNodes().length, testedTarget.getNodes().length);

		return testedTarget;
	}

	@Test
	public void getSuccessNodeResponse() {

		ClusterName cn = new ClusterName("mycluster");

		DiscoveryNode dn = new DiscoveryNode("aa", DummyTransportAddress.INSTANCE, Version.CURRENT);
		DiscoveryNode dn2 = new DiscoveryNode("aa2", DummyTransportAddress.INSTANCE, Version.CURRENT);
		DiscoveryNode dn3 = new DiscoveryNode("aa3", DummyTransportAddress.INSTANCE, Version.CURRENT);

		{
			JRPeriodResponse tested = new JRPeriodResponse();
			Assert.assertNull(tested.getSuccessNodeResponse());
		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[0];
			JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);
			Assert.assertNull(tested.getSuccessNodeResponse());
		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] { new NodeJRPeriodResponse(dn, false, false) };
			JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);
			Assert.assertNull(tested.getSuccessNodeResponse());
		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] { new NodeJRPeriodResponse(dn, true, true) };
			JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);
			Assert.assertEquals(nodes[0], tested.getSuccessNodeResponse());
		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] { new NodeJRPeriodResponse(dn, true, false) };
			JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);
			Assert.assertEquals(nodes[0], tested.getSuccessNodeResponse());
		}

		{
			NodeJRPeriodResponse[] nodes = new NodeJRPeriodResponse[] { new NodeJRPeriodResponse(dn, false, false),
					new NodeJRPeriodResponse(dn2, false, false), new NodeJRPeriodResponse(dn3, true, true) };
			JRPeriodResponse tested = new JRPeriodResponse(cn, nodes);
			Assert.assertEquals(nodes[2], tested.getSuccessNodeResponse());
		}

	}

}

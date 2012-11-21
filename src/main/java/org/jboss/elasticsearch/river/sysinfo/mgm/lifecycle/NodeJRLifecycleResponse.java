package org.jboss.elasticsearch.river.sysinfo.mgm.lifecycle;

import org.elasticsearch.cluster.node.DiscoveryNode;
import org.jboss.elasticsearch.river.sysinfo.mgm.NodeJRMgmBaseResponse;

/**
 * SysinfoRiver lifecycle command node response.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class NodeJRLifecycleResponse extends NodeJRMgmBaseResponse {

  protected NodeJRLifecycleResponse() {
  }

  /**
   * Create response with values to be send back to requestor. <code>riverFound</code> is set to <code>false</code>.
   * 
   * @param node this response is for.
   */
  public NodeJRLifecycleResponse(DiscoveryNode node) {
    super(node);
  }

  /**
   * Create response with values to be send back to requestor.
   * 
   * @param node this response is for.
   * @param riverFound set to true if you found river on this node
   */
  public NodeJRLifecycleResponse(DiscoveryNode node, boolean riverFound) {
    super(node, riverFound);
  }

}

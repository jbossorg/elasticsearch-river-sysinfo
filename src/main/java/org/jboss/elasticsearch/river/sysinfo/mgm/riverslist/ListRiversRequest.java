/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.mgm.riverslist;

import org.elasticsearch.action.support.nodes.NodesOperationRequest;

/**
 * Request to list names of all Sysinfo Rivers running in ES cluster.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class ListRiversRequest extends NodesOperationRequest {

  public ListRiversRequest() {
    super();
  }

}

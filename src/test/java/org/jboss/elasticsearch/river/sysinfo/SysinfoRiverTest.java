/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsException;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.jboss.elasticsearch.river.sysinfo.esclient.SourceClientESClient;
import org.jboss.elasticsearch.river.sysinfo.esclient.SourceClientESTransportClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link SysinfoRiver}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class SysinfoRiverTest {

  @SuppressWarnings("unchecked")
  @Test
  public void configure_sourceClient() throws Exception {

    // case - local es_commection
    {
      Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_local.json");
      SysinfoRiver tested = prepareRiverInstanceForTest(null);
      tested.configure(settings);
      Assert.assertEquals(SourceClientESClient.class, tested.sourceClient.getClass());
    }

    // case - remote es_commection
    {
      Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_remote.json");
      SysinfoRiver tested = prepareRiverInstanceForTest(null);
      tested.configure(settings);
      Assert.assertEquals(SourceClientESTransportClient.class, tested.sourceClient.getClass());
      Assert.assertEquals(2, ((SourceClientESTransportClient) tested.sourceClient).getTransportAddresses().length);
    }

    // TODO case - REST es_commection

    // case - invalid es_commection
    {
      try {
        Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_local.json");
        ((Map<String, Object>) settings.get("es_connection")).put("type", "nonsense");
        SysinfoRiver tested = prepareRiverInstanceForTest(null);
        tested.configure(settings);
        Assert.fail("SettingsException must be thrown");
      } catch (SettingsException e) {
        // OK
      }
    }
  }

  @Test
  public void configure_indexers() throws Exception {

    // case - missing indexers
    {
      try {
        Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_local.json");
        settings.remove("indexers");
        SysinfoRiver tested = prepareRiverInstanceForTest(null);
        tested.configure(settings);
        Assert.fail("SettingsException must be thrown");
      } catch (SettingsException e) {
        // OK
      }
    }

    // case - empty indexers
    {
      try {
        Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_local.json");
        ((List<?>) settings.get("indexers")).clear();
        SysinfoRiver tested = prepareRiverInstanceForTest(null);
        tested.configure(settings);
        Assert.fail("SettingsException must be thrown");
      } catch (SettingsException e) {
        // OK
      }
    }

    // case - OK read
    {
      Map<String, Object> settings = Utils.loadJSONFromJarPackagedFile("/river_configuration_test_conn_local.json");
      SysinfoRiver tested = prepareRiverInstanceForTest(null);
      tested.configure(settings);
      Assert.assertEquals(2, tested.indexers.size());
      SysinfoIndexer idxr = tested.indexers.get(0);
      Assert.assertEquals(tested.sourceClient, idxr.sourceClient);
      Assert.assertEquals(tested.client, idxr.targetClient);
      Assert.assertEquals(SysinfoType.CLUSTER_HEALTH, idxr.infoType);
      Assert.assertEquals("my_index_1", idxr.indexName);
      Assert.assertEquals("my_type_1", idxr.typeName);
      Assert.assertEquals(60 * 1000, idxr.indexingPeriod);
      Assert.assertNotNull(idxr.params);
    }

  }

  @Test
  public void start() throws Exception {
    SysinfoRiver tested = prepareRiverInstanceForTest(null);
    SourceClient scMock = tested.sourceClient;

    // case - exception if started already
    {
      tested.closed = false;
      try {
        tested.start();
        Assert.fail("IllegalStateException must be thrown");
      } catch (IllegalStateException e) {
        // OK
        Assert.assertFalse(tested.closed);
        Assert.assertEquals(0, tested.indexerThreads.size());
        Mockito.verifyZeroInteractions(scMock);
      }
    }

    // case - no exception if indexers list is empty
    {
      Mockito.reset(scMock);
      tested.closed = true;
      tested.start();
      Assert.assertFalse(tested.closed);
      Assert.assertEquals(0, tested.indexerThreads.size());
      Mockito.verify(scMock).start();
    }

    // case - check threads created and started for indexers
    {
      Mockito.reset(scMock);
      tested.closed = true;
      tested.indexers.add(indexerMock(SysinfoType.CLUSTER_HEALTH));
      tested.indexers.add(indexerMock(SysinfoType.CLUSTER_STATE));
      tested.start();
      Assert.assertFalse(tested.closed);
      Assert.assertEquals(2, tested.indexerThreads.size());
      Mockito.verify(scMock).start();
      // give threads chance to run so we can do next assertion on this method
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // nothing to do
      }
      for (SysinfoIndexer i : tested.indexers) {
        Mockito.verify(i).run();
      }

    }
  }

  @Test
  public void close() throws Exception {
    SysinfoRiver tested = prepareRiverInstanceForTest(null);
    SourceClient scMock = tested.sourceClient;

    // case - no errors on empty indexers list and unstarted river
    {
      tested.close();
      Assert.assertTrue(tested.closed);
      Mockito.verify(scMock).close();
    }

    // case - successful close
    {
      Mockito.reset(scMock);
      tested.closed = false;
      tested.indexers.add(indexerMock(SysinfoType.CLUSTER_HEALTH));
      tested.indexers.add(indexerMock(SysinfoType.CLUSTER_STATE));
      List<Thread> t = new ArrayList<Thread>();
      t.add(Mockito.mock(Thread.class));
      t.add(Mockito.mock(Thread.class));
      tested.indexerThreads.addAll(t);

      tested.close();
      Assert.assertTrue(tested.closed);
      Assert.assertEquals(0, tested.indexerThreads.size());
      Mockito.verify(scMock).close();
      for (SysinfoIndexer i : tested.indexers) {
        Mockito.verify(i).close();
      }
      for (Thread i : t) {
        Mockito.verify(i).interrupt();
      }
    }
  }

  /**
   * Prepare mock indexer of given type
   * 
   * @return
   */
  protected SysinfoIndexer indexerMock(SysinfoType type) {
    SysinfoIndexer i = Mockito.mock(SysinfoIndexer.class);
    i.infoType = type;
    return i;
  }

  /**
   * Prepare {@link JiraRiver} instance for unit test, with Mockito moceked elasticSearchClient.
   * 
   * @param toplevelSettings settings to be passed to the river configuring constructor. If null then river is
   *          constructed without configuration.
   * @return instance for tests
   * @throws Exception from constructor
   */
  public static SysinfoRiver prepareRiverInstanceForTest(Map<String, Object> toplevelSettings) throws Exception {
    Map<String, Object> settings = new HashMap<String, Object>();
    if (toplevelSettings != null)
      settings.putAll(toplevelSettings);

    Settings gs = mock(Settings.class);
    RiverSettings rs = new RiverSettings(gs, settings);
    Client clientMock = mock(Client.class);
    SysinfoRiver ret;
    if (toplevelSettings != null) {
      ret = new SysinfoRiver(new RiverName("sysinfo", "my_sysinfo_river"), rs, clientMock);
    } else {
      ret = new SysinfoRiver(new RiverName("sysinfo", "my_sysinfo_river"), rs);
      ret.client = clientMock;
    }
    ret.sourceClient = Mockito.mock(SourceClient.class);
    return ret;

  }

}

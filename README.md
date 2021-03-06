System info River for Elasticsearch
===================================

[![Build Status](https://travis-ci.org/searchisko/elasticsearch-river-sysinfo.svg?branch=master)](https://travis-ci.org/searchisko/elasticsearch-river-sysinfo)
[![Coverage Status](https://coveralls.io/repos/searchisko/elasticsearch-river-sysinfo/badge.png?branch=master)](https://coveralls.io/r/searchisko/elasticsearch-river-sysinfo)


System info river component for [Elasticsearch](https://www.elastic.co/products/elasticsearch)
collects in defined intervals system information from Elasticsearch cluster,
and store them into search indexes, so may be used for later analysis.
System info can be collected from local or remote ES cluster, in case of remote 
cluster REST protocol may be used too to decrease different ES versions impedance.

**Please note that Rivers are going to be [deprecated from Elasticsearch 1.5](https://www.elastic.co/blog/deprecating_rivers).**

In order to install the plugin into Elasticsearch 1.3.x, simply run:

    bin/plugin -install elasticsearch-river-sysinfo` \
    -url https://repository.jboss.org/nexus/content/groups/public-jboss/org/jboss/elasticsearch/elasticsearch-river-sysinfo/1.4.1/elasticsearch-river-sysinfo-1.4.1.zip

In order to install the plugin into Elasticsearch 1.4.x, simply run:

    bin/plugin -install elasticsearch-river-sysinfo \
    -url https://repository.jboss.org/nexus/content/groups/public-jboss/org/jboss/elasticsearch/elasticsearch-river-sysinfo/1.5.1/elasticsearch-river-sysinfo-1.5.1.zip

<table>
  <tr>
    <th>Sysinfo River</th>
    <th>Elasticsearch</th>
    <th>Release date</th>
    <th>Notes</th>
  </tr>
  <tr>
    <td><code>master</code></td>
    <td><code>1.4.x</code></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td><code>1.5.1</code></td>
    <td><code>1.4.x</code></td>
    <td>27.3.2015</td>
    <td>See [Milestone 1.5.1] details.</td>
  </tr>
  <tr>
    <td><code>1.5.0</code></td>
    <td><code>1.4.x</code></td>
    <td>4.12.2014</td>
    <td></td>
  </tr>
  <tr>
    <td><code>1.4.1</code></td>
    <td><code>1.3.x</code></td>
    <td>22.9.2014</td>
    <td></td>
  </tr>
  <tr>
    <td><code>1.4.0</code></td>
    <td><code>1.3.x</code></td>
    <td>20.8.2014</td>
    <td></td>
  </tr>
  <tr>
    <td><code>1.3.0</code></td>
    <td><code>1.2.x</code></td>
    <td>8.7.2014</td>
    <td>changes in <code>indexers</code> config section necessary to monitor ES 1.2</td>
  </tr>
  <tr>
    <td><code>1.2.2</code></td>
    <td><code>0.90.5</code></td>
    <td>20.9.2013</td>
    <td></td>
  </tr>
  <tr>
    <td><code>1.2.1</code></td>
    <td><code>0.90.0</code></td>
    <td>17.5.2013</td>
    <td>Management REST API url's changed</td>
  </tr>
  <tr>
    <td><code>1.1.0</code></td>
    <td><code>0.90.11</code></td>
    <td>23.11.2012</td>
    <td>river configuration format changed in <code>indexers</code> section</td>
  </tr>
  <tr>
    <td><code>1.0.0</code></td>
    <td><code>0.19.11</code></td>
    <td>20.11.2012</td>
    <td></td>
  </tr>
</table>

  [Milestone 1.5.1]: https://github.com/searchisko/elasticsearch-river-sysinfo/issues?q=milestone%3A1.5.1+is%3Aclosed

For changelog, planned milestones/enhancements and known bugs see [github issue tracker](https://github.com/searchisko/elasticsearch-river-sysinfo/issues) please.

Creation of the System info river can be done using:

	curl -XPUT localhost:9200/_river/my_sysinfo_river/_meta -d '
	{
	    "type" : "sysinfo",
	    "es_connection" : {
	      "type" : "local"
	    },
	    "indexers" : {
	      "cluster_health" : {
	        "info_type"   : "cluster_health",
	        "index_name"  : "my_index_1",
	        "index_type"  : "my_type_1",
	        "period"      : "1m",
	        "params" : {
	          "level" : "shards"
	        }
	      },
	      "cluster_state" : {
	        "info_type"   : "cluster_state",
	        "index_name"  : "my_index_2",
	        "index_type"  : "my_type_2",
	        "period"      : "1m",
	        "params" : {
	          "metric" : "nodes"
	        }
	      }
	    }
	}
	'

The example above lists basic configuration used to store two types of information about cluster where river runs. 
Detailed description of configuration follows in next chapters.
Other examples of configuration can be found in [test resources](https://github.com/searchisko/elasticsearch-river-sysinfo/tree/master/src/test/resources).

## Connection to the monitored ES cluster
Connection used to collect ES cluster system information is configured using
`es_connection` element. Content depends on type of connection. There are three types available.  

### local
Local mode is used to collect information about ES cluster where river runs.
Only `type` option is used here, no any additional configuration parameter necessary.

	"es_connection" : {
	  "type" : "local"
	},

### remote
Remote mode uses [Transport Client](http://www.elastic.co/guide/en/elasticsearch/client/java-api/current/client.html#transport-client) to collect
system information from remote ES cluster using internal [Transport](http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-transport.html)
mechanism.
You can use this connection if transport mechanism of remote ES cluster version is compatible with version of ES cluster where river runs.  
Configuration requires `address` element with list of remote cluster nodes (both `host` and `port` elements are mandatory). 
Optionally you can define other connection `settings` as described in the [Transport Client documentation](http://www.elastic.co/guide/en/elasticsearch/client/java-api/current/client.html).

	"es_connection" : {
	  "type" : "remote",
	  "addresses" : [
	    {"host": "host1", "port" : "9300"},
	    {"host": "host2", "port" : "9300"}
	  ],
	  "settings" : {
	    "cluster.name" : "myCluster",
	    "client.transport.ping_timeout" : "10"
	  }
 	}

### rest
REST mode uses Elasticsearch [HTTP REST API](http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-http.html)
to collect system information from remote ES cluster.
You can use this connection mode in case of compatibility or networking problems with `remote` mode. 
Note that performance of REST API is commonly worse than binary transport mechanism behind `remote` mode.

	"es_connection" : {
	  "type"     : "rest",
	  "urlBase"  : "http://localhost:9200",
	  "timeout"  : "1s",
	  "username" : "myusername",
	  "pwd"      : "mypassword"
	 }

Configuration options:

* `urlBase` mandatory base URL of remote ES cluster to be used for http(s) REST API calls.
* `timeout` optional timeout for http(s) requests, default 5 second.
* `username` optional username for http basic authentication.
* `pwd` optional password for http basic authentication.

## Configuration of indexers
Second significant part of the river configuration is map of `indexers`. Each indexer defines what 
information will be collected in which interval, and where will be stored in ES indexes.
Each indexer has unique name defined as key in map of indexers.
Information is stored to the ES indexes in cluster where river runs. Structure of stored
information is exactly same as returned from ElasticSearch API call. Note that this information typically do not contain timestamp when it was acquired and stored, to get time information you have to enable automatic [`_timestamp`](http://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html) field in your mapping.
Indexer configuration is:

	"indexer_name" : {
	  "info_type"  : "cluster_health",
	  "index_name" : "my_index_1",
	  "index_type" : "my_type_1",
	  "period"     : "1m",
	  "params"     : {
	      "level" : "shards"
	  }
	}

Configuration options:
	
* `info_type` mandatory type of information collected by this indexer. See table below for list of all available types. You can create more indexers with same type.
* `index_name` mandatory name of index used to store information. Note that this river can produce big amount of data over time, so consider use of [rolling index](http://github.com/elastic/elasticsearch/issues/1500) here.
* `index_type` mandatory [type](http://www.elastic.co/guide/en/elasticsearch/reference/current/glossary.html#type) used to stored information into search index. You should define [Mapping](http://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html) for this type. You should enable [Automatic Timestamp Field](http://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html) in this mapping to have consistent timestamp available in stored data.
* `period` mandatory period of information collecting in milliseconds. You can use postfixes appended to the number to define units: `s` for seconds, `m` for minutes, `h` for hours, `d` for days and `w` for weeks. So for example value `5h` means five fours, `2w` means two weeks.
* `params` optional map of additional parameters to narrow down collected information. Available parameters depend on `info_type`, and can be found as 'Request parameters' in relevant ES API doc for each type. Some additional parameters (passed as URL parts in API doc) are described in note, see table below.

Available information types:

<table>

  <tr>
    <th><code>info_type</code></th>
    <th>Relevant ES API doc</th>
    <th>River version</th>
    <th>Notes</th>
  </tr>

  <tr>
    <td>
      <code>cluster_health</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-health.html">Cluster Health</a>
    </td>
    <td></td>
    <td>
      <code>index</code> param
    </td>
  </tr>

  <tr>
    <td>
      <code>cluster_state</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-state.html">Cluster State</a>
    </td>
    <td></td>
    <td>
      <code>indices</code>, <code>metric</code> param for ES 1.2, use of <code>metadata</code> metric may bring performance problems!
    </td>
  </tr>

  <tr>
    <td>
      <code>cluster_stats</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-stats.html">Cluster Stats</a>
    </td>
    <td>&gt;= 1.5.1</td>
    <td>
      <code>nodeId</code> param
    </td>
  </tr>

  <tr>
    <td>
      <code>pending_cluster_tasks</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-pending.html">Pending Cluster Tasks</a>
    </td>
    <td>&gt;= 1.5.1</td>
    <td/>
  </tr>

  <tr>
    <td>
      <code>cluster_nodes_info</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-info.html">Nodes Info</a>
    </td>
    <td></td>
    <td>
      <code>nodeId</code> param. <code>metrics</code> param for ES 1.2
    </td>
  </tr>

  <tr>
    <td>
      <code>cluster_nodes_stats</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-stats.html">Nodes Stats</a>
    </td>
    <td></td>
    <td>
      <code>nodeId</code> param. <code>metric</code>, <code>indexMetric</code>, <code>fields</code> params for ES 1.2
    </td>
  </tr>

  <tr>
    <td>
      <code>indices_status</code>
    </td>
    <td>
      <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/indices-status.html">Indices Status</a>
    </td>
    <td></td>
    <td>
      <code>index</code> param. Note this API is deprecated in ES 1.2.0 - Index Recovery should be used instead.
    </td>
  </tr>

  <tr>
    <td>
      <code>indices_stats</code>
    </td>
    <td>
      <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-stats.html">Indices Stats</a>
    </td>
    <td></td>
    <td>
      <code>index</code> param. <code>metric</code>, <code>indexMetric</code> params for ES 1.2
    </td>
  </tr>

  <tr>
    <td>
      <code>indices_segments</code>
    </td>
    <td>
      <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-segments.html">Indices Segments</a>
    </td>
    <td></td>
    <td>
      <code>index</code> param.
      Use of this api can lead to high load on cluster due to constant dynamic mapping updates.
    </td>
  </tr>

  <tr>
    <td>
      <code>indices_recovery</code>
    </td>
    <td>
      <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-recovery.html">Indices Recovery</a>
    </td>
    <td>&gt;= 1.5.1</td>
    <td>
      <code>index</code> param
    </td>
  </tr>

</table>

Management REST API
-------------------
Sysinfo river supports next REST commands for management purposes. Note `my_sysinfo_river`
in examples is name of Sysinfo river you can call operation for, so replace it with real 
name for your calls.

Stop Sysinfo river indexing process. Process is stopped temporarily, so after complete 
elasticsearch cluster restart or river migration to another node it's started back.

	curl -XPOST localhost:9200/_river/my_sysinfo_river/_mgm_sr/stop

Restart Sysinfo river indexing process. Configuration of river is reloaded during restart. 
You can restart running indexing, or stopped indexing (see previous command).

	curl -XPOST localhost:9200/_river/my_sysinfo_river/_mgm_sr/restart
	
Change indexing period for named indexers (indexers are named in url and comma 
separated, see `cluster_health,cluster_state` in example below). Change is not 
persistent, it's back on value from river configuration file after river restart!
	
	curl -XPOST localhost:9200/_river/my_sysinfo_river/_mgm_sr/cluster_health,cluster_state/period/2s

List names of all Sysinfo Rivers running in ES cluster.

	curl -XGET localhost:9200/_sysinfo_river/list


License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2012-2014 Red Hat Inc. and/or its affiliates and other contributors as indicated by the @authors tag. 
    All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
	

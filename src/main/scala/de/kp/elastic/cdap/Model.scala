package de.kp.elastic.cdap
/*
 * Copyright 2019, Dr. Krusche & Partner PartG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import co.cask.cdap.client.proto.PluginPropertyField

case class CDAPException(
  message:String,
  trace:Option[String] = None
)

case class CDAPProgram(
  progName:String,progDesc:String,progType:String  
)

//case class CDAPApplication(
//  namespace:String,  
//  appName:String,
//  appDesc:String,
//  appVersion:String,
//  programs:List[CDAPProgram]
//)

case class CDAPColumnDesc(
  colName:String,
  colType:String,
  colComment:String,
  colPos:Int
)

case class CDAPContainerInfo(
	name:String,
	`type`:String,
	instance:Int,
	container:String,
	host:String,
	memory:Int,
	virtualCores:Int,
	debugPort:Int
)

case class CDAPDataset(
  namespace:String,
  datasetName:String,
  datasetDesc:String,
	datasetType:String,
	datasetProps:Map[String,String]    
)

case class CDAPDatasetSpec(
  namespace:String,
  datasetName:String,
  datasetDesc:String,
	datasetType:String,
	datasetProps:Map[String,String],
	hiveTableName:String,
	ownerPrincipal:String
)

case class CDAPMetrics(
  metrics:List[String],
  metricTags:Map[String,String]
)

case class CDAPPlugin(
  /*
   * Concatenated description of the parent artifact this plugin
   * belongs too; the current version of CDAP (v5.1.1) distinguishes
   * 'cdap-data-pipeline' and 'cdap-data-streams'
   */
  artifact:String,
  /*
   * The className is used as a unique identifier of a certain plugin,
   * as plugins can be assigned to pipelines as well as to streams
   */
  className:String,
  name:String,
  `type`:String,
  description:String,
  endpoints:List[String],
  configFieldName:String,
  properties:Map[String,PluginPropertyField]
)

case class CDAPQueryResult(
  namespace:String,
  datasetName:String,
  query:String,
  /* The # of rows within the result */
  count:Int,
  /* The status of the query request */
  status:String,
  schema:List[CDAPColumnDesc],
  /* Row column values */
  rows:List[List[Any]]
)

case class CDAPRunRecord(
  pid:String,
  startTs:Long,
  runTs:Long,
  stopTs:Long,
	suspendTs:Long,
	resumeTs:Long,
	status:String,
  properties:Map[String,String]
)

case class CDAPSystemServiceMeta(
  name:String,
  description:String,
  status:String,
  logs:String,
  minInstances:Int,
  maxInstances:Int,
  instancesRequested:Int,
  instancesProvisioned:Int
)

case class CDAPSystemServiceStatus(
  name:String,
  status:String
)

case class CDAPTimeValue(
  time:Long,
  value:Long
)

case class CDAPTimeSeries(
  /* Name of the metric */
  name:String,
  data:List[CDAPTimeValue]
)

case class CDAPMetricResult(
  startTime:Long, endTime:Long,resolution:String,series:List[CDAPTimeSeries]
)

/*
 * Request to retrieve a specific application 
 * associated with a certain namespace 
 */
case class AppReq(
  namespace:String, 
  appName:String, 
  appVersion:String
)
/*
 * Request  to retrieve all applications 
 * associated with a certain namespace
 */
case class AppsReq(
  namespace:String
)
/*
 * Request to check whether a certain
 * application exists within a specific
 * namespace
 */
case class ExistsReq(
  namespace:String, 
  appName:String, 
  appVersion:String
)
/*
 * Request to register a certain application
 * described by its (logical) blueprint
 */
case class RegisterReq(
  namespace:String, 
  appName:String, 
  appVersion:String,
  blueprint:String
)
/*
 * Request to update a certain application
 * described by its (logical) blueprint
 */
case class UpdateReq(
  namespace:String, 
  appName:String, 
  appVersion:String,
  blueprint:String
)
/*
 * Request to start a certain program of an 
 * application
 */
case class StartReq(
  namespace:String,
  appName:String,
  appVersion:String,
  progName:String,
  progType:String,
  runtimeArgs:Map[String,String]
) 
/*
 * Request to stop a certain program of an 
 * application
 */
case class StopReq(
  namespace:String,
  appName:String,
  appVersion:String,
  progName:String,
  progType:String
) 

//case class AppsRsp(apps:List[CDAPApplication])

case class ExistsRsp(exists:Boolean)

case class RegisterRsp(message:String)

case class UpdateRsp(message:String)

case class StartRsp(message:String)

case class StopRsp(message:String)


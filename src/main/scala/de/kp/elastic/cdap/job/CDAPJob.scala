package de.kp.elastic.cdap.job
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
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import co.cask.cdap.client.proto._

import de.kp.elastic.cdap._
import de.kp.elastic.cdap.core.CDAPContext
/**
 * [CDAPJob] is a dual-use interface to CDAP context: It supports 
 * both, a CDAP client service and Elasticsearch's plugin concept
 */
class CDAPJob(props:Properties) {
  
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
   
  private val ctx = new CDAPContext(props)

  /**
   * Delegate retrieval of applications to CDAP context
   */
  def getApps(namespace:String):List[ApplicationRecord] = {
    ctx.getApps(namespace)
  }
  
  def getAppsAsJson(namespace:String):String = {
    val apps = getApps(namespace)
    mapper.writeValueAsString(apps)
  }
  
  /**
   * Delegate retrieval of application to CDAP context  
   */
  def getApp(namespace:String, appName:String, appVersion:String):ApplicationDetail = {
    ctx.getApp(namespace, appName, appVersion)
  }
  
  def getAppAsJson(namespace:String, appName:String, appVersion:String):String = {
    val app = getApp(namespace, appName, appVersion)
    mapper.writeValueAsString(app)
  }
  
  /**
   * Delegate application existence check to CDAP context  
   */
  def existsApp(namespace:String, appName:String, appVersion:String):Boolean = {
    ctx.existsApp(namespace, appName, appVersion)
  }
   
  /**
   * Delegate registration of a blueprint to CDAP Context
   */
  def registerApp(namespace:String, appName:String, appVersion:String, blueprint:String):String = {
    ctx.registerApp(namespace, appName, appVersion, blueprint)
    "Blueprint successfully registered"
  }
  /**
   * Delegate update of a blueprint to CDAP context
   */
  def updateApp(namespace:String, appName:String, appVersion:String, blueprint:String):String = {
    ctx.updateApp(namespace, appName, appVersion, blueprint)
    "Blueprint successfully updated"
  }
  /**
   * Delegate retrieval of datasets to CDAP context
   */
  def getDatasets(namespace:String):List[CDAPDataset] = {
    ctx.getDatasets(namespace)
  }
  def getDatasetsAsJson(namespace:String):String = {
    val datasets = ctx.getDatasets(namespace)
    mapper.writeValueAsString(datasets)
  }
  /**
   * Delegate retrieval of datasets to CDAP context
   */
  def getDataset(namespace:String,datasetName:String):CDAPDatasetSpec = {
    ctx.getDataset(namespace,datasetName)
  }
  
  def getDatasetAsJson(namespace:String,datasetName:String):String = {
   val dataset = ctx.getDataset(namespace,datasetName)
    mapper.writeValueAsString(dataset)
  }
  
  /**
   * Delegate start of a certain program to CDAP context
   */
  def startProg(namespace:String,appName:String,appVersion:String,progName:String,progType:String,runtimeArgs:java.util.Map[String,String]):String = {
    ctx.startProg(namespace, appName, appVersion, progName, progType, runtimeArgs)
    "Program successfully started"
  }
  
  /**
   * Delegate stop a certain program to CDAP context
   */
  def stopProg(namespace:String,appName:String,appVersion:String,progName:String,progType:String):String = {
    ctx.stopProg(namespace, appName, appVersion, progName, progType)
    "Programm successfuly stopped"
  }
  /**
   * Delegate status retrieval of a certain program to CDAP context
   */
  def getProgStatus(namespace:String,appName:String,appVersion:String,progName:String,progType:String):String = {
    ctx.getProgStatus(namespace, appName, appVersion, progName, progType)
  }
  /**
   * Delegates program run retrieval of a certain program to CDAP context
   */
  def getProgramRuns(namespace: String, appName: String, appVersion: String, progName: String, progType: String, 
      progState: String, startTime: Long, endTime: Long, limit: Int): List[CDAPRunRecord] = {
    ctx.getProgramRuns(namespace, appName, appVersion, progName, progType, progState, startTime, endTime, limit)
  }
  /**
   * This method retrieves run records of a certain program in JSON format
   */
  def getProgramRunsAsJson(namespace: String, appName: String, appVersion: String, progName: String, progType: String, 
      progState: String, startTime: Long, endTime: Long, limit: Int): String = {
    
    val runs = getProgramRuns(namespace, appName, appVersion, progName, progType, progState, startTime, endTime, limit)
    mapper.writeValueAsString(runs)
  
  }
  /**
   * Delegate metrics retrieval to CDAP Context
   */
  def getMetrics(namespace:String,context:java.util.Map[String,String]):CDAPMetrics = {
    ctx.getMetrics(namespace,context)
  }
  
  def getMetricsAsJson(namespace:String,context:java.util.Map[String,String]):String = {
    val metrics = getMetrics(namespace,context)
    mapper.writeValueAsString(metrics)
  }
  /**
   * Delegate metric result retrieval to CDAP context
   */
  def queryMetric(tags:java.util.Map[String,String],metric:String):CDAPMetricResult = {
    ctx.queryMetric(tags, metric)
  }
  
  def queryMetricAsJson(tags:java.util.Map[String,String],metric:String):String = {
    val result = ctx.queryMetric(tags, metric)
    mapper.writeValueAsString(result)
  }
  
  /**
   * Delegate query execution to CDAP context
   */
  def executeQuery(namespace:String,datasetName:String,query:String):CDAPQueryResult = {
    ctx.executeQuery(namespace, datasetName, query)
  }

  def executeQueryAsJson(namespace:String,datasetName:String,query:String):String = {
    val result = executeQuery(namespace, datasetName, query)
    mapper.writeValueAsString(result)
  }
   
  /**
   * Delegate system service retrieval to CDAP context
   */
  def getSystemServices:List[CDAPSystemServiceMeta] = {
    ctx.getSystemServices
  }

  def getSystemServicesAsJson:String = {
    val services = ctx.getSystemServices
    mapper.writeValueAsString(services)
  }
  
  /**
   * Delegates retrieval of live info of a certain system service
   * to CDAP context
   */
  def getSystemServiceLiveInfo(serviceName:String):List[CDAPContainerInfo] = {
    ctx.getSystemServiceLiveInfo(serviceName)
  }

  def getSystemServiceLiveInfoAsJson(serviceName:String):String = {
    val info = ctx.getSystemServiceLiveInfo(serviceName)
    mapper.writeValueAsString(info)
  }
  
  /**
   * Delegates the retrieval of a service status to CDAP context
   */
  def getSystemServiceStatus(serviceName:String):String = {
    ctx.getSystemServiceStatus(serviceName)
  }
  
  def  getSystemServicesStatus:List[CDAPSystemServiceStatus] = {
    ctx.getAllSystemServiceStatus
  }
  
  def  getSystemServicesStatusAsJson:String = {
    val statuses = ctx.getAllSystemServiceStatus
    mapper.writeValueAsString(statuses)
  }
  
	/**
	 * This method retrieves the number of instances the system 
	 * service is running on.
	 */
	def getSystemServiceInstances(serviceName:String):Int = {
	  ctx.getSystemServiceInstances(serviceName)
	}
 
}
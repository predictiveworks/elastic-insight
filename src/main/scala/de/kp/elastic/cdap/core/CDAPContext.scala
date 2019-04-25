package de.kp.elastic.cdap.core

import java.util.Properties

import co.cask.cdap.client._
import co.cask.cdap.client.config._
import co.cask.cdap.client.proto._

import co.cask.cdap.security.authentication.client.basic._
import co.cask.cdap.proto.ProgramType
import co.cask.cdap.proto.QueryResult
import co.cask.cdap.proto.artifact.AppRequest
import co.cask.cdap.proto.id.{ApplicationId,ArtifactId,DatasetId,NamespaceId,ProgramId}

import com.google.gson.{Gson,JsonObject}

import de.kp.elastic.cdap._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.mutable.ArrayBuffer

/*
 * https://docs.cask.co/cdap/6.0.0-SNAPSHOT/en/reference-manual/java-client-api.html#application-client
 */

/**
 * TOOD:
 * 
 * 1. Register logical pipeline as physical application
 * 
 * 		Evaluate CDAP UI to learn how this is achieved; as far as I know there is
 *    support within the respective Java client
 *    
 * 2. Retrieve metadata about a certain (registered) application
 * 
 * 3. Start program associated with a certain (registered) application
 * 
 * 4. Stop program associated with a certain (registered) application
 * 
 * 5. Retrieve metrics data about CDAP specific entities
 *   
 */

class CDAPContext(props:Properties) {
  
  private val USERNAME_PROP_NAME = "security.auth.client.username"
  private val PASSWORD_PROP_NAME = "security.auth.client.password"
  /*
   * Extract CDAP configuration and build CDAP context
   * either secure or non-secure
   */
  private val host = props.getProperty("host")
  private val port = props.getProperty("port").toInt
  
  private val sslEnabled = {
    
    val prop = props.getProperty("sslEnabled")
    if (prop == "yes") true else false
  
  }
  
  private val alias = {
    
    val prop = props.getProperty("alias")
    if (prop.isEmpty) None else Some(prop)
    
  }
  private val password = {
    
    val prop = props.getProperty("password")
    if (prop.isEmpty) None else Some(prop)
    
  }
  
  private val connConfig = new ConnectionConfig(host, port, sslEnabled)

  private val clientConfig = if (sslEnabled) {
    /*
     * Build client configuration for a secure CDAP instance
     */
    if (alias.isDefined == false || password.isDefined == false)
      throw new Exception("[ERROR] SSL connection requires alias and password.")
    
    val props = new Properties()
    props.setProperty(USERNAME_PROP_NAME, alias.get)
    props.setProperty(PASSWORD_PROP_NAME, password.get)
    
    val authClient = new BasicAuthenticationClient()
    authClient.configure(props)
    
    authClient.setConnectionInfo(host, port, sslEnabled)
    val accessToken = authClient.getAccessToken()

    ClientConfig.builder()
      .setConnectionConfig(connConfig)
      .setAccessToken(accessToken)
      .build()
    
  } else {
    /*
     * Build client configuration for a non-secure CDAP instance
     */
    ClientConfig.builder()
      .setConnectionConfig(connConfig)
      .build()    
  }
    
  /*
   * Build application client to access CDAP application REST service
   */
  private val appClient = new ApplicationClient(clientConfig)
  /*
   * Build artifact client to access CDAP artifact REST service
   */
  private val artifactClient = new ArtifactClient(clientConfig)
  /*
   * Build dataset client to access CDAP dataset REST service
   */
  private val datasetClient = new DatasetClient(clientConfig)
  /*
   * Build metrics client to access CDAP metrics REST service
   */
  private val metricsClient = new MetricsClient(clientConfig)
  /*
   * Build monitor client to access CDAP monitor REST service
   */
  private val monitorClient = new MonitorClient(clientConfig)
  /*
   * Build program client to access CDAP program REST service
   */
  private val progClient = new ProgramClient(clientConfig)
  /*
   * Build query client to access CDAP query REST service
   */
  private val queryClient = new QueryClient(clientConfig)
  
  /********************************
   * 
   * APP SUPPORT
   * 
   *******************************/
  
  /**
   * A public method to retrieve all applications that are assigned to 
   * a specific namespace
   */
  def getApps(namespace:String = "default"):List[ApplicationRecord] = {
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    appClient.list(nsID).asScala.toList
    
  }
  
  /**
   * An external method that is used to retrieve a certain
   * application of a specific namespace that matches name
   * and version provided.
   */
  def getApp(namespace:String, appName:String, appVersion:String):ApplicationDetail = {

    val app = getAppID(namespace,appName,appVersion)
    appClient.get(app)
    
  }
  
  /**
   * An external method to determine whether an application
   * specified by name and version exists within a certain
   * namespace
   */
  def existsApp(namespace:String, appName:String, appVersion:String):Boolean = {
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    val app = getAppID(nsID, appName, appVersion)
    
    appClient.exists(app)

  }
   
  /**
   * This method receives a logical pipeline (blueprint) specified 
   * as JSON and initiates conversion into physical one which then 
   * is registered as an application
   */
  def registerApp(namespace:String, appName:String, appVersion:String, blueprint:String):Unit = {
    /*
     * STEP #1: Convert application metadata into unique application
     * identifier and transform logical pipeline representation into
     * JSON based application (create) request
     */
    val app = getAppID(namespace,appName,appVersion)
    val req = new Gson().fromJson(blueprint, classOf[AppRequest[JsonObject]])
    
    /*
     * STEP #2: Initiate backend registration request via REST API
     */
    appClient.deploy(app, req)

  }
  
  def updateApp(namespace:String, appName:String, appVersion:String, blueprint:String):Unit = {
    /*
     * STEP #1: Convert application metadata into unique application
     * identifier and transform logical pipeline representation into
     * JSON based application (create) request
     */
    val app = getAppID(namespace,appName,appVersion)
    val req = new Gson().fromJson(blueprint, classOf[AppRequest[JsonObject]])
    
    /*
     * STEP #2: Initiate backend update request via REST API
     */
    appClient.update(app, req)

  }
  
  /**
   * An external method to retrieve an (internal) application identifier
   * from serializable application info
   */
  def getAppID(namespace:String, appName:String, appVersion:String):ApplicationId = {
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    getAppID(nsID, appName, appVersion)

  }
  /********************************
   * 
   * DATASET SUPPORT
   * 
   *******************************/
  
  /**
   * A public method to retrieve all dataset that are assigned to 
   * a specific namespace
   */
  def getDatasets(namespace:String = "default"):List[CDAPDataset] = {
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    val datasets = datasetClient.list(nsID)
    
    datasets.map(dataset => {
      
      val datasetName = dataset.getName
      val datasetDesc = dataset.getDescription
      
      val datasetType = dataset.getType
      val datasetProps = dataset.getProperties.asScala.toMap
      
      CDAPDataset(namespace=namespace, datasetName=datasetName, datasetDesc=datasetDesc,datasetType=datasetType,datasetProps=datasetProps)
      
    }).toList
    
  }
  /**
   * A public method to retrieve the specification of a certain dataset
   */
  def getDataset(namespace:String,datasetName:String):CDAPDatasetSpec = {
    
    val dsID = getDatasetId(namespace,datasetName)
    val dataset = datasetClient.get(dsID)
    
    val spec = dataset.getSpec
    val datasetDesc = spec.getDescription

    val datasetType = spec.getType
    val datasetProps = spec.getProperties.asScala.toMap
    
    val hiveTableName = dataset.getHiveTableName
    val ownerPrincipal = dataset.getOwnerPrincipal
     
    CDAPDatasetSpec(
      namespace=namespace, 
      datasetName=datasetName, 
      datasetDesc=datasetDesc,
      datasetType=datasetType,
      datasetProps=datasetProps,
      hiveTableName=hiveTableName,
      ownerPrincipal=ownerPrincipal
    )

  }
  
  def getDatasetId(namespace:String,datasetName:String):DatasetId = {
    new DatasetId(namespace,datasetName)
  }
  
  /********************************
   * 
   * METRIC SUPPORT
   * 
   *******************************/
  
  /**
   * This method retrieves the internal metric names that are available 
   * for application related metrics context   
   */
  def getMetrics(namespace:String,context:java.util.Map[String,String]):CDAPMetrics = {

    val tags = getMetricTags(namespace,context)
    if (tags == null)
      throw new Exception("[ERROR] Provided context is not valid")
    
    val metrics = getMetrics(tags)

    CDAPMetrics(metrics=metrics,metricTags=tags)
    
  }
  
  def getMetricTags(namespace:String,context:java.util.Map[String,String]):Map[String,String] = {
    
    if (context == null || context.isEmpty)
      /*
       * Retrieve metric context for all applications
       * of a certain namespace
       */
      return MetricsUtil.getAllApp(namespace)

    /*
     * Determine whether this context specifies a certain 
     * metric type  
     */
    if (context.contains("metricType") == false) {
      /*
       * This request its restricted to a certain application
       * and must provide the respective app name
       */
      if (context.contains("appName") == false)
        return null
        
      else {
        /* Retrieve tags for a certain application 
         * without further context information
         */
        val appName = context("appName")
        return MetricsUtil.getApp(namespace, appName)
      }  
    
    } else {
      
      val metricType = context("metricType")
      
      metricType match {
        case "dataset" => {
          if (context.contains("datasetName") == false)
            /*
             * Retrieve metrics tags for all dataset of 
             * a namespace
             */          
            return MetricsUtil.getAllDataset(namespace)
          
          else {
            val datasetName = context("datasetName")
            if (context.contains("appName") == false) {
              /*
               * Retrieve metrics tags for a certain dataset 
               * of a namespace
               */
              return MetricsUtil.getDataset(namespace, datasetName)
           }
            else {
               val appName = context("appName")
               /*
                * Retrieve metrics tags for a certain dataset of an 
                * application
                */
               return MetricsUtil.getAppDataset(namespace, appName, datasetName)
            }
          }
        }
        case "flow" => {
          if (context.contains("appName") == false)
            return null
            
          else {
            val appName = context("appName")
            if (context.contains("flowName") == false) 
              /*
               * Retrieve metrics tags for all flows of 
               * an application
               */
              return MetricsUtil.getAllFlow(namespace, appName)
              
            else {
              /*
               * Retrieve metrics tags for a certain flow 
               * of an application
               */
              val flowName = context("flowName")
              return MetricsUtil.getFlow(namespace, appName, flowName)
            }
          }          
        }   
        case "mapreduce" => {
          if (context.contains("appName") == false)
            return null
            
          else {
            val appName = context("appName")
            if (context.contains("mapreduceName") == false)
              /*
               * Retrieve metrics tags for all mapreduce programs 
               * of an application
               */
              return MetricsUtil.getAllMapReduce(namespace, appName)
              
             else {
               /*
                * Retrieve metrics tags for a certain mapreduce 
                * of an application
                */
               val mapreduceName = context("mapreduceName")
               return MetricsUtil.getMapReduce(namespace, appName, mapreduceName)
             }
          }          
        }                                
        case "service" => {
          if (context.contains("appName") == false)
            return null
            
          else {
            val appName = context("appName")
            if (context.contains("serviceName") == false)
              /*
               * Retrieve metrics tags for all services of 
               * an application
               */            
              return MetricsUtil.getAllService(namespace, appName)
              
            else {
              /*
               * Retrieve metrics tags for a certain service 
               * of an application
               */
               val serviceName = context("serviceName")
               return MetricsUtil.getService(namespace, appName, serviceName)
            }
          }          
        }                
        case "spark" => {
          if (context.contains("appName") == false)
            return null
            
          else {
            val appName = context("appName")
            if (context.contains("sparkName") == false)
              /*
               * Retrieve metrics tags for all spark programs 
               * of an application
               */            
              return MetricsUtil.getAllSpark(namespace, appName)
              
            else {
              /*
               * Retrieve metrics tags for a certain spark program 
               * of an application
               */
               val sparkName = context("sparkName")
               return MetricsUtil.getSpark(namespace, appName, sparkName)
            }
          }          
        }
        case "worker" => {
          /*
           * Retrieve metrics tags for all workers of an application
           */
          if (context.contains("appName") == false)
            return null
            
          else {
            val appName = context("appName")
            if (context.contains("workerName") == false)
              /*
               * Retrieve metrics tags for all workers 
               * of an application
               */            
              return MetricsUtil.getAllWorker(namespace, appName)
              
            else {
              /*
               * Retrieve metrics tags for a certain worker 
               * of an application
               */
               val workerName = context("workerName")
               return MetricsUtil.getWorker(namespace, appName, workerName)
            }
          }          
        }
        case _ => return null
      }
      
    }
      
    null
  
  }
  
  /**
   * This method retrieves the internal metric names that are available 
   * for a certain metrics context described as a tag map   
   */
  private def getMetrics(tags:Map[String,String]):List[String] = {
    
    val metrics = metricsClient.searchMetrics(tags)
    metrics.asScala.toList
    
  }
  /**
   * This method retrieves metric data for a single metric
   * and associated metric context
   */
  def queryMetric(tags:java.util.Map[String,String],metric:String):CDAPMetricResult = {
    
    val result = metricsClient.query(tags, metric)
    
    val startTime = result.getStartTime
    val endTime = result.getEndTime
    
    val resolution = result.getResolution
    val series = result.getSeries.map(timeseries => {

      val metric = timeseries.getMetricName
      val data = timeseries.getData.map(timeValue =>
        CDAPTimeValue(timeValue.getTime,timeValue.getValue)
      )
      
      CDAPTimeSeries(name=metric,data=data.toList)
      
    }).toList
    
    CDAPMetricResult(startTime=startTime,endTime=endTime,resolution=resolution,series=series)
    
  }
  
  /********************************
   * 
   * MONITOR SUPPORT
   * 
   *******************************/
   
  /**
   * This method retrieves all system services
   */
  def getSystemServices():List[CDAPSystemServiceMeta] = {
    
    val services = monitorClient.listSystemServices
    services.map(service => {

      CDAPSystemServiceMeta(
        name = service.getName,
        description = service.getDescription,
        status = service.getStatus,
        logs = service.getLogs,
        minInstances = service.getMinInstances,
        maxInstances = service.getMaxInstances,
        instancesRequested = service.getInstancesRequested,
        instancesProvisioned = service.getInstancesProvisioned
      )
      
    }).toList
    
  }
  /**
   * This method retrieves live info of a certain system service
   */
  def getSystemServiceLiveInfo(serviceName:String):List[CDAPContainerInfo] = {
    
    val info = monitorClient.getSystemServiceLiveInfo(serviceName)
    val containers = info.getContainers
    
    if (containers.isEmpty) return List.empty[CDAPContainerInfo]
    containers.map(container => {
      
      CDAPContainerInfo(
	      name = container.getName,
	      `type` = container.getType.name,
	      instance = container.getInstance,
	      container = container.getContainer,
	      host = container.getHost,
	      memory = container.getMemory,
	      virtualCores = container.getVirtualCores,
	      debugPort = container.getDebugPort
      )
    }).toList
    
  }
  /**
   * This method retrieves the service of a certain system service
   */
  def getSystemServiceStatus(serviceName:String):String = {
    monitorClient.getSystemServiceStatus(serviceName)
  }
  def  getAllSystemServiceStatus:List[CDAPSystemServiceStatus] = {
    
    val statuses = monitorClient.getAllSystemServiceStatus
    
    statuses.map(e => {
      CDAPSystemServiceStatus(name = e._1, status = e._2)
    }).toList
    
  }
	/**
	 * This method retrieves the number of instances the system 
	 * service is running on.
	 */
	def getSystemServiceInstances(serviceName:String):Int = {
	  monitorClient.getSystemServiceInstances(serviceName)
	}
  
  /********************************
   * 
   * PLUGIN SUPPORT
   * 
   *******************************/
  
	/**
	 * This request retrieves the CDAP based machinery
	 * that can be used to build plugins
	 */   
	def getPlugins(namespace:String):List[CDAPPlugin] = {
	  
	  /* 
	   * CDAP provides for the creation of custom plugins to extend the 
	   * existing 'cdap-data-pipeline' and 'cdap-data-streams' system 
	   * artifacts.
	   */
	  val artifacts = props.getProperty("artifacts").split(",")
	  val version = props.getProperty("version")

	  val plugins = artifacts.flatMap(artifact => {
	    
	    val artifactNS = s"SYSTEM:${artifact}:${version}"
	    
	    val artifactID = getArtifactID(namespace,artifact,version)
	    /*
	     * STEP #1: Retrieve list of all plugin types that are available 
	     * for each of the system artifacts
    	   */
	    val types = artifactClient.getPluginTypes(artifactID)
	    
	    types.flatMap(pluginType => {
	      /*
	       * STEP #2: Retrieve plugin summaries for each of the
	       * available plugin types
	       */
	      artifactClient.getPluginSummaries(artifactID, pluginType).flatMap(summary => {
	        
	        val pluginName = summary.getName
	        val pluginType = summary.getType
	        /*
	         * PluginInfo is more detailed that summaries
	         */
	        val infos = artifactClient.getPluginInfo(artifactID, pluginType, pluginName)
	        infos.map(info => {
	          
	          CDAPPlugin(
		          artifact        = artifactNS,
	            className       = info.getClassName,
	            name            = info.getName,
	            `type`          = info.getType,
	            description     = info.getDescription,
	            endpoints       = info.getEndpoints.asScala.toList,
	            configFieldName = info.getConfigFieldName,
	            properties      = info.getProperties.asScala.toMap
            )
            
	        })
	        
	      })
	    
	    })
	    
	  })
	  
	  plugins.toList
	  
	}
	
	def getArtifactID(namespace:String, artifact:String, version:String):ArtifactId = {
	  new ArtifactId(namespace,artifact,version)
	}
	
  /********************************
   * 
   * PROGRAM SUPPORT
   * 
   *******************************/
  
  def getProgID(namespace:String, appName:String, appVersion:String, progName:String, progType:String):ProgramId = {
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    getProgID(nsID, appName, appVersion, progName, progType)
    
  }
  
  /**
   * This method starts a certain program of a specific 
   * application that refers to a certain program type
   */
  def startProg(namespace:String,appName:String,appVersion:String,progName:String,progType:String,runtimeArgs:java.util.Map[String,String]):Unit = {
    
    /* Retrieve program identifier */
    val prog = getProgID(namespace, appName, appVersion, progName, progType)
       
    /* Start program: debug = false */
    progClient.start(prog, false, runtimeArgs)
    
  }
  
  /**
   * This method stops a certain program of a specific 
   * application that refers to a certain program type
   */
  def stopProg(namespace:String,appName:String,appVersion:String,progName:String,progType:String):Unit = {
    
    /* Retrieve program identifier */
    val prog = getProgID(namespace, appName, appVersion, progName, progType)
      
    /* Stop program */
    progClient.stop(prog)
    
  }
  /**
   * This method return the status of a certain program of a specific 
   * application that refers to a certain program type
   */
  def getProgStatus(namespace:String,appName:String,appVersion:String,progName:String,progType:String):String = {
    
    /* Retrieve program identifier */
    val prog = getProgID(namespace, appName, appVersion, progName, progType)
       
    /* Get program status */
    progClient.getStatus(prog)
    
  }
  /**
   * This method returns the run records of a certain program filtered
   * with respect to start & end time, and limit
   */
  def getProgramRuns(namespace: String, appName: String, appVersion: String, progName: String, progType: String, 
      progState: String, startTime: Long, endTime: Long, limit: Int): List[CDAPRunRecord] = {

    /* Retrieve program identifier */
    val prog = getProgID(namespace, appName, appVersion, progName, progType)

    /* Get program run records */
    val runs = progClient.getProgramRuns(prog, progState, startTime, endTime, limit)

    runs.map(run => {

      CDAPRunRecord(
        pid = run.getPid,
        startTs = run.getStartTs,
        runTs = run.getRunTs,
        stopTs = run.getStopTs,
        suspendTs = run.getSuspendTs,
        resumeTs = run.getResumeTs,
        status = run.getStatus,
        properties = run.getProperties.asScala.toMap)

    }).toList

  }
  
  /********************************
   * 
   * QUERY SUPPORT
   * 
   *******************************/
  
  /**
   * This method executes an ad-hoc SQL-like query
   * to explore the content of a certain dataset
   */
  def executeQuery(namespace:String,datasetName:String,query:String):CDAPQueryResult = {
    /*
     * Query statements are used to explore the content 
     * of dataset with ad-hoc SQL-like queries. 
     * 
     * Queries can be run over streams and certain types 
     * of datasets. Enabling exploration for a dataset results 
     * in the creation of a SQL table in the Explore system. 
     * 
     * The name of this table is, by default, the same as the 
     * name of the dataset, prefixed with dataset_. 
     */
    val table = s"dataset_${datasetName}"
    val statement = query.replace(datasetName, table)   
    
    val nsID = NamespaceId.fromIdParts(List(namespace))
    val executionResult = queryClient.execute(nsID, statement).get
    
    /* The # of rows returned */
    val count = executionResult.getFetchSize
    
    /* Operations status of query request */
    val status = executionResult.getStatus.getStatus.name
    
    val schema = executionResult.getResultSchema.map(col => {
      
      CDAPColumnDesc(
        colName=col.getName.replace("dataset_",""),
        colType=col.getType,
        colComment=col.getComment,
        colPos=col.getPosition)
    
    }).toList
    
    /* Retrieve column data */
    
    val iter = executionResult.asScala
    val rows = ArrayBuffer.empty[List[_]]
    
    while (iter.hasNext) {
      
      val row = iter.next()
      val values = row.getColumns.asScala.toList
      
      rows += values
      
    }
    
    CDAPQueryResult(
      namespace=namespace,
      datasetName=datasetName,
      query=query,
      count=count,
      status=status,
      schema=schema,
      rows=rows.toList
    )

  }
  
  /********************************
   * 
   * PRIVATE METHODS
   * 
   *******************************/
  
  private def getAppID(nsID:NamespaceId, appName:String, appVersion:String):ApplicationId = {
    nsID.app(appName,appVersion)
  }
  
  private def getProgID(namespace:NamespaceId, appName:String, appVersion:String, progName:String, progType:String):ProgramId = {
    
    val app = getAppID(namespace,appName,appVersion)
    getProgID(app, progName, progType)
  
  }

  private def getProgID(app:ApplicationId,progName:String,progType:String):ProgramId = {
    app.program(ProgramType.valueOfPrettyName(progType),progName)
  }
 
  private def getPrograms(nsID:NamespaceId, appName:String, appVersion:String):List[ProgramRecord] = {
    /*
     * STEP #1: Transform application name & version 
     * into associated application identifier
     */
    val appId = nsID.app(appName,appVersion)
    /*
     * STEP #2: Determine programs of a certain application
     * and return
     */
    val programs = appClient.listPrograms(appId)
    programs.asScala.toList
    
  }
  
}

object CDAPContext {
  
  def main(args:Array[String]) {    
		
    CDAPConf.init()
    
    val props = CDAPConf.getProps
		val ctx = new CDAPContext(props)
    
    println(ctx.getPlugins("default"))
    
  }
}
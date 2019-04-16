package de.kp.elastic.cdap.core
    
import scala.util.matching.Regex
    /*
     * [
     * --- Application Logic Metric ---
     * system.app.log.debug, 
     * system.app.log.info, 
     * system.app.log.warn, 
     * 
     * --- Spark Metric ---
     * system.driver.BlockManager.disk.diskSpaceUsed_MB, 
     * system.driver.BlockManager.memory.maxMem_MB, 
     * system.driver.BlockManager.memory.memUsed_MB, 
     * system.driver.BlockManager.memory.remainingMem_MB, 
     * system.driver.DAGScheduler.job.activeJobs, 
     * system.driver.DAGScheduler.job.allJobs, 
     * system.driver.DAGScheduler.stage.failedStages, 
     * system.driver.DAGScheduler.stage.runningStages, 
     * system.driver.DAGScheduler.stage.waitingStages, 
     * 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_processingDelay, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_processingEndTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_processingStartTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_schedulingDelay, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_submissionTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastCompletedBatch_totalDelay, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastReceivedBatch_processingEndTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastReceivedBatch_processingStartTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastReceivedBatch_records, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.lastReceivedBatch_submissionTime, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.receivers, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.retainedCompletedBatches, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.runningBatches, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.totalCompletedBatches, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.totalProcessedRecords, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.totalReceivedRecords, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.unprocessedBatches, 
     * system.driver.DataStreamsSparkStreaming.StreamingMetrics.streaming.waitingBatches, 
     * 
     * system.metrics.emitted.count, 
     * system.program.killed.runs, 
     * user.Botnet Detector.records.in, 
     * user.Botnet Detector.records.out, 
     * user.Enron Consumer.records.out, 
     * user.File.process.time.avg, 
     * user.File.process.time.max, 
     * user.File.process.time.min, 
     * user.File.process.time.stddev, 
     * user.File.process.time.total, 
     * user.File.records.in, 
     * user.File.records.out, 
     * user.metrics.emitted.count]
     * 
     */

object MetricsUtil {
  /*
   * Prefixes to determine a certain metric type
   */
  private val APPLOG_METRICS  = "system.app.log"
  private val DATASET_METRICS = "system.dataset.store"
  private val STREAM_METRICS  = "system.collect"
  /*
   * Regexes to determine a certain metric type
   */
  private val SPARK_METRICS = "system.(.*)driver.(.*)".r
  
  /********************************
   * 
   * SYSTEM METRICS CONTEXT
   * 
   *******************************/
  
  def isDatasetMetric(name:String):Boolean = {
    /*
     * These metrics are available in a dataset context:
     * 
     * system.dataset.store.bytes: 	Number of bytes written
     * system.dataset.store.ops: 	  Operations (reads and writes) performed
     * system.dataset.store.reads:	Read operations performed
     * system.dataset.store.writes:	Write operations performed
     * 
     */    
    name.startsWith(DATASET_METRICS)
  }
  
  def isLogMetric(name:String):Boolean = {
    /*
     * These metrics are available in an application log context:
     * 
     * system.app.log.{debug, error, info, warn}	 
     * 
     * Number of debug, error, info, or warn log messages logged 
     * by an application or applications
     * 
     */
    name.startsWith(APPLOG_METRICS)
  }
  
  def isSparkMetric(name:String):Boolean = {
    /*
     * These metrics are available in a spark context  
     * 
     * system.<spark-id>.driver.BlockManager.disk.diskSpaceUsed_MB:	 Disk space used by the Block Manager
     * system.<spark-id>.driver.BlockManager.memory.maxMem_MB:	     Maximum memory given to the Block Manager
     * system.<spark-id>.driver.BlockManager.memory.memUsed_MB:			 Memory used by the Block Manager
     * system.<spark-id>.driver.BlockManager.memory.remainingMem_MB: Memory remaining to the Block Manager
     * system.<spark-id>.driver.DAGScheduler.job.activeJobs:	       Number of active jobs
     * system.<spark-id>.driver.DAGScheduler.job.allJobs:	           Total number of jobs
     * system.<spark-id>.driver.DAGScheduler.stage.failedStages:	   Number of failed stages
     * system.<spark-id>.driver.DAGScheduler.stage.runningStages:	   Number of running stages
     * system.<spark-id>.driver.DAGScheduler.stage.waitingStages:	   Number of waiting stages
     *         
     */
    val matches = SPARK_METRICS.findAllMatchIn(name)
    matches.isEmpty == false
  }
  
  def isStreamMetric(name:String):Boolean = {
    /*
     * These metrics are available in a stream context:
     * 
     * system.collect.events:	Number of events collected by the stream
     * system.collect.bytes:	Number of bytes collected by the stream
     * 
     */
    name.startsWith(STREAM_METRICS)    
  }
  /**
   * Retrieve metrics tags for all components of all applications
   */
  def getAllApp(namespace:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> "*")
  }
  /**
   * Retrieve metrics tags for all components of a specific
   * application
   */
  def getApp(namespace:String,app:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app)
  }
  /**
   * Retrieve metrics tags for all workers of an application
   */
  def getAllWorker(namespace:String,app:String):Map[String,String] = {
    /*
     * The tag 'worker' user here differs from documentation,
     * but we think that this is a documentation bug
     */
    Map("namespace" -> namespace, "app" -> app, "worker" -> "*")
  }
  /**
   * Retrieve metrics tags for a certain workers of an application
   */
  def getWorker(namespace:String,app:String,worker:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "worker" -> worker)
  }
  /**
   * Retrieve metrics tags for all spark programs of an application
   */
  def getAllSpark(namespace:String,app:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "spark" -> "*")
  }
  /**
   * Retrieve metrics tags for a certain spark program of an application
   */
  def getSpark(namespace:String,app:String,spark:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "spark" -> spark)
  }
  /**
   * Retrieve metrics tags for all services of an application
   */
  def getAllService(namespace:String,app:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "service" -> "*")
  }
  /**
   * Retrieve metrics tags for a certain service of an application
   */
  def getService(namespace:String,app:String,service:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "service" -> service)
  }
  /**
   * Retrieve metrics tags for all mapreduce of an application
   */
  def getAllMapReduce(namespace:String,app:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "mapreduce" -> "*")
  }
  /**
   * Retrieve metrics tags for a certain mapreduce of an application
   */
  def getMapReduce(namespace:String,app:String,mapreduce:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "mapreduce" -> mapreduce)
  }
  /**
   * Retrieve metric tags for all mappers of a certain mapreduce of 
   * an application
   */
  def getMapReduceMappers(namespace:String,app:String,mapreduce:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "mapreduce" -> mapreduce, "tasktype" -> "m")
  }
  /**
   * Retrieve metric tags for all reducers of a certain mapreduce of 
   * an application
   */
  def getMapReduceReducers(namespace:String,app:String,mapreduce:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "mapreduce" -> mapreduce, "tasktype" -> "r")
  }
  /**
   * Retrieve metrics tags for all flows of an application
   */
  def getAllFlow(namespace:String,app:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "flow" -> "*")
  }
  /**
   * Retrieve metrics tags for a certain flow of an application
   */
  def getFlow(namespace:String,app:String,flow:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "flow" -> flow)
  }
  /**
   * Retrieve metrics tags for a certain flowlet of a flow of 
   * an application
   */
  def getFlowlet(namespace:String,app:String,flow:String,flowlet:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "flow" -> flow, "flowlet" -> flowlet)
  }
  
  /********************************
   * 
   * DATASET METRICS CONTEXT
   * 
   *******************************/
  
  /**
   * Retrieve metrics tags for all dataset of a namespace
   */
  def getAllDataset(namespace:String):Map[String,String] = {
    Map("namespace" -> namespace, "dataset" -> "*")
  }
  
  /**
   * Retrieve metrics tags for a certain of a namespace
   */
  def getDataset(namespace:String,dataset:String):Map[String,String] = {
    Map("namespace" -> namespace, "dataset" -> dataset)
  }
  
  /**
   * Retrieve metrics tags for a certain dataset of an 
   * application
   */
  def getAppDataset(namespace:String,app:String,dataset:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "dataset" -> dataset)
  }
  
  /**
   * Retrieve metrics tags for a certain dataset of a specific 
   * flow of an application
   */
  def getFlowDataset(namespace:String,app:String,flow:String,dataset:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "flow" -> flow, "dataset" -> dataset)
  }
  
  /**
   * Retrieve metrics tags for a certain dataset of a specific 
   * flowlet of a flow of an application
   */
  def getFlowletDataset(namespace:String,app:String,flow:String,flowlet:String,dataset:String):Map[String,String] = {
    Map("namespace" -> namespace, "app" -> app, "flow" -> flow, "flowlet" -> flowlet, "dataset" -> dataset)
  }

}
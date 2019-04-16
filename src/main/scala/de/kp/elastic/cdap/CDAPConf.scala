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

import com.typesafe.config.{Config, ConfigFactory}
import java.util.Properties

object CDAPConf extends Serializable {

  private var config: Option[Config] = None
  
  def init(cfg:Option[Config] = None):Unit = {
    
    if (config.isDefined == false) {
      
      if (cfg.isDefined) config = cfg
      else {
        
        val path = "application.conf"
        config = Option(ConfigFactory.load(path))
        
      }
      
    }
    
  }
    
  def getProps(text:String):Properties = {
    
    val conf = ConfigFactory
      .parseString(text).getConfig("cdap")
      
    val props = new Properties()

    props.setProperty("sslEnabled", conf.getString("sslEnabled"))
    
    props.setProperty("alias",    conf.getString("alias"))
    props.setProperty("password", conf.getString("password")) 

    if (conf.getString("sslEnabled") == "no") {
      
      props.setProperty("host", conf.getString("router.host"))
      props.setProperty("port", conf.getInt("router.port").toString)
    
    } else {
      
      props.setProperty("host", conf.getString("router.ssl.host"))
      props.setProperty("port", conf.getInt("router.ssl.port").toString)
    
    }
    
    props
    
  }

  def getProps:Properties = {
    
    val conf = cdapConfig
    val props = new Properties()

    props.setProperty("sslEnabled", conf.getString("sslEnabled"))
    
    props.setProperty("alias",    conf.getString("alias"))
    props.setProperty("password", conf.getString("password")) 

    if (conf.getString("sslEnabled") == "no") {
      
      props.setProperty("host", conf.getString("router.host"))
      props.setProperty("port", conf.getInt("router.port").toString)
    
    } else {
      
      props.setProperty("host", conf.getString("router.ssl.host"))
      props.setProperty("port", conf.getInt("router.ssl.port").toString)
    
    }
    
    props

  }
  
  def getConfig = {
    if (config.isDefined) config.get else null
  }
  
  def bindingConfig = {
    if (config.isDefined) config.get.getConfig("binding") else null
  }
  
  def cdapConfig = {
    if (config.isDefined) config.get.getConfig("cdap") else null
  }
  
}

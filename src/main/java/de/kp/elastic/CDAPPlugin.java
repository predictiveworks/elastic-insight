package de.kp.elastic;
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

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import de.kp.elastic.action.AppAction;
import de.kp.elastic.action.AppsAction;
import de.kp.elastic.action.DatasetAction;
import de.kp.elastic.action.DatasetsAction;
import de.kp.elastic.action.ExistsAction;
import de.kp.elastic.action.MetricQueryAction;
import de.kp.elastic.action.MetricsAction;
import de.kp.elastic.action.ProgRunAction;
import de.kp.elastic.action.QueryAction;
import de.kp.elastic.action.RegisterAction;
import de.kp.elastic.action.StartAction;
import de.kp.elastic.action.StatusAction;
import de.kp.elastic.action.StopAction;
import de.kp.elastic.action.SystemServiceAction;
import de.kp.elastic.action.SystemServiceInstancesAction;
import de.kp.elastic.action.SystemServiceStatusAction;
import de.kp.elastic.action.SystemServicesAction;
import de.kp.elastic.action.SystemServicesStatusAction;
import de.kp.elastic.action.UpdateAction;

import java.util.List;
import java.util.function.Supplier;
import java.util.Arrays;

public class CDAPPlugin extends Plugin implements ActionPlugin {
	
    @Override
    public List<RestHandler> getRestHandlers(Settings settings,
                                             RestController restController,
                                             ClusterSettings clusterSettings,
                                             IndexScopedSettings indexScopedSettings,
                                             SettingsFilter settingsFilter,
                                             IndexNameExpressionResolver indexNameExpressionResolver,
                                             Supplier<DiscoveryNodes> nodesInCluster) {

        return Arrays.asList(
        		/** _cdap/apps **/
        		new AppsAction(settings, restController),
        		
        		/** _cdap/app/* **/
        		new AppAction(settings, restController),
        		new ExistsAction(settings, restController),
        		/**
        		 * Register or update an externally provided blueprint
        		 * in CDAP specific format as CDAP application
        		 * 
        		 * _cdap/app/register
        		 */
        		new RegisterAction(settings, restController),
        		/**
        		 * _cdap/app/update
        		 */
        		new UpdateAction(settings, restController),
        		/**
        		 * Start or stop the program associated with the 
        		 * associated (registered) blueprint
        		 * 
        		 * _cdap/app/start
        		 */
        		new StartAction(settings, restController),
        		/**
        		 * _cdap/app/stop
        		 */
        		new StopAction(settings, restController),
        		/**
        		 * Retrieve status of a certain program of a
        		 * specific application
        		 * 
        		 * _cdap/app/status
        		 */
        		new StatusAction(settings, restController),
        		/**
        		 * Retrieve run records of a certain program of a 
        		 * certain state and within a specific period of time
        		 * 
        		 * _cdap/app/runinfo
        		 */
        		new ProgRunAction(settings, restController),
        		/**
        		 * Retrieve the lists of datasets registered with
        		 * a certain namespace
        		 * 
        		 * _cdap/datasets
        		 */
        		new DatasetsAction(settings, restController),
        		/**
        		 * Retrieve information about a certain dataset
        		 * within a specific namespace
        		 * 
        		 * _cdap/dataset
        		 */
        		new DatasetAction(settings, restController),
        		/**
        		 * Explore registered dataset with an ad-hoc SQL-like
        		 * query statement
        		 * 
        		 * _cdap/dataset/query
        		 */
        		new QueryAction(settings, restController),
        		/**
        		 * Retrieve available metric names for a certain context
        		 * 
        		 * _cdap/dataset/metrics/search
        		 */
        		new MetricsAction(settings, restController),
        		/**
        		 * Retrieve available metric data for a certain context
        		 * 
        		 * _cdap/dataset/metrics/query
        		 */
        		new MetricQueryAction(settings, restController),
        		/**
        		 * Retrieve all system services
        		 * 
        		 * _cdap/system/services
        		 */
        		new SystemServicesAction(settings, restController),
        		/**
        		 * Retrieve all system services status
        		 * 
        		 * _cdap/system/services/status
        		 */
        		new SystemServicesStatusAction(settings, restController),
        		/**
        		 * Retrieve specific system service
        		 * 
        		 * _cdap/system/service
        		 */
        		new SystemServiceAction(settings, restController),
        		/**
        		 * Retrieve status of specific system service
        		 * 
        		 * _cdap/system/service/status
        		 */
        		new SystemServiceStatusAction(settings, restController),
        		/**
        		 * Retrieve instances of specific system service
        		 * 
        		 * _cdap/system/service/instances
        		 */
        		new SystemServiceInstancesAction(settings, restController)
        		
        		
        	);
    }

}

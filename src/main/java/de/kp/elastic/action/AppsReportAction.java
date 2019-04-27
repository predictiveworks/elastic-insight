package de.kp.elastic.action;
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

import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.stream.Collectors;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import de.kp.elastic.AppsReportRequest;
import de.kp.elastic.JsonResult;
import de.kp.elastic.cdap.CDAPConf;
import de.kp.elastic.cdap.job.CDAPJob;

public class AppsReportAction extends BaseRestHandler {

	private String ACTION_NAME = "AppsReportAction";
	private String CONFIG_PATH = "/application.conf";

	private CDAPJob job;

	public AppsReportAction(Settings settings, RestController controller) {
		super(settings);
		controller.registerHandler(POST, "/_cdap/apps/report", this);
		/*
		 * A privileged access to the application configuration file that contains the
		 * settings for CDAP connection and authorized user
		 */
		this.job = AccessController.doPrivileged((PrivilegedAction<CDAPJob>) () -> {

			InputStream stream = getClass().getResourceAsStream(CONFIG_PATH);
			if (stream != null) {

				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String config = reader.lines().collect(Collectors.joining(System.lineSeparator()));

				Properties props = CDAPConf.getProps(config);
				return new CDAPJob(props);

			} else
				return null;

		});

	}

	@Override
	public String getName() {
		return ACTION_NAME;
	}

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {

		return channel -> {
			try {

				JsonResult response = doRequest(request);

				XContentBuilder builder = channel.newBuilder();
				response.toXContent(builder, request);

				channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

			} catch (final Exception e) {
				channel.sendResponse(new BytesRestResponse(channel, e));
			}
		};
	}

	private JsonResult doRequest(RestRequest request) throws Exception {

		if (request.hasContentOrSourceParam()) {

			XContentParser parser = request.contentOrSourceParamParser();
			AppsReportRequest req = AppsReportRequest.parseRequest(parser);
			/*
			 * Extract request parameters and delegate execution to CDAPJob
			 */
			String namespace = req.getNamespace();
			Long startTime = req.getStartTime();
			Long endTime = req.getEndTime();
			
			String json = AccessController.doPrivileged((PrivilegedAction<String>) () -> {
				return job.getAppsReportAsJson(namespace, startTime, endTime);

			});

			JsonResult result = new JsonResult(json);
			return result;

		} else
			throw new Exception("[ERROR] The request body must not be empty.");

	}

}


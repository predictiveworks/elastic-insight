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

import de.kp.elastic.UpdateRequest;
import de.kp.elastic.StringResult;
import de.kp.elastic.cdap.CDAPConf;
import de.kp.elastic.cdap.job.CDAPJob;

public class UpdateAction extends BaseRestHandler {

	private String ACTION_NAME = "UpdateAction";
	private String CONFIG_PATH = "/application.conf";

	private CDAPJob job;

	public UpdateAction(Settings settings, RestController controller) {
		super(settings);
		controller.registerHandler(POST, "/_cdap/app/update", this);
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

				StringResult response = doRequest(request);

				XContentBuilder builder = channel.newBuilder();
				response.toXContent(builder, request);

				channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

			} catch (final Exception e) {
				channel.sendResponse(new BytesRestResponse(channel, e));
			}
		};
	}

	private StringResult doRequest(RestRequest request) throws Exception {

		if (request.hasContentOrSourceParam()) {

			XContentParser parser = request.contentOrSourceParamParser();
			UpdateRequest updateRequest = UpdateRequest.parseRequest(parser);
			/*
			 * Extract request parameters and delegate execution to CDAPJob
			 */
			String namespace = updateRequest.getNamespace();
			String appName = updateRequest.getAppName();
			String appVersion = updateRequest.getAppVersion();
			String blueprint = updateRequest.getBlueprint();

			String json = AccessController.doPrivileged((PrivilegedAction<String>) () -> {
				return job.updateApp(namespace, appName, appVersion, blueprint);

			});

			StringResult result = new StringResult(json);
			return result;

		} else
			throw new Exception("[ERROR] The request body must not be empty.");

	}

}


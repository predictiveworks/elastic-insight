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

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class UpdateRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField APP_NAME = new ParseField("appName");
	public static final ParseField APP_VERS = new ParseField("appVersion");
	public static final ParseField BLUEPRINT = new ParseField("blueprint");

	private static final ObjectParser<UpdateRequest, Void> PARSER = new ObjectParser<>("app", UpdateRequest::new);

	static {
		PARSER.declareString(UpdateRequest::setNamespace, NAMESPACE);
		PARSER.declareString(UpdateRequest::setAppName, APP_NAME);
		PARSER.declareString(UpdateRequest::setAppVersion, APP_VERS);
		PARSER.declareString(UpdateRequest::setBlueprint, BLUEPRINT);
	}

	public static UpdateRequest parseRequest(XContentParser parser) {
		UpdateRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String appName;
	private String appVersion;
	private String blueprint;

	public UpdateRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public void setBlueprint(String blueprint) {
		this.blueprint = blueprint;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getAppName() {
		return this.appName;
	}

	public String getAppVersion() {
		return this.appVersion;
	}

	public String getBlueprint() {
		return this.blueprint;
	}

}

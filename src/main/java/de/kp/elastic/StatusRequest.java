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

public class StatusRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField APP_NAME = new ParseField("appName");
	public static final ParseField APP_VERS = new ParseField("appVersion");
	public static final ParseField PROG_NAME = new ParseField("progName");
	public static final ParseField PROG_TYPE = new ParseField("progType");

	private static final ObjectParser<StatusRequest, Void> PARSER = new ObjectParser<>("prog", StatusRequest::new);

	static {
		PARSER.declareString(StatusRequest::setNamespace, NAMESPACE);
		PARSER.declareString(StatusRequest::setAppName, APP_NAME);
		PARSER.declareString(StatusRequest::setAppVersion, APP_VERS);
		PARSER.declareString(StatusRequest::setProgName, PROG_NAME);
		PARSER.declareString(StatusRequest::setProgType, PROG_TYPE);
	}

	public static StatusRequest parseRequest(XContentParser parser) {
		StatusRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String appName;
	private String appVersion;
	private String progName;
	private String progType;

	public StatusRequest() {
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

	public void setProgName(String progName) {
		this.progName = progName;
	}

	public void setProgType(String progType) {
		this.progType = progType;
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

	public String getProgName() {
		return this.progName;
	}

	public String getProgType() {
		return this.progType;
	}

}

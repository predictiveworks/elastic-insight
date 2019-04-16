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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class StartRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField APP_NAME = new ParseField("appName");
	public static final ParseField APP_VERS = new ParseField("appVersion");
	public static final ParseField PROG_NAME = new ParseField("progName");
	public static final ParseField PROG_TYPE = new ParseField("progType");
	public static final ParseField RUNTIME_ARGS = new ParseField("runtimeArgs");

	private static final ObjectParser<StartRequest, Void> PARSER = new ObjectParser<>("prog", StartRequest::new);

	static {
		PARSER.declareString(StartRequest::setNamespace, NAMESPACE);
		PARSER.declareString(StartRequest::setAppName, APP_NAME);
		PARSER.declareString(StartRequest::setAppVersion, APP_VERS);
		PARSER.declareString(StartRequest::setProgName, PROG_NAME);
		PARSER.declareString(StartRequest::setProgType, PROG_TYPE);

		PARSER.<Map<String, Object>>declareObject(StartRequest::setRuntimeArgs, (p, c) -> p.map(), RUNTIME_ARGS);
	}

	public static StartRequest parseRequest(XContentParser parser) {
		StartRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String appName;
	private String appVersion;
	private String progName;
	private String progType;
	private Map<String, String> runtimeArgs;

	public StartRequest() {
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

	public void setRuntimeArgs(Map<String, Object> runtimeArgs) {

		if (runtimeArgs == null) {
			this.runtimeArgs = new HashMap<String, String>();

		} else {

			this.runtimeArgs = runtimeArgs.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
		}

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
	
	public Map<String,String> getRuntimeArgs() {
		return this.runtimeArgs;
	}

}

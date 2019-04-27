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

public class AppReportRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField APP_NAME = new ParseField("appName");
	public static final ParseField APP_VERS = new ParseField("appVersion");
	public static final ParseField START_TIME = new ParseField("startTime");
	public static final ParseField END_TIME = new ParseField("endTime");

	private static final ObjectParser<AppReportRequest, Void> PARSER = new ObjectParser<>("app", AppReportRequest::new);

	static {
		PARSER.declareString(AppReportRequest::setNamespace, NAMESPACE);
		PARSER.declareString(AppReportRequest::setAppName, APP_NAME);
		PARSER.declareString(AppReportRequest::setAppVersion, APP_VERS);
		PARSER.declareLong(AppReportRequest::setStartTime, START_TIME);
		PARSER.declareLong(AppReportRequest::setEndTime, END_TIME);
	}

	public static AppReportRequest parseRequest(XContentParser parser) {
		AppReportRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String appName;
	private String appVersion;
	
	private Long startTime;
	private Long endTime;

	public AppReportRequest() {
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

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
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

	public Long getStartTime() {
		return this.startTime;
	}

	public Long getEndTime() {
		return this.endTime;
	}

}    

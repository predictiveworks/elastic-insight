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

public class ProgRunRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField APP_NAME = new ParseField("appName");
	public static final ParseField APP_VERS = new ParseField("appVersion");
	public static final ParseField PROG_NAME = new ParseField("progName");
	public static final ParseField PROG_TYPE = new ParseField("progType");
	public static final ParseField PROG_STATE = new ParseField("progState");
	public static final ParseField START_TIME = new ParseField("startTime");
	public static final ParseField END_TIME = new ParseField("endTime");
	public static final ParseField LIMIT = new ParseField("limit");

	private static final ObjectParser<ProgRunRequest, Void> PARSER = new ObjectParser<>("prog", ProgRunRequest::new);

	static {
		PARSER.declareString(ProgRunRequest::setNamespace, NAMESPACE);
		PARSER.declareString(ProgRunRequest::setAppName, APP_NAME);
		PARSER.declareString(ProgRunRequest::setAppVersion, APP_VERS);
		PARSER.declareString(ProgRunRequest::setProgName, PROG_NAME);
		PARSER.declareString(ProgRunRequest::setProgType, PROG_TYPE);
		PARSER.declareString(ProgRunRequest::setProgState, PROG_STATE);
		PARSER.declareLong(ProgRunRequest::setStartTime, START_TIME);
		PARSER.declareLong(ProgRunRequest::setEndTime, END_TIME);
		PARSER.declareInt(ProgRunRequest::setLimit, LIMIT);
	}

	public static ProgRunRequest parseRequest(XContentParser parser) {
		ProgRunRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String appName;
	private String appVersion;
	private String progName;
	private String progType;
	private String progState;

	private Long startTime;
	private Long endTime;
	private Integer limit;
	
	public ProgRunRequest() {
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

	public void setProgState(String progState) {
		this.progState = progState;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
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

	public String getProgState() {
		return progState;
	}

	public Long getStartTime() {
		return startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public Integer getLimit() {
		return limit;
	}

}

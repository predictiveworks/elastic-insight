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

public class AppsReportRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField START_TIME = new ParseField("startTime");
	public static final ParseField END_TIME = new ParseField("endTime");

	private static final ObjectParser<AppsReportRequest, Void> PARSER = new ObjectParser<>("apps", AppsReportRequest::new);

	static {
		PARSER.declareString(AppsReportRequest::setNamespace, NAMESPACE);
		PARSER.declareLong(AppsReportRequest::setStartTime, START_TIME);
		PARSER.declareLong(AppsReportRequest::setEndTime, END_TIME);
	}

	public static AppsReportRequest parseRequest(XContentParser parser) {
		AppsReportRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	
	private Long startTime;
	private Long endTime;

	public AppsReportRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
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

	public Long getStartTime() {
		return this.startTime;
	}

	public Long getEndTime() {
		return this.endTime;
	}

}    

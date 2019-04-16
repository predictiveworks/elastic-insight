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

public class QueryRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField DATASET_NAME = new ParseField("datasetName");
	public static final ParseField QUERY = new ParseField("query");

	private static final ObjectParser<QueryRequest, Void> PARSER = new ObjectParser<>("dataset", QueryRequest::new);

	static {
		PARSER.declareString(QueryRequest::setNamespace, NAMESPACE);
		PARSER.declareString(QueryRequest::setDatasetName, DATASET_NAME);
		PARSER.declareString(QueryRequest::setQuery, QUERY);
	}

	public static QueryRequest parseRequest(XContentParser parser) {
		QueryRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String datasetName;
	private String query;

	public QueryRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getDatasetName() {
		return this.datasetName;
	}

	public String getQuery() {
		return this.query;
	}

}    

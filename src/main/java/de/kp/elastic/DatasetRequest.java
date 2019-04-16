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

public class DatasetRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField DATASET_NAME = new ParseField("datasetName");

	private static final ObjectParser<DatasetRequest, Void> PARSER = new ObjectParser<>("dataset", DatasetRequest::new);

	static {
		PARSER.declareString(DatasetRequest::setNamespace, NAMESPACE);
		PARSER.declareString(DatasetRequest::setDatasetName, DATASET_NAME);
	}

	public static DatasetRequest parseRequest(XContentParser parser) {
		DatasetRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private String datasetName;

	public DatasetRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getDatasetName() {
		return this.datasetName;
	}

}    

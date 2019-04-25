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

public class PluginRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");

	private static final ObjectParser<PluginRequest, Void> PARSER = new ObjectParser<>("plugins", PluginRequest::new);

	static {
		PARSER.declareString(PluginRequest::setNamespace, NAMESPACE);
	}

	public static PluginRequest parseRequest(XContentParser parser) {
		PluginRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;

	public PluginRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return this.namespace;
	}

}    


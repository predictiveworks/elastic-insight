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

public class MetricsRequest {

	public static final ParseField NAMESPACE = new ParseField("namespace");
	public static final ParseField CONTEXT = new ParseField("context");

	private static final ObjectParser<MetricsRequest, Void> PARSER = new ObjectParser<>("metrics", MetricsRequest::new);

	static {
		PARSER.declareString(MetricsRequest::setNamespace, NAMESPACE);
		PARSER.<Map<String, Object>>declareObject(MetricsRequest::setContext, (p, c) -> p.map(), CONTEXT);
	}

	public static MetricsRequest parseRequest(XContentParser parser) {
		MetricsRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String namespace;
	private Map<String, String> context;

	public MetricsRequest() {
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setContext(Map<String, Object> context) {

		if (context == null) {
			this.context = new HashMap<String, String>();

		} else {

			this.context = context.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
		}

	}

	public String getNamespace() {
		return this.namespace;
	}
	
	public Map<String,String> getContext() {
		return this.context;
	}

}

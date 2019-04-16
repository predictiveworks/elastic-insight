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

public class MetricQueryRequest {

	public static final ParseField METRIC = new ParseField("metric");
	public static final ParseField TAGS = new ParseField("metricTags");

	private static final ObjectParser<MetricQueryRequest, Void> PARSER = new ObjectParser<>("metrics", MetricQueryRequest::new);

	static {
		PARSER.declareString(MetricQueryRequest::setMetric, METRIC);
		PARSER.<Map<String, Object>>declareObject(MetricQueryRequest::setTags, (p, c) -> p.map(), TAGS);
	}

	public static MetricQueryRequest parseRequest(XContentParser parser) {
		MetricQueryRequest request = PARSER.apply(parser, null);
		return request;
	}

	private String metric;
	private Map<String, String> tags;

	public MetricQueryRequest() {
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public void setTags(Map<String, Object> tags) {

		if (tags == null) {
			this.tags = new HashMap<String, String>();

		} else {

			this.tags = tags.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
		}

	}

	public String getMetric() {
		return this.metric;
	}
	
	public Map<String,String> getTags() {
		return this.tags;
	}

}

package org.oslo.origo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiBase {
	
	@JsonProperty("last_updated")
	private long lastUpdated;
	
	@JsonProperty("ttl")
	private int timeToLive;
	
	@JsonProperty("data")
	private ApiDataWrapper apiDataWrapper;

	long getLastUpdated() {
		return lastUpdated;
	}

	void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	int getTimeToLive() {
		return timeToLive;
	}

	void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	ApiDataWrapper getApiDataWrapper() {
		return apiDataWrapper;
	}

	void setApiDataWrapper(ApiDataWrapper apiDataWrapper) {
		this.apiDataWrapper = apiDataWrapper;
	}
	
	
	

}

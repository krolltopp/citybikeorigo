package org.oslo.origo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDataWrapper {
	
	@JsonProperty("stations")
	private List<Station> stations;

	List<Station> getStations() {
		return stations;
	}

	void setStationList(List<Station> stations) {
		this.stations = stations;
	}
	
}

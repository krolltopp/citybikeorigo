package org.oslo.origo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {

	@JsonProperty("station_id")
	private String stationId;
	
	@JsonProperty(required = false)
	private String name;
	
	@JsonProperty(value = "num_docks_available", required = false)
	private int numDocksAvailable;
	
	@JsonProperty(value = "num_bikes_available", required = false)
	private int numBikesAvailable;


	public String getStationId() {
		return stationId;
	}

	void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public int getNumDocksAvailable() {
		return numDocksAvailable;
	}

	void setNumDocksAvailable(int numDocksAvailable) {
		this.numDocksAvailable = numDocksAvailable;
	}

	public int getNumBikesAvailable() {
		return numBikesAvailable;
	}

	void setNumBikesAvailable(int numBikesAvailable) {
		this.numBikesAvailable = numBikesAvailable;
	}
	
	

}

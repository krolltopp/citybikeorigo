package org.oslo.origo;

import java.util.List;

public interface CityBikeClient {
	
	public List<Station> getStationsCompleteStatus() throws Exception;

}

package org.oslo.origo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oslo.origo.CityBikeClientImpl.CityBikeUrl;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@ExtendWith(MockitoExtension.class)
class CityBikeClientImplTest {
	
	private CityBikeClient cityBikeClient;

	@Mock
	private HttpRequestFactory httpRequestFactoryMock;
	
	@Mock
	private HttpResponse httpResponseStationInfoMock;
	
	@Mock
	private HttpResponse httpResponseStationStatusMock;
	
	@Mock
	HttpRequest httpRequestStationInfoMock;
	
	@Mock
	HttpRequest httpRequestStationStatusMock;
	
	
	@BeforeEach
	public void before() throws IOException {
		cityBikeClient = new CityBikeClientImpl(httpRequestFactoryMock, CityBikeClientImpl.createBackOff());
		when(httpRequestFactoryMock.buildGetRequest(new CityBikeUrl(CityBikeClientImpl.STATION_INFO_URL))).thenReturn(httpRequestStationInfoMock);
		when(httpRequestFactoryMock.buildGetRequest(new CityBikeUrl(CityBikeClientImpl.STATION_STATUS_URL))).thenReturn(httpRequestStationStatusMock);
		when(httpRequestStationInfoMock.getHeaders()).thenReturn(new HttpHeaders());
		when(httpRequestStationStatusMock.getHeaders()).thenReturn(new HttpHeaders());
		when(httpRequestStationInfoMock.execute()).thenReturn(httpResponseStationInfoMock);
		when(httpRequestStationStatusMock.execute()).thenReturn(httpResponseStationStatusMock);
		when(httpResponseStationInfoMock.parseAsString()).thenReturn(loadTestJson("stations-info.json"));
		when(httpResponseStationStatusMock.parseAsString()).thenReturn(loadTestJson("stations-status.json"));
	}
	
	@Test
	void getStationCompleteStatus_should_return_available_bikes_and_docks_per_station() throws Exception {
		List<Station> stations = cityBikeClient.getStationsCompleteStatus();
		assertEquals(true, stations.size() > 0);
		long validStationsCount  = stations.stream().filter(s -> s.getNumBikesAvailable() >= 0 && s.getNumDocksAvailable() >= 0).count();
		assertEquals(stations.size(), validStationsCount);
	}
	
	private String loadTestJson(String fileName) {
        URL url = Resources.getResource(getClass(), fileName);
        try {
            String data = Resources.toString(url, Charsets.UTF_8);
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load a JSON file.", e);
        }
    }

}

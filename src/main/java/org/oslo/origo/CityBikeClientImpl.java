package org.oslo.origo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.ExponentialBackOff;
//import com.google.common.base.Joiner;

class CityBikeClientImpl implements CityBikeClient {

	private static final String CUSTOM_HEADER_NAME = "client-name";
	private static final String CUSTOM_HEADER_VALUE = "anders-city-bike-crawler";
	public static final String STATION_INFO_URL = "http://gbfs.urbansharing.com/oslobysykkel.no/station_information.json";
	public static final String STATION_STATUS_URL = "http://gbfs.urbansharing.com/oslobysykkel.no/station_status.json";

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static Logger LOGGER = Logger.getLogger(CityBikeClientImpl.class.getSimpleName());

	private HttpRequestFactory httpRequestFactory;
	private ObjectMapper objectMapper;
	private ExponentialBackOff exponentialBackOff;

	public CityBikeClientImpl() {
		httpRequestFactory = createFactory();
		objectMapper = new ObjectMapper();
		exponentialBackOff = createBackOff();
	}
	
	public CityBikeClientImpl(HttpRequestFactory httpRequestFactory, ExponentialBackOff exponentialBackOff) {
		this.httpRequestFactory = httpRequestFactory;
		this.exponentialBackOff = exponentialBackOff;
		objectMapper = new ObjectMapper();
	}

	public List<Station> getStationsCompleteStatus() throws Exception {

		Map<String, Station> stationsInfoMap = getStations(STATION_INFO_URL).stream()
				.collect(Collectors.toMap(Station::getStationId, Function.identity()));
		List<Station> stationsStatus = getStations(STATION_STATUS_URL);
		stationsStatus.forEach(s -> {
			stationsInfoMap.merge(s.getStationId(), s, (s1, s2) -> {
				s1.setNumDocksAvailable(s2.getNumDocksAvailable());
				s1.setNumBikesAvailable(s2.getNumBikesAvailable());
				return s1;
			});
		});

		List<Station> result = stationsInfoMap.values().stream()
				.sorted((s1, s2) -> s1.getName().compareTo(s2.getName())).collect(Collectors.toList());
		LOGGER.info(format(result));
		return result;
	}

	private List<Station> getStations(String url) throws Exception {
		HttpRequest httpRequest = httpRequestFactory.buildGetRequest(new CityBikeUrl(url));
		addCustomHeader(httpRequest);
		httpRequest.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(exponentialBackOff));
		String rawResponse = httpRequest.execute().parseAsString();
		ApiBase apiBase = parseJson(rawResponse);
		return apiBase.getApiDataWrapper().getStations();
	}

	public static HttpRequestFactory createFactory() {
		return HTTP_TRANSPORT.createRequestFactory();
	}

	public static ExponentialBackOff createBackOff() {
		return new ExponentialBackOff.Builder().setInitialIntervalMillis(500).setMaxElapsedTimeMillis(900000)
				.setMaxIntervalMillis(6000).setMultiplier(1.5).setRandomizationFactor(0.5).build();
	}

	private void addCustomHeader(HttpRequest httpRequest) {
		HttpHeaders headers = httpRequest.getHeaders();
		headers.set(CUSTOM_HEADER_NAME, CUSTOM_HEADER_VALUE);
	}

	private ApiBase parseJson(String json) throws IOException {
		return objectMapper.readerFor(ApiBase.class).readValue(json);
	}

	private String format(List<Station> stations) throws JsonProcessingException {
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stations);
	}

	public static class CityBikeUrl extends GenericUrl {

		public CityBikeUrl(String url) {
			super(url);
		}
	}

}

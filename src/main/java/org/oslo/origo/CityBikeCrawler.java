package org.oslo.origo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CityBikeCrawler {

	private static final Logger LOGGER = Logger.getLogger(CityBikeCrawler.class.getSimpleName());

	static {
		InputStream stream = CityBikeCrawler.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void crawl() {
		CityBikeClient cityBikeClient = new CityBikeClientImpl();
		try {
			List<Station> stations = cityBikeClient.getStationsCompleteStatus();
			dumpToFile(stations);

		} catch (Exception e) {
			LOGGER.info("Failed computing CityBike status with: " + e.getMessage());
		}
	}
	
	void dumpToFile(List<Station> stations) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		FileWriter writer = new FileWriter("stations.csv");
		StatefulBeanToCsv<Station> sbc = new StatefulBeanToCsvBuilder<Station>(writer)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
				.build();
		sbc.write(stations);
		writer.close();
	}

	public static void main(String[] args) {
		CityBikeCrawler cityBikeCrawler = new CityBikeCrawler();
		cityBikeCrawler.crawl();
	}

}

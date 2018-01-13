package com.ensai.pigudf;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeoDistance extends EvalFunc<Integer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeoDistance.class);
	private static final int EARTH_RADIUS_KM = 6371;

	@Override
	public Integer exec(Tuple input) throws IOException {
		Integer distance = null;
		if (checkInputIsOk(input)) {
			double lat1 = (Double) input.get(0);
			double lng1 = (Double) input.get(1);
			double lat2 = (Double) input.get(2);
			double lng2 = (Double) input.get(3);
			Integer earthRadiusKm = (Integer) input.get(4); // km
			if (earthRadiusKm == null) {
				earthRadiusKm = EARTH_RADIUS_KM;
			}
			distance = getDistance(lat1, lng1, lat2, lng2, earthRadiusKm);
		}
		return distance;
	}

	private boolean checkInputIsOk(Tuple input) throws ExecException {
		boolean isOK = true;
		if (input == null) {
			isOK = false;
			LOGGER.error("Cannot compute geospace distance when input is NULL.");
		} else if (input.size() != 5) {
			isOK = false;
			LOGGER.error("Cannot compute geospace distance with " + input
					+ ". Expecting (latitude_1,longitude_1,latitude_2,longitude_2,earth_radius_in_km).");
		} else if (input.get(0) == null || !(input.get(0) instanceof Double)) {
			isOK = false;
			LOGGER.error("Incorrect value for parameter latitude_1. Expecting not null double.");
		} else if (input.get(1) == null || !(input.get(1) instanceof Double)) {
			isOK = false;
			LOGGER.error("Incorrect value for parameter longitude_1. Expecting not null double.");
		} else if (input.get(2) == null || !(input.get(2) instanceof Double)) {
			isOK = false;
			LOGGER.error("Incorrect value for parameter latitude_2. Expecting not null double.");
		} else if (input.get(3) == null || !(input.get(3) instanceof Double)) {
			isOK = false;
			LOGGER.error("Incorrect value for parameter longitude_2. Expecting not null double.");
		} else if (input.get(4) != null && !(input.get(4) instanceof Integer)) {
			isOK = false;
			LOGGER.error("Incorrect value for parameter earth_radius. Expecting null or integer.");
		}
		return isOK;
	}

	public static int getDistance(double lat1, double lng1, double lat2, double lng2) {
		return getDistance(lat1, lng1, lat2, lng2, EARTH_RADIUS_KM);
	}

	public static int getDistance(double lat1, double lng1, double lat2, double lng2, int R) {
		double distance = -1;
		double φ1 = Math.toRadians(lat1);
		double φ2 = Math.toRadians(lat2);
		double Δφ = Math.toRadians(lat2 - lat1);
		double Δλ = Math.toRadians(lng2 - lng1);
		double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2)
				+ Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		distance = (R * c) + 1;
		return (int) distance;
	}
}

package com.go.jek.godrive;

/**
 * Created by kumardev on 11/26/2016.
 */

public class Config {

//    public static final String FIREBASE_URL_GETGARAGE = "https://greenwire-2b2ad.firebaseio.com/jek.json";
    public static final String FIREBASE_URL_GETGARAGE = "http://gojekwebapi.azurewebsites.net/api/Garages/GetGarages";
    public static final String FIREBASE_URL_GETFUEL = "https://greenwire-2b2ad.firebaseio.com/fuel.json";
    public static final String FIREBASE_URL_SENSORS = "https://greenwire-2b2ad.firebaseio.com/sensordata";

    public static final String PACKAGE_NAME ="com.go.jek.godrive";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";


}

package com.example.jms.touristapp.Interface;

/**
 *  Interface for Activity for require some operations depends on internet connectivity or gps service status
 */
public interface RequiredConnectionActivityImpl {


    /**
     * This method must be loaded only when there is an internet connection
     */
    void onInternetConnected();

    /**
     * This method must be loaded only when the gps location is enabled
     */
    void onLocationConnected();



}

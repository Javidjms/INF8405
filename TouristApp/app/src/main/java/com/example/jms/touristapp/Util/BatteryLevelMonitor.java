package com.example.jms.touristapp.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Monitor for battery level
 */
public class BatteryLevelMonitor extends BroadcastReceiver {

    private static BatteryLevelMonitor mInstance = null; // Single instance (singleton)
    // level float variable
    private Float initialBatteryLevel;
    private float lastBatteryLevel;
   // battery status intent
    private Intent batteryStatus;

    /**
     * Get an instance of the BatteryLevelMonitor
     * @return instance
     */
    public static BatteryLevelMonitor getInstance(){
        if(mInstance == null)
            mInstance = new BatteryLevelMonitor();
        return mInstance;
    }

    /**
     * BatteryLevelMonitor constructor/initilizer
     */
    private BatteryLevelMonitor(){
        initialBatteryLevel = 0f;
        lastBatteryLevel = 0f;
        batteryStatus = new Intent();
    }

    /**
     * Initialize the initial battery level
     */
    public void initializeBatteryLevel() {
        initialBatteryLevel = 0f;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, intentFilter);
        // Set initial battery level (only once)
        if(initialBatteryLevel != null && initialBatteryLevel==0f){
            initialBatteryLevel = getCurrentBatteryLevel();
        }
    }

    // Get the current battery level
    public float getCurrentBatteryLevel() {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // By prevention but can be avoided
        if(level==-1 || scale ==-1){
            return 0f;
        }
        // Compute current battery level percentage
        float batteryPct = (((float) level) /((float)scale)) * 100.0f;
        return batteryPct;
    }

    // get the last battery usage with the latest saved level
    public float getLastBatteryUsage() {
        float latestBatteryLevel = getCurrentBatteryLevel();
        return lastBatteryLevel - latestBatteryLevel;
    }

    // Get the total battery usage
    public float getTotalBatteryUsage() {
        return initialBatteryLevel - getCurrentBatteryLevel() ;
    }

    // Save the current battery level
    public void saveCurrentBatteryLevel() {
        lastBatteryLevel = getCurrentBatteryLevel();
    }


}
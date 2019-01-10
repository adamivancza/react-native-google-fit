/**
 * Copyright (c) 2017-present, Stanislav Doskalenko - doskalenko.s@gmail.com
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Based on Asim Malik android source code, copyright (c) 2015
 *
 **/

package com.reactnative.googlefit;

import android.app.Activity;
import com.reactnative.googlefit.GoogleFitManager;

import com.facebook.react.bridge.ReactContext;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.data.DataType;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.fitness.data.Subscription;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class SavingSteps {

    private ReactContext reactContext;
    private GoogleFitManager googleFitManager;
    private Activity activity;

    private static final String TAG = "SavingSteps";

    public SavingSteps (ReactContext reactContext, GoogleFitManager googleFitManager, Activity activity) {

        this.reactContext = reactContext;
        this.googleFitManager = googleFitManager;
        this.activity = activity;

    }

    public boolean saveOneStep () {

      Calendar cal = Calendar.getInstance();
      Date now = new Date();
      cal.setTime(now);
      cal.add(Calendar.MINUTE, 0);
      long endTime = cal.getTimeInMillis();
      cal.add(Calendar.MINUTE, -50);
      long startTime = cal.getTimeInMillis();

      // Create a data source
      DataSource dataSource = new DataSource.Builder()
              .setAppPackageName(activity)
              .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
              .setStreamName(TAG + " - step count")
              .setType(DataSource.TYPE_RAW)
              .build();

      // Create a data set
      DataSet dataSet = DataSet.create(dataSource);
      DataPoint dataPoint = dataSet.createDataPoint()
              .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
      dataPoint.getValue(Field.FIELD_STEPS).setInt(1);
      dataSet.add(dataPoint);

      DataUpdateRequest request = new DataUpdateRequest.Builder()
              .setDataSet(dataSet)
              .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
              .build();

      com.google.android.gms.common.api.Status updateStatus =
              Fitness.HistoryApi.updateData(googleFitManager.getGoogleApiClient(), request)
                      .await(1, TimeUnit.MINUTES);

      Log.i(TAG, "updateStatus: " + updateStatus.toString());
      Log.i(TAG, "updateStatus success: " + updateStatus.isSuccess());

      return updateStatus.isSuccess();
        
    }
}
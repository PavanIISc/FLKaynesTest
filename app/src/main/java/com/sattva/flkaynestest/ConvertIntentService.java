package com.sattva.flkaynestest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConvertIntentService extends IntentService {

    public ConvertIntentService() {
        super("ConvertIntentService");
    }

    private static double mCheck = Math.pow(2, 23);
    private static double mVref = 4.5;
    private static double mGain = 24;
    private static double mCheckDivide = 2 * mCheck;
    private Queue<String> mStringArray = new LinkedList();
    private double mInput[][] = new double[ApplicationUtils.bufferLength][4];
    private List<String> mStringCheck;


    Intent sendIntent = new Intent("to_display_data");

    @Override
    protected void onHandleIntent(Intent intent) {



        ApplicationUtils.outComFlag = ApplicationUtils.STOP_STREAM;
        populateInputArray();

        TestInternalSignalAlgo test = new TestInternalSignalAlgo();
        double[] channel_frequency = test.internalFreqTest(mInput, ApplicationUtils.functionOffset);

        sendIntent.putExtra("double_out", channel_frequency);

        LocalBroadcastManager.getInstance(ConvertIntentService.this).sendBroadcast(sendIntent);

    }

    private void feedInputArray(String iInputString, int iInputIndex) {
       //Log.e("feedInputArray", iInputString);
        for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
            mInput[iInputIndex][aInputChannelIndex] = stringToDouble(iInputString.substring(6 * aInputChannelIndex + 1, 6 * aInputChannelIndex + 7));
//	        if (aInputChannelIndex == 0) {
//	            ApplicationUtils.mInputArrayUc[iInputIndex] = ApplicationUtils.mInputArray[iInputIndex][aInputChannelIndex];
//	        }
        }
    }


    private void populateInputArray() {
        int aInputArrayCounter = 0;
        String aSample = getNextValidSample();

        //Log.e("PopulateArray", aSample);

        int aLastIndex = Character.getNumericValue(aSample.charAt(0));

        feedInputArray(aSample, aInputArrayCounter);


        for (aInputArrayCounter++; aInputArrayCounter < ApplicationUtils.dynamicDataStore.size(); aInputArrayCounter++) {
            aSample = getNextValidSample();

           // Log.e("PopulateArray", aSample);

            int aCurrentIndex = Character.getNumericValue(aSample.charAt(0));
            int aIndexDiff = aCurrentIndex - aLastIndex;
            if (aIndexDiff <= 0)
                aIndexDiff += 10;
            // DO CHANGE IN THIS LINE
            if (aIndexDiff == 1 || aIndexDiff == -9) {
                feedInputArray(aSample, aInputArrayCounter);
            } else {
                aInputArrayCounter += aIndexDiff-1;
                feedInputArray(aSample, aInputArrayCounter);
                interpolate(aInputArrayCounter, aInputArrayCounter - aIndexDiff, aLastIndex);
            }
            aLastIndex = Character.getNumericValue(aSample.charAt(0));
        }

    }

    private String getNextValidSample() {
        String aValidSample = "";
        if (ApplicationUtils.dynamicDataStore.size() > 0) {
            do {
                aValidSample = ApplicationUtils.dynamicDataStore.remove();
            } while (aValidSample.length() != 25 && mStringArray.size() > 0);
        }
        return aValidSample;
    }

    private void interpolate(int iCurrentInputIndex, int iStartIndex, int iEndIndex) {
        System.out.println("iCurrentInputIndex: " + iCurrentInputIndex);
        System.out.println("iStartIndex: " + iStartIndex);
        System.out.println("iEndIndex: " + iEndIndex);
        for (int k = iStartIndex + 1; k < iCurrentInputIndex; k++) {
            for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
                // DO CHANGE IN THIS LINE
                mInput[k][aInputChannelIndex] = mInput[iStartIndex][aInputChannelIndex] + (mInput[iCurrentInputIndex][aInputChannelIndex] - mInput[iStartIndex][aInputChannelIndex]) / (iCurrentInputIndex - iStartIndex) * (k-iStartIndex);
            }
        }
    }

    private double stringToDouble(String iChannelInput) {
        return doubleConv(new BigInteger(iChannelInput, 16).doubleValue());
    }

    private double doubleConv(double iDoubleValue) {
        double aOut;
        if (iDoubleValue >= mCheck) {
            aOut = (iDoubleValue - mCheckDivide) * mVref / (mCheck - 1) / mGain;
        } else {
            aOut = iDoubleValue / (mCheck - 1) / mGain * mVref;
        }
        return aOut;
    }

}


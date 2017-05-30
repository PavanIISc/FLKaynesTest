package com.sattva.flkaynestest;

import android.text.Editable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Vibhav on 16/11/16.
 */
public class ApplicationUtils {

    public static final int IDLE = 0;


    public static Queue<String> dynamicDataStore = new LinkedList<String>();
    public static int bufferLength = 15000;
    public static int chan_select = 0;


    public static final int PROCESSING = 1;



    public static int convert_flag = 1;
    public static int plot_flag = 1;

    public static int conversion_flag = IDLE;

    public static double[][] input_array = new double[15000][4];
    public static double[][] input_array_uc = new double[15000][4];
    public static final double[][] test_input_array = new double[15000][4];
    public static long startMS;
    public static int[] FQRS;
    public static int[] MQRS;
    public static int test_printer_flag = 1;

    public static float x_entry_diff = 0;
    public static int sample_set = 0;


    public static long sleep_time = (long) 0.984;

    public static int outComFlag = 0;

    public static int DONT_COMMUNICATE = 0;
    public static int STOP_STREAM = 1;
    public static int START_INTERNAL_TEST = 2;
    public static int START_EXTERNAL_TEST = 3;
    public static int START_PATIENT_TEST = 4;

    public static double functionOffset = 0;

    public static double internalOffset = 0;
    public static double externalOffset = 0.1;

    public static double lowerBoundary;
    public static double upperBoundary;


    public static double internalLowerBoundary = 0.9;
    public static double internalUpperBoundary = 1.0;

    public static double externalLowerBoundary = 99.0;
    public static double externalUpperBoundary = 101.0;

    public static int patientTestFlag = 0;

    public static Editable fileTextInput;
    public static int enableButtons = 0;
   // public static int readyForTest = 0;

}

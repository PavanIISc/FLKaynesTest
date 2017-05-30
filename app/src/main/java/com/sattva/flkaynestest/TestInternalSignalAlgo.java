package com.sattva.flkaynestest;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Vibhav on 25/01/17.
 */
public class TestInternalSignalAlgo {

    public static double[] internalFreqTest(double[][] input, double offset)
    {
        Log.e("internalFreqTest", "" + input[0][0] + ", " + input[100][0]);

        int no_of_channels = input[0].length;
        int no_of_samples = input.length;

        double frequency_channel[] = new double[no_of_channels];
        LinkedList<Integer> positive_Peak_Locations = new LinkedList<>();
        LinkedList<Integer> negative_Peak_Locations = new LinkedList<>();


        int positiveCounter;
        int negativeCounter;
        int changeFlag0;
        int changeFlag;

        for (int cols = 0; cols < no_of_channels; cols++)
        {

            positiveCounter = 0;
            negativeCounter = 0;

            changeFlag = 0;
            changeFlag0 = 0;

            for (int i = 0; i<no_of_samples; i++)
            {
                changeFlag0 = changeFlag;
                if (input[i][cols] > offset)
                {
                    changeFlag = 1;

                    if (changeFlag0 != changeFlag)
                    {
                        positive_Peak_Locations.add(positiveCounter);
                        positiveCounter = 0;
                    }
                    positiveCounter = positiveCounter +1;
                }
                else
                {
                    changeFlag = 0;
                    if (changeFlag0 != changeFlag)
                    {
                        negative_Peak_Locations.add(negativeCounter);
                        negativeCounter = 0;
                    }
                    negativeCounter = negativeCounter +1;
                }
            }
            positive_Peak_Locations.removeFirst();
            negative_Peak_Locations.removeFirst();
            int sizeP = positive_Peak_Locations.size();
            int sizeN = negative_Peak_Locations.size();
            int[] positive_array = new int[positive_Peak_Locations.size()];
            int[] negative_array = new int[negative_Peak_Locations.size()];
            for (int i = 0; i< sizeP; i++)
            {
                positive_array[i] = positive_Peak_Locations.removeFirst();
            }
            for (int i = 0; i< sizeN; i++)
            {
                negative_array[i] = negative_Peak_Locations.removeFirst();
            }

            int positive_peak_width = median(positive_array);
            int negative_peak_width = median(negative_array);

            System.out.println("Positive peak width = "+positive_peak_width);
            System.out.println("Negative peak width = "+negative_peak_width);

            frequency_channel[cols] = 1000.0/(positive_peak_width + negative_peak_width);
            System.out.println("frequency of sine = "+frequency_channel[cols]);
        }

        return frequency_channel;
    }
    /**
     * Find median
     */
    private static int median(int[] list)
    {
        // TODO Auto-generated method stub
        Arrays.sort(list);
        final int len = list.length;
        int median =0;
        if (len %2 == 1){
            median = list[len/2];
        }
        else {
            median = (list[len/2] + list[len/2 -1])/2;
        }
        return median;

    }
}

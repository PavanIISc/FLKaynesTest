package com.sattva.flkaynestest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{


    public Button btnInternalTest, btnExternalTest, btnPatientTest, btnSave;
    TextView tvCH1, tvCH2, tvCH3, tvCH4;
    EditText fileInputName;
    //public Handler handler1;

    Intent startSocketServiceIntent;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btnInternalTest = (Button)findViewById(R.id.btnStartInternalTest);
        btnExternalTest = (Button)findViewById(R.id.btnStartExternalTest);
        btnPatientTest = (Button)findViewById(R.id.btnStartPatientTest);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnInternalTest.setEnabled(false);
        btnExternalTest.setEnabled(false);
        btnPatientTest.setEnabled(false);


        tvCH1 = (TextView)findViewById(R.id.tvChannelValueOne);
        tvCH2 = (TextView)findViewById(R.id.tvChannelValueTwo);
        tvCH3 = (TextView)findViewById(R.id.tvChannelValueThree);
        tvCH4 = (TextView)findViewById(R.id.tvChannelValueFour);

        fileInputName = (EditText) findViewById(R.id.etFileName);
        context = this;


        start_server();
       //new listenForFlagCheck().execute();


//        handler1 = new Handler() {
//            @Override
//            public void handleMessage(android.os.Message msg) {
//                if (ApplicationUtils.enableButtons == 1) {
//                    btnInternalTest.setEnabled(true);
//                    btnExternalTest.setEnabled(true);
//                    //btnPatientTest.setEnabled(false);
//                }
//            }
//        };

//        if(ApplicationUtils.readyForTest == 1){
//            Toast.makeText(getApplicationContext(), "Ready for Test", Toast.LENGTH_LONG).show();
//        }

        btnPatientTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ApplicationUtils.outComFlag = ApplicationUtils.START_PATIENT_TEST;
                ApplicationUtils.patientTestFlag =1 ;

            }
        });


        btnInternalTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplicationUtils.outComFlag = ApplicationUtils.START_INTERNAL_TEST;
                ApplicationUtils.lowerBoundary = ApplicationUtils.internalLowerBoundary;
                ApplicationUtils.upperBoundary = ApplicationUtils.internalUpperBoundary;
                ApplicationUtils.functionOffset = ApplicationUtils.internalOffset;

            }
        });



        btnExternalTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplicationUtils.outComFlag = ApplicationUtils.START_EXTERNAL_TEST;
                ApplicationUtils.lowerBoundary = ApplicationUtils.externalLowerBoundary;
                ApplicationUtils.upperBoundary = ApplicationUtils.externalUpperBoundary;
                ApplicationUtils.functionOffset = ApplicationUtils.externalOffset;

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                ApplicationUtils.fileTextInput = fileInputName.getText();
                //Toast.makeText(getApplicationContext(), " FileSaved as : " + ApplicationUtils.fileTextInput, Toast.LENGTH_LONG).show();
            }
        });

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                displayData(intent.getDoubleArrayExtra("double_out"));

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("to_display_data"));

        BroadcastReceiver mButtonReset = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ApplicationUtils.enableButtons == 1) {
                    btnInternalTest.setEnabled(true);
                    btnExternalTest.setEnabled(true);
                    btnPatientTest.setEnabled(true);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mButtonReset, new IntentFilter("reset_Buttons"));


    }


    public void start_server()
    {

        startSocketServiceIntent = new Intent(MainActivity.this, SocketIntentService.class);
        startService(startSocketServiceIntent);
    }


//    class listenForFlagCheck extends AsyncTask<Object, Object, Integer>
//    {
//
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Shows Progress Bar Dialog and then call doInBackground method
//        }
//
//        @Override
//        protected Integer doInBackground(Object... voids) {
//
//            while (true)
//            {
//                if(ApplicationUtils.readyForTest == 1)
//                {
//                    Log.e("MainActivity", "readyForTest reset");
//                    return 0;
//                }
//            }
//
//
//        }
//
//
//        protected void onPostExecute(int i) {
//
//            Toast.makeText(getApplicationContext(),"Please press which test" + i, Toast.LENGTH_LONG).show();
//            Log.e("MainActivity", "In OnPostExecute");
//
//        }
//
//    }

    public void displayData(double[] inputValues)
    {


        if(ApplicationUtils.lowerBoundary <= inputValues[0] & inputValues[0] <= ApplicationUtils.upperBoundary)
        {
            tvCH1.setBackgroundColor(getResources().getColor(R.color.colorRightValue));
        }
        if(ApplicationUtils.lowerBoundary <= inputValues[1] & inputValues[1] <= ApplicationUtils.upperBoundary)
        {
            tvCH2.setBackgroundColor(getResources().getColor(R.color.colorRightValue));
        }
        if(ApplicationUtils.lowerBoundary <= inputValues[2] & inputValues[2] <= ApplicationUtils.upperBoundary)
        {
            tvCH3.setBackgroundColor(getResources().getColor(R.color.colorRightValue));
        }
        if(ApplicationUtils.lowerBoundary<= inputValues[3] & inputValues[3] <= ApplicationUtils.upperBoundary)
        {
            tvCH4.setBackgroundColor(getResources().getColor(R.color.colorRightValue));
        }

        tvCH1.setText("" + inputValues[0]);
        tvCH2.setText("" + inputValues[1]);
        tvCH3.setText("" + inputValues[2]);
        tvCH4.setText("" + inputValues[3]);



    }


}




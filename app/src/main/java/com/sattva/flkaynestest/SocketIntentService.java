package com.sattva.flkaynestest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class SocketIntentService extends IntentService {


    private ServerSocket serverSocket;
    Thread serverThread = null;
    public static final int SERVERPORT = 8080;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH-mm-ss");
    String strDate = sdf.format(c.getTime());
    String fileName = "sattva-" ; // + strDate;
    String[] tempString;
    String tempInput;
    String checkOK = null;
    char tempOkBuf[];
    int originalCheck = 0;
    Context context;
    private Handler handler;
    String fileMName; // = ApplicationUtils.fileTextInput.toString();
   Button btnExternalTest;
    Intent resetButtonIntent = new Intent("reset_Buttons");
    public SocketIntentService() {
        super("SocketIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {


        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
       // fileMName = ApplicationUtils.fileTextInput.toString();

    }


    class ServerThread implements Runnable {



        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.e("ServerThread", "in Server thread");


            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);

                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;
        private PrintWriter output;


        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {
                // start reading from socket input stream
                //Toast.makeText(getApplicationContext(), "Ready for Test", Toast.LENGTH_LONG).show();
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.output = new PrintWriter(this.clientSocket.getOutputStream());
                Log.e("SocketIntentService", "defined output stream");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            ApplicationUtils.startMS = System.currentTimeMillis();
            Log.d("SocketIntentService", "CommunicationThread started");
            ApplicationUtils.enableButtons = 1;
            //while (!Thread.currentThread().isInterrupted()) {
                while (true) {
                try {
                    //Log.e("SocketIntentService", "before tempOKcheck");

                    if(input.ready() && originalCheck == 0)
                    {

                        Log.e("SocketIntentService", "inside tempOKcheck");
                        tempInput = input.readLine();
                        Log.e("SocketIntentService", tempInput);
                        if(tempInput.equals("+OK+"))
                        {
                            //ApplicationUtils.readyForTest = 1;

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // if(ApplicationUtils.readyForTest == 1)
                                    Toast.makeText(getApplicationContext(), "Ready for test..", Toast.LENGTH_SHORT).show();
                                    //fileMName = ApplicationUtils.fileTextInput.toString();
                                    resetButtonIntent.putExtra("reset_Buttons", ApplicationUtils.enableButtons);

                                    LocalBroadcastManager.getInstance(SocketIntentService.this).sendBroadcast(resetButtonIntent);

                                    
                                }
                            });
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Ready for test", Toast.LENGTH_LONG).show();
//                                }
//                            });
                            //Log.e("SocketIntentService", "Ready for test");

                            if(ApplicationUtils.outComFlag == ApplicationUtils.START_EXTERNAL_TEST)
                            {
                                output.print("+b+");
                                output.flush();
                                originalCheck = 1;
                            }

                        }
                        else
                        {
                            Log.e("SocketIntentService", "bad input = " + tempInput);
                        }


                    }
                    else
                    {
                       // Log.e("SocketIntentService", "read() returns false");
                    }


                    if(input.ready() == true  && ApplicationUtils.outComFlag == ApplicationUtils.DONT_COMMUNICATE)
                    {
                        tempInput = input.readLine();
                        Log.e("SocketIntentService", tempInput);
                        LogData(tempInput + "\n");
                        if(ApplicationUtils.patientTestFlag != 1)
                        {
                            pushData();
                        }

                    }
                    else
                    {
                        sendCommand();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendCommand()
        {



            if(ApplicationUtils.outComFlag == ApplicationUtils.DONT_COMMUNICATE)
            {

            }
            else if(ApplicationUtils.outComFlag == ApplicationUtils.STOP_STREAM)
            {
                output.print("+s+");
                ApplicationUtils.outComFlag = ApplicationUtils.DONT_COMMUNICATE;
            }
            else if(ApplicationUtils.outComFlag == ApplicationUtils.START_INTERNAL_TEST)
            {

                Log.e("SocketIntentService", "sending +n+");
                output.print("+n+");
                output.flush();
                Log.e("SocketIntentService", "sent +n+");


                ApplicationUtils.outComFlag = ApplicationUtils.DONT_COMMUNICATE;
            }
            else if(ApplicationUtils.outComFlag == ApplicationUtils.START_EXTERNAL_TEST)
            {
                Log.e("SocketIntentService", "sending +z+");
                output.print("+z+");
                output.flush();
                ApplicationUtils.outComFlag = ApplicationUtils.DONT_COMMUNICATE;
            }
            else if(ApplicationUtils.outComFlag == ApplicationUtils.START_PATIENT_TEST)
            {
                Log.e("SocketIntentService", "sending +b+");
                output.print("+b+");
                output.flush();
                ApplicationUtils.outComFlag = ApplicationUtils.DONT_COMMUNICATE;
            }
        }

        public void pushData()
        {

            tempString = tempInput.split("\\+");
            ApplicationUtils.dynamicDataStore.addAll(Arrays.asList(tempString).subList(1, tempString.length));
            callBufferFull();

        }


        public void addStringToQue()
        {
            for(int temp_count = 1; temp_count < tempString.length; temp_count++)
            {
                ApplicationUtils.dynamicDataStore.add(tempString[temp_count]);

            }

        }




        public void callBufferFull()
        {
            if(ApplicationUtils.dynamicDataStore.size() >=  ApplicationUtils.bufferLength && ApplicationUtils.conversion_flag == ApplicationUtils.IDLE)
            {


                Log.e("SocketIntentService", "15000 samples read at: " + (System.currentTimeMillis() - ApplicationUtils.startMS));
                Intent startConvertingIntent = new Intent(SocketIntentService.this, ConvertIntentService.class);
                startService(startConvertingIntent);
                ApplicationUtils.conversion_flag = ApplicationUtils.PROCESSING;

            }

        }


        public void LogData(String input)
        {


            try {

//                context = getApplicationContext();
//                MainActivity a=(MainActivity)context;
//                EditText editText = (EditText)a.findViewById(R.id.etFileName);
//                ApplicationUtils.fileTextInput = editText.getText();
                File root = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/sattva");
                File ActivityLog = new File(Environment
                        .getExternalStorageDirectory().getPath() + "/sattva",
                        fileName + ApplicationUtils.fileTextInput.toString() + strDate + ".txt");
                   // Toast.makeText(getApplicationContext(), "Fine name: " + ActivityLog, Toast.LENGTH_LONG ).show();

                if (root.isDirectory()) {
                    // Log.d("root exists","root exists");
                    if (ActivityLog.exists()) {

                        FileWriter outFile = new FileWriter(ActivityLog, true);
                        // Log.d("writing","writing " +
                        // ActivityLog.getAbsolutePath());
                        PrintWriter out = new PrintWriter(outFile);
                        out.print(input);
                        out.flush();
                        out.close();
                    } else {
                        // Log.d("writing2","writing2");
                        if (ActivityLog.createNewFile()) {
                            FileWriter outFile = new FileWriter(ActivityLog, true);
                            PrintWriter out = new PrintWriter(outFile);
                            out.print(input);
                            out.flush();
                            out.close();
                        }
                    }
                } else {
                    if (root.mkdir()) {
                        if (ActivityLog.createNewFile()) {

                            FileWriter outFile = new FileWriter(ActivityLog, true);

                            PrintWriter out = new PrintWriter(outFile);
                            out.println(input);
                            out.flush();
                            // Log.d("writing3","writing3");
                            out.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
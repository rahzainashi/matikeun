package com.example.autosleep;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private NumberPicker hourPicker,minutePicker,secondPicker;
    private Button applyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyButton = findViewById(R.id.apply_button);

        pickerInit();

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
    }

    private void lockScreen() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            DevicePolicyManager policy = (DevicePolicyManager)
                    getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException ex) {
                Toast.makeText(
                        this,
                        "You must enable this app as a device administrator\n\n" +
                                "Please enable it and press back button to return here.",
                        Toast.LENGTH_LONG).show();
                ComponentName admin = new ComponentName(this, AdminReceiver.class);
                Intent intent = new Intent(
                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                startActivity(intent);
            }
        }
    }

    private void pickerInit(){

        hourPicker = findViewById(R.id.hour_picker);
        minutePicker = findViewById(R.id.minute_picker);
        secondPicker = findViewById(R.id.second_picker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        minutePicker.setValue(0);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(60);
        secondPicker.setValue(0);


    }

    public void initDialog() {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.confirm_dialog, null);
            final Button confirm = mView.findViewById(R.id.confirm_button);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

            dialog.show();

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                     dialog.dismiss();
                    startSleep();
                }
            });


    }

    private void startSleep(){

                lockScreen();

                AsyncTask.execute(new Runnable() {

                    @Override
                    public void run() {
                        //TODO your background code

                        BroadcastReceiver mybroadcast = new BroadcastReceiver() {
                            long startTime = System.currentTimeMillis();
                            long duration = hourPicker.getValue()*3600000 + minutePicker.getValue()*60000 + secondPicker.getValue()*1000;

                            //When Event is published, onReceive method is called
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Log.i("[BroadcastReceiver]", "MyReceiver");

                                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                                    if(System.currentTimeMillis() - startTime< duration) {
                                        Log.i("[BroadcastReceiver]", "Screen ON");
                                        lockScreen();


                                    }
                                    else{
                                        Log.i("[BroadcastReceiver]", "Screen ON");
                                    }
                                }
                            }


                        };

                        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));



                    }




                });

    }

}

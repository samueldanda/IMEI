package com.example.imei;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static boolean readPhoneStatePermissionGranted = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if the read phone state permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            readPhoneStatePermissionGranted = false;
            Log.i(TAG, "Read phone state permission granted: " + false);

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            // Permission is not granted
            readPhoneStatePermissionGranted = true;
            Log.w(TAG, "Read phone state permission granted: " + true);
        }

        MaterialButton displayIMEIButton = findViewById(R.id.displayIMEI);
        displayIMEIButton.setOnClickListener(view -> {
            Log.i(TAG, "Display IMEI button is clicked");
            Log.w(TAG, "Read phone state permission granted: " + readPhoneStatePermissionGranted);
            if (readPhoneStatePermissionGranted) {
                // Permission granted, proceed to get IMEI
                displayIMEINumber();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }

            displaySerialNumber();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to get IMEI
                displayIMEINumber();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displaySerialNumber() {
        try {
            String serialNumber = "Serial Number: " + Build.getSerial();
            Log.d("SerialNumber", "Device Serial Number: " + serialNumber);

            TextView textView = findViewById(R.id.serialNumberTextView);
            textView.setText(serialNumber);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

            TextView textView = findViewById(R.id.serialNumberTextView);
            textView.setText("Permission denied");
        }
    }

    private void displayIMEINumber() {
        Log.i(TAG, "displayIMEINumber function is opened");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                int phoneCount = telephonyManager.getPhoneCount();
                Log.i(TAG, "phoneCount: " + phoneCount);

                for (int i = 0; i < phoneCount; i++) {
                    String imei = telephonyManager.getImei(i);
                    if (imei != null) {
                        switch (i) {
                            case 0:
                                TextView textView1 = findViewById(R.id.IMEItextView1);
                                textView1.setText(imei);
                                break;
                            case 1:
                                TextView textView2 = findViewById(R.id.IMEItextView2);
                                textView2.setText(imei);
                                break;
                            // Add more cases if there are more TextViews
                        }
                    } else {
                        // Handle the case when IMEI is null
                    }
                }
            } else {
                Log.w(TAG, "TelephonyManager is null");
                Toast.makeText(this, "TelephonyManager is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);

            TextView textView1 = findViewById(R.id.IMEItextView1);
            textView1.setText("Permission denied");
            TextView textView2 = findViewById(R.id.IMEItextView2);
            textView2.setText("Permission denied");

            if (e.getClass().getSimpleName().equals("SecurityException")) {
                Log.e(TAG, "The user 10221 does not meet the requirements to access device identifiers");
                Toast.makeText(this, "The user 10221 does not meet the requirements to access device identifiers", Toast.LENGTH_LONG).show();
            }
        }
    }
}
package com.rakibdevhub.textbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView logTextView;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        logTextView = findViewById(R.id.logTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Set start button click listener
        startButton.setOnClickListener(v -> startSmsGatewayService());

        // Set stop button click listener
        stopButton.setOnClickListener(v -> stopSmsGatewayService());
    }

    private void startSmsGatewayService() {
        Intent serviceIntent = new Intent(this, SmsGatewayService.class);
        startService(serviceIntent);
        logTextView.setText(getString(R.string.sms_service_started));
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopSmsGatewayService() {
        Intent serviceIntent = new Intent(this, SmsGatewayService.class);
        stopService(serviceIntent);
        logTextView.setText(getString(R.string.sms_service_stopped));
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}

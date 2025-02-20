package com.rakibdevhub.textbridge;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity {

    private TextView logTextView;
    private Button startButton, stopButton, copyButton;
    private TextView endpointTextView;
    private String endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request SMS Permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        // Initialize UI elements
        logTextView = findViewById(R.id.logTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        copyButton = findViewById(R.id.copyButton);
        endpointTextView = findViewById(R.id.endpointTextView);

        // Get local IP and set endpoint
        endpoint = "http://" + getLocalIpAddress() + ":4006";
        endpointTextView.setText(endpoint);

        // Set start button click listener
        startButton.setOnClickListener(v -> startSmsGatewayService());

        // Set stop button click listener
        stopButton.setOnClickListener(v -> stopSmsGatewayService());

        // Set copy button click listener
        copyButton.setOnClickListener(v -> copyToClipboard());
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

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "Unknown IP";
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Endpoint", endpoint);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

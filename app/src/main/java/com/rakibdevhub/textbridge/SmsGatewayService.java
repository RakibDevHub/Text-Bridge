package com.rakibdevhub.textbridge;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Map;

public class SmsGatewayService extends Service {
    private static final String TAG = "SmsGatewayService";
    private SmsServer smsServer;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        SmsGatewayService getService() {
            return SmsGatewayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        smsServer = new SmsServer(4006); // Listen on port 4006
        try {
            smsServer.start();
            Log.i(TAG, "Text Bridge SMS Gateway Server started on port 4006");
        } catch (IOException e) {
            Log.e(TAG, "Failed to start Text Bridge SMS server", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsServer != null) {
            smsServer.stop();
            Log.i(TAG, "Text Bridge SMS Gateway Server stopped");
        }
    }

    private static class SmsServer extends NanoHTTPD {
        public SmsServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            if (Method.POST.equals(session.getMethod())) {
                try {
                    session.parseBody(null);
                    Map<String, String> params = session.getParms();
                    String phoneNumber = params.get("phone");
                    String message = params.get("message");

                    if (phoneNumber != null && message != null) {
                        sendSMS(phoneNumber, message);
                        return newFixedLengthResponse(Response.Status.OK, "text/plain", "SMS Sent Successfully");
                    }
                } catch (IOException | ResponseException e) {
                    Log.e(TAG, "Error processing SMS request", e);
                }
            }
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid Request");
        }

        private void sendSMS(String phoneNumber, String message) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Log.i(TAG, "SMS sent Successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS", e);
            }
        }
    }
}

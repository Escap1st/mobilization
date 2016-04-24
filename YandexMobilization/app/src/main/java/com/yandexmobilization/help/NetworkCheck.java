package com.yandexmobilization.help;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkCheck {
    Context c;
    NetworkInfo NI;
    ConnectivityManager conMngr;
    boolean mAvailable = false;
    boolean	mConnected = false;
    boolean mWifi = false;
    boolean mNetwork = false;
    boolean internetIsOK = false;

    public NetworkCheck(Context c) {
        this.c = c;
    }

    public boolean check() {
        conMngr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMngr != null) {
            NI = conMngr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (NI != null) {
                mAvailable = NI.isAvailable();
                mConnected = NI.isConnected();
            }

            if (mAvailable & mConnected) {
                mNetwork = true;
            }

            mConnected = false;
            mAvailable = false;

            NI = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (NI != null) {
                mAvailable = NI.isAvailable();
                mConnected = NI.isConnected();
            }

            Thread t = new Thread() {
                public void run() {
                    Looper.prepare();
                    if (NI != null && NI.isConnected()) {
                        internetIsOK = false;
                        ping();
                    }
                    Looper.loop();
                }
            };
            t.start();

            try {
                t.join(1100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mAvailable & mConnected & internetIsOK) {
                mWifi = true;
            }
        }

        if (mWifi | mNetwork) {
            return true;
        } else {
            return false;
        }
    }

    private void ping() {
        for (int i = 0; i < 4; i++) {
            try {
                URL url = new URL("http://google.com/");

                final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(250);

                urlc.connect();

                if (urlc.getResponseCode() == 200) {
                    internetIsOK = true;
                    break;
                } else {
                    internetIsOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.naijaplanet.magosla.android.moviesplanet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Checks for the available of internet service
 * {@see @https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out}
 */
@SuppressWarnings("ALL")
class InternetCheck extends AsyncTask<Void,Void,Boolean> {

    private final Consumer mConsumer;
    public  interface Consumer { void accept(Boolean internet); }

    public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }

    @Override protected Boolean doInBackground(Void... voids) { try {
        String hostName = "8.8.8.8";
        int hostPort = 53;
        int timeout = 1500;

        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(hostName, hostPort), timeout);
        sock.close();
        return true;
    } catch (IOException e) {
        return false; } }

    @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }

    /**
     * If there is a network connection to the device
     * @param context the application context
     * @return boolean value of the state
     */
    public static boolean hasConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////////
// Usage

    //new InternetCheck(internet -> { /* do something with boolean response */ });
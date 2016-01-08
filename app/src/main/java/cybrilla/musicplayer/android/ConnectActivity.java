package cybrilla.musicplayer.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cybrilla.musicplayer.R;
import cybrilla.musicplayer.util.Constants;

public class ConnectActivity extends AppCompatActivity {
    String mServiceName;
    private NsdManager mNsdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        registerService();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private void registerService(){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(Constants.SERVICE_NAME);
        serviceInfo.setServiceType(Constants.SERVICE_TYPE);
        serviceInfo.setPort(Constants.PORT_NUMBER);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    @Override
    protected void onPause() {
        if (mNsdManager != null) {
            mNsdManager.unregisterService(mRegistrationListener);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNsdManager != null) {
            registerService();
        }

    }

    @Override
    protected void onDestroy() {
        if (mNsdManager != null) {
            mNsdManager.unregisterService(mRegistrationListener);
        }
        super.onDestroy();
    }


    NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e("Connect activity", "Failed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e("Connect activity", "Failed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                mServiceName = serviceInfo.getServiceName();
                Log.e("Connect activity", "Service name: "+mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.e("Connect activity", "Unregistering");
            }
        };
}

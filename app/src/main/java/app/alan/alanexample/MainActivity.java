package app.alan.alanexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alan.alansdk.Alan;
import com.alan.alansdk.BasicSdkListener;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.alanbase.ConnectionState;
import com.alan.alansdk.button.AlanButton;

public class MainActivity extends AppCompatActivity {

    private Alan sdk;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View initButton = findViewById(R.id.init_button);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_CODE);
                } else {
                    initAlanSDK();
                }
            }
        });

        View voiceBtn = findViewById(R.id.voice);
        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdk != null && sdk.isInited()) {
                    sdk.toggle();
                }
            }
        });

        View scriptMethodCallBtn = findViewById(R.id.callScript);
        scriptMethodCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sdk != null && sdk.isInited()) {
                    sdk.call("script::test", "{\"test\":1}", new ScriptMethodCallback() {
                        @Override
                        public void onResponse(String methodName, String response, String error) {
                            if (error != null && !error.isEmpty()) {
                                Log.i("AlanResponse", methodName + " failed with: " + error);
                            } else {
                                Log.i("AlanResponse", methodName + " response is: " + response);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAlanSDK();
        }
    }

    private void initAlanSDK() {
        Alan.enableLogging(true);
        sdk = Alan.getInstance();

        sdk.init("8e0b083e795c924d64635bba9c3571f42e956eca572e1d8b807a3e2338fdd0dc/stage");

        sdk.registerCallback(new MyCallback());

        //Link Alan button with sdk so it can listen to the dialog state and control voice interaction
        AlanButton alanButton = findViewById(R.id.alanBtn);
        alanButton.withConfig(sdk);
    }

    class MyCallback extends BasicSdkListener {
        @Override
        public void onConnectStateChanged(@NonNull ConnectionState connectState) {
            super.onConnectStateChanged(connectState);
            Log.i("AlanCallback", "Connection state changed -> " + connectState.name());
        }
    }
}

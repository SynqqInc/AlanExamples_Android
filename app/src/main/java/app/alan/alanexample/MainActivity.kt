package app.alan.alanexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alan.alansdk.Alan
import com.alan.alansdk.BasicSdkListener
import com.alan.alansdk.alanbase.ConnectionState
import com.alan.alansdk.button.AlanButton

class MainActivity : AppCompatActivity() {

    private var alanButton: AlanButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val initButton = findViewById<View>(R.id.init_button)
        initButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSION_REQUEST_CODE)
            } else {
                //       initAlanSDK();
            }
        }

        /**You should also listen to the
         * [ to update visual state according to the dialog state][com.alan.alansdk.AlanCallback.onDialogStateChanged]
         */
        //        View voiceBtn = findViewById(R.id.voice);
        //        voiceBtn.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if (sdk != null && sdk.isInited()) {
        //                    sdk.toggle();
        //                }
        //            }
        //        });

        val scriptMethodCallBtn = findViewById<View>(R.id.callScript)
        scriptMethodCallBtn.setOnClickListener {
            alanButton!!.sdk.call("script::test", "{\"test\":1}") { methodName, response, error ->
                if (error != null && !error.isEmpty()) {
                    Log.i("AlanResponse", "$methodName failed with: $error")
                } else {
                    Log.i("AlanResponse", "$methodName response is: $response")
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAlanSDK()
        }
    }

    private fun initAlanSDK() {
        Alan.enableLogging(true)
        //        sdk = Alan.getInstance();
        //
        //        Insert your project key here from "Embed code" button on the tutor.alan.app page
        //        sdk.init("8e0b083e795c924d64635bba9c3571f42e956eca572e1d8b807a3e2338fdd0dc/stage");
        //
        //        sdk.registerCallback(new MyCallback());
        //
        //        Link Alan button with sdk so it can listen to the dialog state and control voice interaction
        alanButton = findViewById(R.id.alanBtn)
        alanButton!!.initSDK("8e0b083e795c924d64635bba9c3571f42e956eca572e1d8b807a3e2338fdd0dc/stage")
        //        alanButton.withConfig(sdk);
    }

    internal inner class MyCallback : BasicSdkListener() {
        override fun onConnectStateChanged(connectState: ConnectionState) {
            super.onConnectStateChanged(connectState)
            Log.i("AlanCallback", "Connection state changed -> " + connectState.name)
        }
    }

    companion object {

        private val PERMISSION_REQUEST_CODE = 101
    }
}

package com.loits.mobile.checkws;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnInvokeService;

    private static String TAG = "=== MainActivity ===";

    VolleyRESTClient rClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInvokeService = (Button)findViewById(R.id.btnInvokeService);
        btnInvokeService.setOnClickListener(this);

        rClient = new VolleyRESTClient(this);



        /*rClient.authenticateUser("dumindaw", "1*ninayake", new LoginCallback() {
            @Override
            public void onSuccess(String result) {

                String authToken = null;
                String refreshToken = null;
                int expireTime = 0;

                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(result);
                    authToken = jObj.getString("access_token");
                    refreshToken = jObj.getString("refresh_token");
                    expireTime = jObj.getInt("expires_in");

                    rClient.downloadPendingJobList("dumindaw", authToken, new ServiceCallback() {
                        @Override
                        public void onSuccess(String result) {

                            JSONArray jObj = null;
                            try {
                                jObj = new JSONArray(result);
                            }catch (JSONException e){
                                Log.e(TAG, e.toString());
                            }

                        }

                        @Override
                        public void onError(VolleyError result) {
                            Log.e(TAG, result.toString());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError result) {
                Log.e(TAG, result.toString());
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnInvokeService){

            rClient.sync("16edc12be4c51284cc47f9fedbeccb35", new JSONArrayServiceCallback() {
                @Override
                public void onSuccess(JSONArray result) {
                    JSONArray arr = result;
                    Log.v("=============", result.toString());
                }

                @Override
                public void onError(VolleyError result) {

                    int code = 0;
                    long sec = 0;
                    if(result.networkResponse != null){
                        code = result.networkResponse.statusCode;

                        sec = result.networkResponse.networkTimeMs;
                        //result = error;
                    }
                    VolleyError err = result;
                    Log.w("=============", sec + " - "+code + " - " + result.toString());

                    Toast.makeText(getApplicationContext(),""+ result.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

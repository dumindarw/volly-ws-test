package com.loits.mobile.checkws;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.loits.mobile.checkws.Constants.DEV_IDS_LOGIN_PATH;
import static com.loits.mobile.checkws.Constants.DEV_IDS_OAUTH2_CLIENT_ID;
import static com.loits.mobile.checkws.Constants.DEV_IDS_OAUTH2_CLIENT_SECRET;
import static com.loits.mobile.checkws.Constants.DEV_SERVICES_BASE_URL;
import static com.loits.mobile.checkws.Constants.SERVICE_PENDING;
import static com.loits.mobile.checkws.Constants.SOCKET_TIMEOUT_MS;
import static com.loits.mobile.checkws.Constants.SYNC;

/**
 * Created by DumindaW on 24/11/2016.
 */

public class VolleyRESTClient {

    private Context mCtx;
    RequestQueue mLoginRequestQueueHTTPS;
    RequestQueue mServiceRequestQueueHTTPS;

    RequestQueue mServiceRequestQueueHTTP;

    public VolleyRESTClient(Context ctx) {
        mCtx = ctx;
        mLoginRequestQueueHTTPS = Volley.newRequestQueue(mCtx, loginHurlStack);
        mServiceRequestQueueHTTPS = Volley.newRequestQueue(mCtx, serviceHurlStack);
        mServiceRequestQueueHTTP = Volley.newRequestQueue(mCtx);
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return /*hv.verify("localhost", session)*/true;
            }
        };
    }

    private SSLContext newLoginSslSocketFactory(Context ctx) {
        try {

            TrustManagerFactory originalTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            originalTrustManagerFactory.init((KeyStore) null);

            KeyStore trusted = KeyStore.getInstance(KeyStore.getDefaultType());

            InputStream in = ctx.getResources().openRawResource(R.raw.clienttrustoreidsdevnew);

            try {

                trusted.load(in, "wso2carbon".toCharArray());
            } catch (Exception e) {

                e.printStackTrace();

                return null;
            } finally {
                in.close();
            }

            originalTrustManagerFactory.init(trusted);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(/*keyManagerFactory.getKeyManagers()*/null, originalTrustManagerFactory.getTrustManagers()/*trustAllCerts*//*new TrustManager[]{tm}*/, /*new java.security.SecureRandom()*/null);

            return sc;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private SSLContext newServiceSslSocketFactory(Context ctx) {
        try {

            TrustManagerFactory originalTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            originalTrustManagerFactory.init((KeyStore) null);

            KeyStore trusted = KeyStore.getInstance(KeyStore.getDefaultType());

            InputStream in = ctx.getResources().openRawResource(R.raw.clienttruststoreesblive);
            //InputStream in = ctx.getResources().openRawResource(R.raw.clienttrustoreesbdevnew);
            //InputStream in = ctx.getResources().openRawResource(R.raw.mcom);

            try {

                trusted.load(in, "Lolc1231145".toCharArray());
                //trusted.load(in, "wso2carbon".toCharArray());
                //trusted.load(in, "mcom".toCharArray());
            } catch (Exception e) {

                e.printStackTrace();

                return null;
            } finally {
                in.close();
            }

            originalTrustManagerFactory.init(trusted);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(/*keyManagerFactory.getKeyManagers()*/null, originalTrustManagerFactory.getTrustManagers()/*trustAllCerts*//*new TrustManager[]{tm}*/, /*new java.security.SecureRandom()*/null);

            return sc;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    HurlStack loginHurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(newLoginSslSocketFactory(mCtx).getSocketFactory());
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };

    HurlStack serviceHurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(newServiceSslSocketFactory(mCtx).getSocketFactory());
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };

    public void authenticateUser(final String user, final String pass, final LoginCallback loginCallback) {

        StringRequest loginStringRequest = new StringRequest(Request.Method.POST, DEV_IDS_LOGIN_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loginCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("volley", "Error: " + error.toString());
                loginCallback.onError(error);
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("username", user);
                params.put("password", pass);
                params.put("scope", "ipayadmin");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = DEV_IDS_OAUTH2_CLIENT_ID + ":" + DEV_IDS_OAUTH2_CLIENT_SECRET;

                String auth = "Basic " /*+ IDS_AUTH_HEADER*/
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        Request<String> requestQueue = mLoginRequestQueueHTTPS.add(loginStringRequest);
    }

    public void updateTokens(final String refreshToken, final LoginCallback loginCallback) {

        StringRequest refreshTokenStringRequest = new StringRequest(Request.Method.POST, DEV_IDS_LOGIN_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loginCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("volley", "Error: " + error.toString());
                loginCallback.onError(error);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "refresh_token");
                params.put("scope", "ipayadmin");
                params.put("refresh_token", refreshToken);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = DEV_IDS_OAUTH2_CLIENT_ID + ":" + DEV_IDS_OAUTH2_CLIENT_SECRET;

                String auth = "Basic " /*+ IDS_AUTH_HEADER*/
                        + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        Request<String> requestQueue = mLoginRequestQueueHTTPS.add(refreshTokenStringRequest);
    }

    public void downloadPendingJobList( final String assessorId, final String token, final ServiceCallback serviceCallback) {

        StringRequest downloadPendingJobListRequest = new StringRequest(Request.Method.POST, DEV_SERVICES_BASE_URL + SERVICE_PENDING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        serviceCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("volley", "Error: " + error.toString());
                serviceCallback.onError(error);
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("assessorId", assessorId);
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Authorization", "Bearer " + String.valueOf(token));
                headers.put("Accept-Encoding", "application/json");
                headers.put("Connection", "Keep-Alive");
                return headers;
            }

        };

        downloadPendingJobListRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Request<String> requestQueue = mServiceRequestQueueHTTPS.add(downloadPendingJobListRequest);
    }

    public void sync(final String token, final JSONArrayServiceCallback serviceCallback) {
        JSONArray jArrayInput = null;

        try {
            jArrayInput = new JSONArray(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, SYNC, jArrayInput , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                serviceCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serviceCallback.onError(error);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Authorization", "Bearer " + String.valueOf(token));
                //headers.put("Accept-Encoding", "");
                //headers.put("Connection", "close");
                return headers;
            }

            @Override
            public Priority getPriority() {
                Request.Priority mPriority = Priority.HIGH;
                return mPriority;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Request<JSONArray> requestQueue = mServiceRequestQueueHTTPS.add(request);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = mCtx.getAssets().open("data.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    /*public void getMerchantID(final String code, final String action, final String token, final LoginCallback loginCallback) {

        String url = KEY_MERCHANT_URL + "?action=" + action + "&code=" + code;

        StringRequest merchantIDRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loginCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("volley", "Error: " + error.toString());
                loginCallback.onError(error);
                error.printStackTrace();
            }
        }) {

            *//*@Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }*//*

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Authorization", "Bearer " + String.valueOf(token));
                return headers;
            }

        };

        merchantIDRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Request<String> requestQueue = mRequestQueueHTTPS.add(merchantIDRequest);
    }*/

    static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    } };
}



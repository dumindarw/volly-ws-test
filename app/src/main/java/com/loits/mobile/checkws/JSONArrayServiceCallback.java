package com.loits.mobile.checkws;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by DumindaW on 24/11/2016.
 */

public interface JSONArrayServiceCallback {

    void onSuccess(JSONArray result);
    void onError(VolleyError result);
}

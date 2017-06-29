package com.loits.mobile.checkws;

import com.android.volley.VolleyError;

/**
 * Created by DumindaW on 24/11/2016.
 */

public interface LoginCallback {

    void onSuccess(String result);
    void onError(VolleyError result);
}

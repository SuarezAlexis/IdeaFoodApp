package com.jainjo.ideafood.login;

import android.content.res.Resources;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.jainjo.ideafood.R;

import java.util.HashMap;
import java.util.Map;

public class LoginStringRequest extends StringRequest {

    private final Map<String,String> params;
    private final String sessionCookie;

    public LoginStringRequest(String url, Map<String,String> params, String sessionCookie, Response.Listener listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.params = params;
        this.sessionCookie = sessionCookie;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(java.util.Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        if (sessionCookie.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(sessionCookie);
            if (headers.containsKey("Cookie")) {
                builder.append("; ");
                builder.append(headers.get("Cookie"));
            }
            headers.put("Cookie", builder.toString());
        }
        return headers;
    }
}

package com.bnvlab.concienciadeabundancia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.clases.Icb;
import com.bnvlab.concienciadeabundancia.clases.MySingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class PayActivity extends AppCompatActivity {
    SharedPreferences prefs;
//    TextView textView;
//    private static String PUBLIC_KEY = "APP_USR-dd544c46-10ce-4901-9804-1ae3f99aac2b";
    final static String PUBLIC_KEY = "TEST-537e1249-251e-4006-8417-7df99e3ac0cb";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        prefs = getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                MODE_PRIVATE);
        //textView = (TextView) findViewById(R.id.textView1);
        submitMP();
    }
    ///////////////////////////////////////////   MERCADOPAGO
    public void submitMP() {
        sendJSON(new Icb() {
            @Override
            public void callBack(String string) {
                try {
                    //textView.setText(string);
                    CheckoutPreference cp = new Gson().fromJson(string, CheckoutPreference.class);
                    startMercadoPagoCheckout(cp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {
        DecorationPreference decorationPreference = new DecorationPreference.Builder()
                .setBaseColor("#cda76a")
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(PUBLIC_KEY)
                .setCheckoutPreference(checkoutPreference)
                .setDecorationPreference(decorationPreference)
                .startForPayment();
    }

    private void sendJSON(final Icb cb){
        String url = "http://c0810082.ferozo.com/cda/mp/preferences/";

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cb.callBack(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name", prefs.getString(References.SHARED_PREFERENCES_USER_FULLNAME,""));
                params.put("first_name", prefs.getString(References.SHARED_PREFERENCES_USER_FIRSTNAME,""));
                params.put("last_name", prefs.getString(References.SHARED_PREFERENCES_USER_LASTNAME,""));
                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                params.put("phone", prefs.getString(References.SHARED_PREFERENCES_USER_PHONE,""));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                //Done!
                Toast.makeText(this, payment.getStatus(), Toast.LENGTH_LONG).show();
                switch (payment.getStatus()){
                    case Payment.StatusCodes.STATUS_APPROVED:

                        break;
                    case Payment.StatusCodes.STATUS_PENDING:

                        break;
                    case Payment.StatusCodes.STATUS_IN_PROCESS:

                        break;
                    default:

                        break;
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }
}

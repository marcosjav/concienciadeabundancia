package com.bnvlab.concienciadeabundancia;

import android.support.v7.app.AppCompatActivity;

public class PayActivity extends AppCompatActivity {
    /*private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    private static final String CONFIG_CLIENT_ID = "AZbyr2Zwl5lOev_NO5Pb_wFwgF9--Q8jMwCWGT7VcmqcQfhN1308oZFgwjD9dYk28xxgKxKSTYkRVZEv";
    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

            // configuracion minima del ente
            .merchantName("CDA Internacional")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.cdaint.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.cdaint.com/legal"));

    PayPalPayment thingToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        thingToBuy = new PayPalPayment(new BigDecimal("120"), "USD",
                "Entrenamiento Conciencia de Ser", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent2 = new Intent(PayActivity.this,
                PaymentActivity.class);

        intent2.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent2, REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data
                    .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {

                    // informacion extra del pedido
                    Log.d("PAYMENTT", confirm.toJSONObject().toString(4));
                    Log.d("PAYMENTT", confirm.getPayment().toJSONObject()
                            .toString(4));

                    Toast.makeText(getApplicationContext(), "Orden procesada",
                            Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            System.out.println("El usuario canceló el pago");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }*/
}

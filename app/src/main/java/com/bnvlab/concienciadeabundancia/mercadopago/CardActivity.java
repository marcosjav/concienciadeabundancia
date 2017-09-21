package com.bnvlab.concienciadeabundancia.mercadopago;

import android.support.v7.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card2);
    }

    public void onCardStartButtonClicked(View view) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey("TEST-537e1249-251e-4006-8417-7df99e3ac0cb")
                .startCardVaultActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if(requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
                Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

                // Done! Send the data to your server

            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException exception = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                }
            }
        }
    }*/
}

package com.bnvlab.concienciadeabundancia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/*
NORMAS:
    -DESCRIPCIÓN DE MÉTODOS, SUS PARÁMETROS Y DEMÁS
    -COMENTARIOS
    -NOMBRES DE FUNCIONES Y VARIABLES AUTODESCRIPTIVOS - PREFERENTEMENTE EN INGLÉS
    -CADA CLASE DEBE TENER SU COMENTARIO DESCRIBIENDO EL PROPÓSITO
    -EN MÉTODOS COMPLICADOS, ANOTAR EL PSEUDOCÓDICO EN UN COMENTARIO
    -CREAR CÓDIGO LO MÁS GENÉRICO POSIBLE, PARA PODER REUTILIZARLO
    -CADA COMMIT DEBE IR ACOMPAÑADO DE UNA DESCRIPCIÓN DE LO QUE SE HIZO Y DE LO QUE SE SE VA A SEGUIR EN EL PRÓXIMO
    -LOS TEXTOS, COLORES Y DEMÁS EN SU RESPECTIVO XML
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ESTO ES SIMPLEMENTE PARA PROBAR EL LOGIN
        Button buttonTestLogin = (Button) findViewById(R.id.button_login_test);
        buttonTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.layout_login);
            }
        });

    }
}

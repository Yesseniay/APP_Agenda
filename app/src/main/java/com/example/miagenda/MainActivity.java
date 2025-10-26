package com.example.miagenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView card_contactos, card_agregar, card_notas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        card_contactos = findViewById(R.id.card_contactos);
        card_agregar = findViewById(R.id.card_agregar);
        card_notas = findViewById(R.id.card_notas);

        //  Abre el activity contacto
        card_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
                startActivity(intent);
            }
        });

        // Abre el activity agregar contacto
        card_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AgregarContactoActivity.class);
                startActivity(intent);
            }
        });

        //  Abre el activity de notas
        card_notas.setOnClickListener(v -> {
            Toast.makeText(this, "Notas", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, NotaActivity.class));
        });
    }
}

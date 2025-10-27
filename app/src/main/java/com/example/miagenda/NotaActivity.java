package com.example.miagenda;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotaActivity extends AppCompatActivity
        implements NotaCursorAdapter.OnActionListener {

    public static final String EXTRA_NOTA_ID = "nota_id";

    private EditText etBuscador;
    private AgendaManager manager;
    private NotaCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);


        manager = new AgendaManager(this);

        etBuscador = findViewById(R.id.Buscador);
        RecyclerView recyclerView = findViewById(R.id.Notas);
        FloatingActionButton fabAddNota = findViewById(R.id.btnAgregarNota);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor initialCursor = manager.buscarNotas("");
        adapter = new NotaCursorAdapter(initialCursor, this);
        recyclerView.setAdapter(adapter);

        // Lógica para busqueda
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                realizarBusqueda(s.toString());
            }
        });


        fabAddNota.setOnClickListener(v -> {
            startActivity(new Intent(NotaActivity.this, NotaescribirActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        realizarBusqueda(etBuscador.getText().toString());
    }

    private void realizarBusqueda(String texto) {
        Cursor nuevoCursor = manager.buscarNotas(texto);
        adapter.swapCursor(nuevoCursor);
    }


    @Override
    public void onEditClick(long id) {
        Intent intent = new Intent(NotaActivity.this, NotaescribirActivity.class);
        intent.putExtra(EXTRA_NOTA_ID, id);
        startActivity(intent);
    }


    @Override
    public void onDeleteClick(long id) {
        // Llama al metodo del manager para eliminar
        if (manager.eliminarNota(id) > 0) {
            Toast.makeText(this, "Nota eliminada.", Toast.LENGTH_SHORT).show();
            realizarBusqueda(etBuscador.getText().toString());
        } else {
            Toast.makeText(this, "Error al eliminar la nota.", Toast.LENGTH_SHORT).show();
        }
    }
}
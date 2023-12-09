package com.example.proyecto_el_economico_g6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyecto_el_economico_g6.Config.Categoria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrdenarProducto extends AppCompatActivity {

    public List<Categoria> listarProducto = new ArrayList<>();
    ArrayAdapter<Categoria> arrayAdapterCategoria;

    Button btnAtras, btnCarrito, btnUbicacion;
    ListView listViewProducto;

    Categoria categoriaSelected;
    String ubicacionSeleccionada = "";

    FirebaseFirestore mFirestore;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordenarproducto);

        btnAtras = findViewById(R.id.btnAtras);
        btnCarrito = findViewById(R.id.btnCarrito);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        listViewProducto = findViewById(R.id.listarProducto);
        listViewProducto.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Permitir selección múltiple

        btnAtras = findViewById(R.id.btnAtras);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrdenarProducto.this, InicioActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);

        mFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        listarProductos();

        listViewProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoriaSelected = (Categoria) parent.getItemAtPosition(position);
            }
        });

        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarUbicacion();
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCount = listViewProducto.getCount();
                SparseBooleanArray checkedItemPositions = listViewProducto.getCheckedItemPositions();

                for (int i = 0; i < itemCount; i++) {
                    if (checkedItemPositions.get(i)) {
                        // El elemento en la posición i está seleccionado, realiza la acción deseada
                        Categoria categoriaSeleccionada = listarProducto.get(i);
                        // Realiza la acción que desees con la categoría seleccionada
                    }
                }

                // También puedes realizar la acción deseada con la ubicación seleccionada
                // Aquí estamos simplemente imprimiendo la ubicación seleccionada con un Toast.
                Toast.makeText(OrdenarProducto.this, "Ubicación: " + ubicacionSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarProductos() {
        databaseReference.child("Categoria").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listarProducto.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Categoria c = objSnapshot.getValue(Categoria.class);
                    listarProducto.add(c);
                }

                arrayAdapterCategoria = new ArrayAdapter<>(OrdenarProducto.this, android.R.layout.simple_list_item_multiple_choice, listarProducto);
                listViewProducto.setAdapter(arrayAdapterCategoria);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });
    }

    private void seleccionarUbicacion() {
        // Implementa la lógica para seleccionar la ubicación según tus necesidades
        // Por ahora, estableceremos un valor fijo para propósitos de ejemplo.
        ubicacionSeleccionada = "Ubicación seleccionada";
    }
}

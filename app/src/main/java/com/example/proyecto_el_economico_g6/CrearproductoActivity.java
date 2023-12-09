package com.example.proyecto_el_economico_g6;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_el_economico_g6.Config.Categoria;
import com.example.proyecto_el_economico_g6.Config.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CrearproductoActivity extends AppCompatActivity {

    public List<Categoria> listarProducto = new ArrayList<>();
    ArrayAdapter<Categoria> arrayAdapterProductos;

    // Otros elementos de la interfaz
    EditText txtCPNombre, txtCPCantidad, txtCPPrecio;
    Spinner cmbCPCategoria;
    Button btnCPGuardar, btnCPAtras, btnCPGestionarInv;

    Categoria productoSeleccionado;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearproducto);

        txtCPNombre = findViewById(R.id.txtCPNombre);
        txtCPCantidad = findViewById(R.id.txtCPCantidad);
        txtCPPrecio = findViewById(R.id.txtCPPrecio);
        cmbCPCategoria = findViewById(R.id.cmbCPCategoria);
        btnCPGuardar = findViewById(R.id.btnCPGuardar);
        btnCPAtras = findViewById(R.id.btnCPAtras);
        btnCPGestionarInv = findViewById(R.id.btnCPGestionarInv);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Llenar el Spinner con la lista de productos
        arrayAdapterProductos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listarProducto);
        arrayAdapterProductos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbCPCategoria.setAdapter(arrayAdapterProductos);

        // Manejar la selección del Spinner
        cmbCPCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Actualizar el producto seleccionado
                productoSeleccionado = (Categoria) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No se realiza ninguna acción cuando no se selecciona nada
            }
        });


        btnCPAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrearproductoActivity.this, InicioActivity.class));
            }
        });

        btnCPGestionarInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrearproductoActivity.this, AgregarproductoActivity.class));
            }
        });

        // Botón Guardar
        btnCPGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de entrada
                String nombre = txtCPNombre.getText().toString().trim();
                String cantidad = txtCPCantidad.getText().toString().trim();
                String precio = txtCPPrecio.getText().toString().trim();

                // Verificar campos vacíos
                if (nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty() || productoSeleccionado == null) {
                    Toast.makeText(CrearproductoActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear una instancia de Producto con los datos ingresados
                Producto nuevoProducto = new Producto(nombre, cantidad, precio, productoSeleccionado.getId());

                // Obtener una referencia al nodo "productos" en la base de datos
                DatabaseReference productosReference = databaseReference.child("productos");

                // Generar una nueva clave única para el nuevo producto
                String nuevoProductoKey = productosReference.push().getKey();

                // Guardar el nuevo producto en la base de datos
                productosReference.child(nuevoProductoKey).setValue(nuevoProducto);

                // Opcional: Mostrar un mensaje o realizar alguna acción después de guardar
                Toast.makeText(CrearproductoActivity.this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
            }
        });

        listarProductos();
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

                // Notificar al adaptador que los datos han cambiado
                arrayAdapterProductos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });
    }


}

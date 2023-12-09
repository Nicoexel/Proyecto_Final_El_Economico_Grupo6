package com.example.proyecto_el_economico_g6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.proyecto_el_economico_g6.Config.Categoria;
import com.example.proyecto_el_economico_g6.Config.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AgregarproductoActivity extends AppCompatActivity {

    private EditText txtAPNombre, txtAPPrecio, txtAPExistencias;
    private Spinner cmbAPCategoria;
    private Button btnAPActualizar, btnAPEliminar, btnAPBuscar, btnAPAgregar, btnAEAtras;
    private Switch switchEditar;

    private List<Categoria> listarProducto = new ArrayList<>();
    private ArrayAdapter<Categoria> arrayAdapterCategorias;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregarproducto);

        txtAPNombre = findViewById(R.id.txtAPNombre);
        txtAPPrecio = findViewById(R.id.txtAPPrecio);
        cmbAPCategoria = findViewById(R.id.cmbAPCategoria);
        btnAPActualizar = findViewById(R.id.btnAPActualizar);
        btnAPEliminar = findViewById(R.id.btnAPEliminar);
        btnAPBuscar = findViewById(R.id.btnAPBuscar);
        switchEditar = findViewById(R.id.switch1);
        txtAPExistencias = findViewById(R.id.txtAPExistencias);
        btnAPAgregar = findViewById(R.id.btnAPAgregar);
        btnAEAtras = findViewById(R.id.btnAEAtras);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Llenar el Spinner con la lista de categorías
        arrayAdapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listarProducto);
        arrayAdapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbAPCategoria.setAdapter(arrayAdapterCategorias);

        // Manejar la selección del Spinner
        cmbAPCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Puedes realizar acciones adicionales si es necesario
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No se realiza ninguna acción cuando no se selecciona nada
            }
        });

        // Listar las categorías al iniciar la actividad
        listarProductos();

        // Configurar el listener del Switch
        switchEditar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Si el Switch está encendido, habilitar las vistas; de lo contrario, deshabilitarlas
            boolean isEnabled = isChecked;
            txtAPPrecio.setEnabled(isEnabled);
            cmbAPCategoria.setEnabled(isEnabled);
            btnAPActualizar.setEnabled(isEnabled);
        });

        // Inicialmente, deshabilitar las vistas si el Switch está apagado
        boolean isEnabledInicialmente = switchEditar.isChecked();
        txtAPPrecio.setEnabled(isEnabledInicialmente);
        cmbAPCategoria.setEnabled(isEnabledInicialmente);
        btnAPActualizar.setEnabled(isEnabledInicialmente);

        // Configurar el listener del botón Eliminar
        btnAPEliminar.setOnClickListener(view -> mostrarDialogoConfirmacion());

        // Configurar el listener del botón Buscar
        btnAPBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre del producto a buscar
                String nombreProducto = txtAPNombre.getText().toString().trim();

                // Verificar si el nombre no está vacío
                if (!nombreProducto.isEmpty()) {
                    // Llamar al método para buscar el producto
                    buscarProducto(nombreProducto);
                } else {
                    // Mostrar un mensaje si el campo está vacío
                    Toast.makeText(AgregarproductoActivity.this, "Ingrese un nombre de producto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el listener del botón Actualizar
        btnAPActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para actualizar el producto en la base de datos
                actualizarProducto();
            }
        });

        // Configurar el listener del botón Actualizar
        btnAPAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para actualizar el producto en la base de datos
                actualizarProducto();
            }
        });

        btnAEAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgregarproductoActivity.this, CrearproductoActivity.class));
            }
        });
    }

    // Método para mostrar el cuadro de diálogo de confirmación
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas eliminar este producto?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Obtener el nombre del producto a eliminar
                String nombreProducto = txtAPNombre.getText().toString().trim();

                // Limpiar los EditText
                txtAPNombre.setText("");
                txtAPPrecio.setText("");
                txtAPExistencias.setText("");

                // Verificar si el nombre no está vacío
                if (!nombreProducto.isEmpty()) {
                    // Llamar al método para eliminar el producto
                    eliminarProducto(nombreProducto);
                } else {
                    // Mostrar un mensaje si el campo está vacío
                    Toast.makeText(AgregarproductoActivity.this, "Ingrese un nombre de producto", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No hacer nada o manejar la acción según tu necesidad
            }
        });
        builder.show();
    }

    // Método para listar las categorías
    private void listarProductos() {
        databaseReference.child("Categoria").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listarProducto.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Categoria c = objSnapshot.getValue(Categoria.class);
                    listarProducto.add(c);
                }

                // Notificar al adaptador que los datos han cambiado
                arrayAdapterCategorias.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });
    }

    // Método para buscar un producto por nombre
    private void buscarProducto(String nombreProducto) {
        // Obtener una referencia al nodo "productos" en la base de datos
        DatabaseReference productosReference = databaseReference.child("productos");

        // Realizar la consulta para buscar el producto por nombre
        productosReference.orderByChild("nombre").equalTo(nombreProducto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Verificar si se encontraron resultados
                if (snapshot.exists()) {
                    // Obtener el primer producto encontrado (asumiendo que hay solo uno con ese nombre)
                    DataSnapshot primerProducto = snapshot.getChildren().iterator().next();
                    Producto productoEncontrado = primerProducto.getValue(Producto.class);

                    // Mostrar la información en los EditText
                    txtAPPrecio.setText(String.valueOf(productoEncontrado.getPrecio()));
                    txtAPExistencias.setText(productoEncontrado.getCantidad());
                    cmbAPCategoria.setSelection(obtenerPosicionCategoria(productoEncontrado.getIdCategoria()));
                    // Agregar lógica adicional según sea necesario

                } else {
                    // Mostrar un mensaje si no se encontraron resultados
                    Toast.makeText(AgregarproductoActivity.this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });
    }

    // Método para obtener la posición de la categoría en el Spinner
    private int obtenerPosicionCategoria(String idCategoria) {
        for (int i = 0; i < listarProducto.size(); i++) {
            if (listarProducto.get(i).getId().equals(idCategoria)) {
                return i;
            }
        }
        return 0;
    }

    // Método para eliminar un producto por nombre
    private void eliminarProducto(String nombreProducto) {
        // Obtener una referencia al nodo "productos" en la base de datos
        DatabaseReference productosReference = databaseReference.child("productos");

        // Realizar la consulta para buscar el producto por nombre
        productosReference.orderByChild("nombre").equalTo(nombreProducto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Verificar si se encontraron resultados
                if (snapshot.exists()) {
                    // Obtener el primer producto encontrado (asumiendo que hay solo uno con ese nombre)
                    DataSnapshot primerProducto = snapshot.getChildren().iterator().next();
                    String productoKey = primerProducto.getKey();

                    // Eliminar el nodo del producto en la base de datos
                    productosReference.child(productoKey).removeValue();

                    // Opcional: Mostrar un mensaje o realizar alguna acción después de eliminar
                    Toast.makeText(AgregarproductoActivity.this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    // Mostrar un mensaje si no se encontraron resultados
                    Toast.makeText(AgregarproductoActivity.this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });
    }



    // Método para actualizar el producto en la base de datos
    private void actualizarProducto() {
        // Obtener el nombre del producto
        String nombreProducto = txtAPNombre.getText().toString().trim();

        // Verificar si el nombre no está vacío
        if (!nombreProducto.isEmpty()) {
            // Obtener los nuevos valores de los campos de entrada
            String nuevoPrecio = txtAPPrecio.getText().toString().trim();
            String nuevaExistencia = txtAPExistencias.getText().toString().trim();
            String nuevaCategoriaId = listarProducto.get(cmbAPCategoria.getSelectedItemPosition()).getId();

            // Verificar campos vacíos
            if (nuevoPrecio.isEmpty() || nuevaExistencia.isEmpty()) {
                Toast.makeText(AgregarproductoActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener una referencia al nodo "productos" en la base de datos
            DatabaseReference productosReference = databaseReference.child("productos");

            // Realizar la consulta para buscar el producto por nombre
            productosReference.orderByChild("nombre").equalTo(nombreProducto).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // Verificar si se encontraron resultados
                    if (snapshot.exists()) {
                        // Obtener el primer producto encontrado (asumiendo que hay solo uno con ese nombre)
                        DataSnapshot primerProducto = snapshot.getChildren().iterator().next();
                        String productoKey = primerProducto.getKey();

                        // Crear un objeto Producto actualizado
                        Producto productoActualizado = new Producto(nombreProducto, nuevaExistencia, nuevoPrecio, nuevaCategoriaId);

                        // Actualizar el producto en la base de datos
                        productosReference.child(productoKey).setValue(productoActualizado);

                        // Opcional: Mostrar un mensaje o realizar alguna acción después de actualizar
                        Toast.makeText(AgregarproductoActivity.this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar un mensaje si no se encontraron resultados
                        Toast.makeText(AgregarproductoActivity.this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Manejar la cancelación si es necesario
                }
            });
        } else {
            // Mostrar un mensaje si el campo está vacío
            Toast.makeText(AgregarproductoActivity.this, "Ingrese un nombre de producto", Toast.LENGTH_SHORT).show();
        }
    }
}

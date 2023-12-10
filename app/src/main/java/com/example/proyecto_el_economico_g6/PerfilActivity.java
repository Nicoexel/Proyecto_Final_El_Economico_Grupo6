package com.example.proyecto_el_economico_g6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_el_economico_g6.Config.Persona;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    EditText txtNombre, txtTelefono, txtLatitud, txtLongitud;
    Button btnCancelar, btnGuardar;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Relacionar botones
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGuardar = findViewById(R.id.btnGuardar);

        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        auth = FirebaseAuth.getInstance();

        // Verificar el estado de autenticación al iniciar la actividad
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Obtener el ID del usuario actual
            String userId = currentUser.getUid();

            // Crear referencia al nodo de usuario en la base de datos
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Persona").child(userId);

            // Agregar un ValueEventListener para obtener los datos del nodo
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Persona persona = dataSnapshot.getValue(Persona.class);

                    if (persona != null) {
                        // Actualizar los EditText con los datos del nodo
                        txtNombre.setText(persona.getNombre());
                        txtTelefono.setText(persona.getTelefono());
                        txtLatitud.setText(persona.getLatitud());
                        txtLongitud.setText(persona.getLongitud());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores si es necesario
                }
            });

            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener nuevos valores de los campos de texto
                    String nuevoNombre = txtNombre.getText().toString();
                    String nuevoTelefono = txtTelefono.getText().toString();
                    String nuevaLatitud = txtLatitud.getText().toString();
                    String nuevaLongitud = txtLongitud.getText().toString();

                    // Llamar al método para actualizar los datos en Firebase
                    actualizarDatosFirebase(nuevoNombre, nuevoTelefono, nuevaLatitud, nuevaLongitud);

                    // Mostrar notificación de que los cambios se realizaron con éxito
                    mostrarNotificacion("Cambios realizados con éxito");

                    // Redirige a la actividad de inicio
                    startActivity(new Intent(PerfilActivity.this, InicioActivity.class));
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PerfilActivity.this, InicioActivity.class));
                }
            });
        }
    }

    // Método para actualizar los datos en Firebase
    private void actualizarDatosFirebase(String nuevoNombre, String nuevoTelefono, String nuevaLatitud, String nuevaLongitud) {
        // Crear un mapa para almacenar los nuevos valores a actualizar
        Map<String, Object> actualizacionDatos = new HashMap<>();
        actualizacionDatos.put("nombre", nuevoNombre);
        actualizacionDatos.put("telefono", nuevoTelefono);
        actualizacionDatos.put("latitud", nuevaLatitud);
        actualizacionDatos.put("longitud", nuevaLongitud);

        // Actualizar solo los campos especificados en Firebase
        databaseReference.updateChildren(actualizacionDatos);
    }

    // Método para mostrar una notificación utilizando Toast
    private void mostrarNotificacion(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}

package com.example.proyecto_el_economico_g6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.DescriptorProtos;

public class InicioActivity extends AppCompatActivity {

    ImageButton btnPerfilUser, btncategoria, btncrearpedido;
    TextView lblcategoria,lblcrearproducto;
    String rolUsuario; // Variable para almacenar el rol del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        lblcategoria = findViewById(R.id.lblcategoria);
        lblcrearproducto = findViewById(R.id.textView6);

        btnPerfilUser = findViewById(R.id.btnPerfilUser);
        btncategoria = findViewById(R.id.btncategoria);
        btncrearpedido = findViewById(R.id.btnCrearPedido);

        btnPerfilUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioActivity.this, PerfilActivity.class));
            }
        });

        btncategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioActivity.this, CategoriActivity.class));
            }
        });

        btncrearpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicioActivity.this, CrearproductoActivity.class));
            }
        });

        // Obtener el rol del usuario desde Firebase
        obtenerRolUsuarioDesdeFirebase();
    }

    // Método para obtener el rol del usuario desde Firebase
    private void obtenerRolUsuarioDesdeFirebase() {
        // Obtener el ID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Crear referencia al nodo de usuario en la base de datos
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Persona").child(userId);

            // Agregar un ValueEventListener para obtener el rol del usuario
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Obtener el valor de rol_usuario
                        rolUsuario = dataSnapshot.child("rol_usuario").getValue(String.class);

                        // Ocultar o mostrar el botón de categoría según el rol del usuario
                        actualizarVisibilidadBotonCategoria();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // Método para ocultar o mostrar el botón de categoría según el rol del usuario
    private void actualizarVisibilidadBotonCategoria() {
        if (rolUsuario != null && rolUsuario.equals("admin")) {
            // Mostrar el botón de categoría si el rol es admin
            btncategoria.setVisibility(View.VISIBLE);
            lblcategoria.setVisibility(View.VISIBLE);
            btncrearpedido.setVisibility(View.VISIBLE);
            lblcrearproducto.setVisibility(View.VISIBLE);
        } else {
            // Ocultar el botón de categoría en otros casos
            btncategoria.setVisibility(View.GONE);
            lblcategoria.setVisibility(View.GONE);
            btncrearpedido.setVisibility(View.GONE);
            lblcrearproducto.setVisibility(View.GONE);
        }
    }
}

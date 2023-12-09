package com.example.proyecto_el_economico_g6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_el_economico_g6.Config.Persona;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView    lblcuentaExiste;
    EditText    txtnombre, txtapellido, txttelefono, txtEmail, txtpassword, txtconfirmPass;
    Button btnRegistrar ;
    String patronEmail = " /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/";
    //String id, latitud, longitud, rol;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mFirestore;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

        lblcuentaExiste = findViewById(R.id.lblCuentaNueva);
        txtnombre = findViewById(R.id.txtnombre);
        txtapellido = findViewById(R.id.txtapellido);
        txttelefono = findViewById(R.id.txttelefono);
        txtEmail = findViewById(R.id.txtEmail);
        txtpassword = findViewById(R.id.txtPass1);
        txtconfirmPass = findViewById(R.id.txtPass2);
        btnRegistrar = findViewById(R.id.btncrear);
        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        lblcuentaExiste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
            }
        });

    }//fin onCreate

    private void PerforAuth() {

        String nombre = txtnombre.getText().toString();
        String apellido = txtapellido.getText().toString();
        String telefono = txttelefono.getText().toString();
        String latitud = String.valueOf(145.0145354);
        String longitud = String.valueOf(169.45682);
        String rol = "usuario";
        String email = txtEmail.getText().toString();
        String password = txtpassword.getText().toString();
        String confirmPass = txtconfirmPass.getText().toString();
        String foto = "";

        //evitar ingresar zaracteres especiales
        if (email.matches(patronEmail)) {
            txtEmail.setError("No usar Caracteres especiales!");
        } else if (password.isEmpty() || password.length() < 6) {
            txtpassword.setError("Ingrese una contraseña valida!");
        } else if (!password.equals(confirmPass)) {
            txtconfirmPass.setError("Contraseña no concuerda!");
        } else if(nombre.equals("")){
            txtnombre.setError("Debe escribir un nombre!");
        }else if(apellido.equals("")) {
            txtapellido.setError("Debe escribir un apellido!");
        }else if(telefono.equals("")) {
            txttelefono.setError("Debe escribir un telefono!");
        }else{
                progressDialog.setMessage("Registro en proceso");
                progressDialog.setTitle("Registro");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            String id = mAuth.getCurrentUser().getUid();

                            //Prueba con Map
                           // Map<String, Object> map = new HashMap<>();

                            Persona p = new Persona();
                            p.setId(id);
                            p.setNombre(nombre);
                            p.setApellido(apellido);
                            p.setCorreo(email);
                            p.setLatitud(latitud);
                            p.setLongitud(longitud);
                            p.setRol_usuario(rol);
                            p.setFoto(foto);

                            databaseReference.child("Persona").child(p.getId()).setValue(p);

                            /*
                            //usando Map
                            map.put("id", id);
                            map.put("nombre", nombre);
                            map.put("apellido",apellido);
                            map.put("telefono", telefono);
                            map.put("email", email);
                            map.put("latitud", latitud);
                            map.put("longitud", longitud);
                            map.put("rol_usuario", rol);
                            map.put("foto", foto);


                            mFirestore.collection("usuarios").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Error al salvar!", Toast.LENGTH_LONG).show();
                                }
                            });*/

                            progressDialog.dismiss();
                            cambioActividad();
                            Toast.makeText(RegisterActivity.this,"Registro Exitso!", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }//cierre else

    }//fin perforauth

    private void cambioActividad() {
        Intent intent = new Intent(RegisterActivity.this,InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
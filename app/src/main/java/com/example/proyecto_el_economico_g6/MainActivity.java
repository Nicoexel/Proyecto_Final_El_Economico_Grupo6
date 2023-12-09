package com.example.proyecto_el_economico_g6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText txtEmail, txtpassword;
    TextView lblCrearCuenta, lblRestablecer;
    Button btnlogin;
    String patronEmail = " /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$/";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblCrearCuenta = findViewById(R.id.lblCuentaNueva);
        lblRestablecer = findViewById(R.id.lblRestablecer);
        txtEmail = findViewById(R.id.txtEmail);
        txtpassword = findViewById(R.id.txtPass1);
        btnlogin = findViewById(R.id.btncrear);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        lblCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inciarSesion();
            }
        });


    }

    private void inciarSesion() {
        String email = txtEmail.getText().toString();
        String password = txtpassword.getText().toString();

        //evitar ingresar caracteres especiales
        if (email.matches(patronEmail)) {
            txtEmail.setError("No usar Caracteres especiales!");
        } else if (password.isEmpty() || password.length() < 6) {
            txtpassword.setError("Ingrese una contraseña valida!");
        } else {
            progressDialog.setMessage("Iniciando sesion");
            progressDialog.setTitle("Accediendo");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        cambioActividad();
                        Toast.makeText(MainActivity.this, "Incio de Sesion Exitsa!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
            }

        lblRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogo = getLayoutInflater().inflate(R.layout.dialogo_restablecer, null);
                EditText emailBox = dialogo.findViewById(R.id.txtcorreo);

                builder.setView(dialogo);
                AlertDialog dialog = builder.create();

                dialogo.findViewById(R.id.btnEnviar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String usuarioMail = emailBox.getText().toString();

                        if(TextUtils.isEmpty(usuarioMail)&& !Patterns.EMAIL_ADDRESS.matcher(usuarioMail).matches()){
                            Toast.makeText(MainActivity.this,"Ingrese un corrreo registrado",Toast.LENGTH_LONG).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(usuarioMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Toast.makeText(MainActivity.this,"Revise su correo",Toast.LENGTH_LONG).show();
                                         dialog.dismiss();
                                     }else{
                                         Toast.makeText(MainActivity.this,"No se puedo enviar, hay un error", Toast.LENGTH_LONG).show();
                                     }
                            }
                        });
                    }
                });
                dialogo.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow() !=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    private void cambioActividad() {
        Intent intent = new Intent(MainActivity.this,InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
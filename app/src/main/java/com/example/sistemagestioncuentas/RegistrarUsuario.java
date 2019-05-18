package com.example.sistemagestioncuentas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarUsuario extends AppCompatActivity {

    private EditText Nombre;
    private EditText Email;
    private EditText Password;

    private Button btnRegistrar;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        //inicioa del objeto firebase
        mAuth = FirebaseAuth.getInstance();

        // identificacion de variables y botones
        Nombre = (EditText) findViewById(R.id.txtnombre);
        Email = (EditText) findViewById(R.id.txtemail);
        Password = (EditText) findViewById(R.id.txtpassword);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        login = (TextView) findViewById(R.id.login);


        mProgress = new ProgressDialog(this);

        //clic en  login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrarUsuario.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // clic en registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });


    }

// metodo registrar Usuario
    private void registrar()
    {

// se obtiene el email y la contraseña desde las cajas de texto y el trim es para borrar los espcios del finala o al inicio del texto
        final String name = Nombre.getText().toString().trim();
        final String email = Email.getText().toString().trim();
        final String password = Password.getText().toString().trim();

//verificar que las cajas de textos no esten vacias
        // si esta vacia la caja e texto entonces se muestra una notificacion
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"se debe ingresar un nombre",Toast.LENGTH_LONG).show();
            return;
        }   // si esta vacia la caja e texto entonces se muestra una notificacion
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }   // si esta vacia la caja e texto entonces se muestra una notificacion
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"se debe ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }
//
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

           //mensaje de progreso
            mProgress.setMessage("Realizando el registro...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            //si el registro es exitoso
                            if (task.isSuccessful()) {
                                mAuth.signInWithEmailAndPassword(email,password);

                                //registrar los datos a firebase
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                                currentUserDB.child("name").setValue(name);
                                currentUserDB.child("image").setValue("default");
                                //una notificacion de que se registro corrctamente y se enia a la vista de MainActivity
                                Toast.makeText(RegistrarUsuario.this, "Se registro correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrarUsuario.this, MainActivity.class);
                                startActivity(intent);

                            } else
                            {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    Toast.makeText(RegistrarUsuario.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(RegistrarUsuario.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                                }
                            }



                        }
                    });
        }

    }

}

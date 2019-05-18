package com.example.sistemagestioncuentas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistemagestioncuentas.model.Users;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // SE DECLARA LOS EXTO Y LOS BOTONES
    private EditText Email;
    private EditText Password;
    private Button btnIngresar;
    private Button btnRegitro;
    private ProgressDialog progressDialog;
    //declaramos un objeto firebaseAUth
    private FirebaseAuth firebaseAuth;

//Google
  private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private SignInButton loginGoogle;
    private static final int RC_SIGN_IN=777;
   protected GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
   //facebook

    private LoginButton loginFacebook;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_login);

   //Iniciamos el objeto firebaseAuth

        callbackManager =  CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();


        //especificacion de texto y botones se refencia con el id
        Email = (EditText) findViewById(R.id.txtemail);
        Password = (EditText) findViewById(R.id.txtpassword);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnRegitro = (Button) findViewById(R.id.Registrate) ;
        loginFacebook = (LoginButton)findViewById(R.id.Facebook);
        //instanciamos la lista de progreso
        progressDialog = new ProgressDialog(this);

loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
        goMainScreen();
    }

    @Override
    public void onCancel() {
        Toast.makeText(Login.this, "se cancelo la sesion: "+Email.getText(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(Login.this, "error al iniciar la sesion: "+Email.getText(), Toast.LENGTH_SHORT).show();
    }
});
        //click en el btn Ingresar
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        //clic en el btn registrar  nuevo usuario
        btnRegitro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this , RegistrarUsuario.class);
                startActivity(intent);
            }
        });



        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Login.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        loginGoogle =(SignInButton)findViewById(R.id.Google);
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser users = firebaseAuth.getCurrentUser();
                if(users !=null)
                {
                    //esta logueado
                    EstaLogueado();
                }
                else
                {
                    // no esta Logueado
                    NoEstaLogueado();
                }
            }
        };

    }

    private void goMainScreen() {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    //loguearse con correo y acontraseña
    private void doLogin() {
        final String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();


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

            //modo de espera
            progressDialog.setMessage("Logueandose.. espere un momento");
            progressDialog.show();

            //Loaguear usuario
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // si la tarea es exitoso
                            if (task.isSuccessful()) {

                                Toast.makeText(Login.this, "Bienvenido: "+Email.getText(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplication(),MainActivity.class);
                                //se envia la variable

                                startActivity(intent);


                            }
                            else
                            {
                                    Toast.makeText(Login.this, "No se puede Loguear", Toast.LENGTH_SHORT).show();

                            }


                            progressDialog.dismiss();

                        }
                    });

    }
////final login

    //error de this en gmail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

// metodo de inicio de google

    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            ;

                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    private void handleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseauthWithGoogle(account);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Segundo error",Toast.LENGTH_SHORT).show();
        }
    }
    private void EstaLogueado() {
        Toast.makeText(getApplicationContext(),"Estas Logueado",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void NoEstaLogueado() {
        Toast.makeText(getApplicationContext(),"no Estas Logueado",Toast.LENGTH_SHORT).show();
    }

    private void firebaseauthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userReef = database.getReference();
                    Toast.makeText(getApplicationContext(),"Se esta ",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error al autenticarse",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}

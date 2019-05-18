package com.example.sistemagestioncuentas;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sistemagestioncuentas.Fragmento.ActualizarDatosFragment;
import com.example.sistemagestioncuentas.Fragmento.EgresosPendientesFragment;
import com.example.sistemagestioncuentas.Fragmento.GestionarCategoriaFragment;
import com.example.sistemagestioncuentas.Fragmento.GestionarEgresosFragment;
import com.example.sistemagestioncuentas.Fragmento.GestionarIngresosFragment;
import com.example.sistemagestioncuentas.Fragmento.IngresosPendientesFragment;
import com.example.sistemagestioncuentas.Fragmento.ReportesFragment;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {



    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;


    private TextView NavProfileUserEmail;
    private TextView NavProfileUserName;
    private TextView NavProfileUserId;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference UserRef;


    //
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        firebaseAuth =  FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("users");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView =(NavigationView)findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);



        photoImageView = (CircleImageView) navView.findViewById(R.id.photoImageView);
        nameTextView = (TextView) navView.findViewById(R.id.name);
        emailTextView = (TextView) navView.findViewById(R.id.emailTextView);
        idTextView = (TextView) navView.findViewById(R.id.idTextView);


        final CircleImageView NavProfileImage = (CircleImageView)navView.findViewById(R.id.photoImageView);
        NavProfileUserId = (TextView) navView.findViewById(R.id.idTextView);
        NavProfileUserName = (TextView) navView.findViewById(R.id.name);
        NavProfileUserEmail = (TextView) navView.findViewById(R.id.emailTextView);


        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String Email = firebaseAuth.getCurrentUser().getEmail();
                    String UID = firebaseAuth.getCurrentUser().getUid();
                    NavProfileUserName.setText(name);
                    NavProfileUserId.setText(UID);
                    NavProfileUserEmail.setText(Email);
                    Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.com_facebook_auth_dialog_background).into(NavProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        navigationView.setNavigationItemSelectedListener(this);

//        idTextView = (TextView) navHeaderView.findViewById(R.id.navId);
//        nameTextView = (TextView) navHeaderView.findViewById(R.id.navNombre);
//        emailTextView = (TextView) navHeaderView.findViewById(R.id.navEmail);
//        photoImageView = (ImageView)navHeaderView.findViewById(R.id.navImagen);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                } else {
                    goLogInScreen();
                }
            }
        };
    }


    private void setUserData(FirebaseUser user){
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        idTextView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }




    private void goLogInScreen() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void logOut(View view) {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_usuario) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ActualizarDatosFragment()).commit();
        } else if (id == R.id.nav_categoria) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new GestionarCategoriaFragment()).commit();

        } else if (id == R.id.nav_Ingresos) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new GestionarIngresosFragment()).commit();

        } else if (id == R.id.nav_Egresos) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new GestionarEgresosFragment()).commit();

        } else if (id == R.id.nav_Reportes) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ReportesFragment()).commit();

        } else if (id == R.id.nav_IngresosPendientes) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new IngresosPendientesFragment()).commit();

        }
        else if (id == R.id.nav_EgresosPendientes) {
           getSupportFragmentManager().beginTransaction().replace(R.id.container,new EgresosPendientesFragment()).commit();


        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}


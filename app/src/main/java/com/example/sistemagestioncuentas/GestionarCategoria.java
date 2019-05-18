package com.example.sistemagestioncuentas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sistemagestioncuentas.model.Categoria;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestionarCategoria extends AppCompatActivity {
    private List<Categoria> listcategoria = new ArrayList<Categoria>();
    ArrayAdapter<Categoria> arrayAdapterCategoria;

    EditText nombre,descripcion;
    ListView listacategoria;
    Spinner tipomovimiento;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Categoria categoriaSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_categoria);

        tipomovimiento =(Spinner) findViewById(R.id.spinner);
        nombre = (EditText) findViewById(R.id.txt_nombreCategoria);
        descripcion = (EditText) findViewById(R.id.txt_descripcionCategoria);
        listacategoria = (ListView) findViewById(R.id.lst_Categoria);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.tipomovimiento,android.R.layout.simple_spinner_item);
        tipomovimiento.setAdapter(adapter);
        inicializarfirebase();
        //metodos
        listarDatos();

        listacategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoriaSelected = (Categoria) parent.getItemAtPosition(position);
                nombre.setText(categoriaSelected.getNomb_cat());
                descripcion.setText(categoriaSelected.getDesc_cat());

                if(categoriaSelected.getTipo_cat().equals("Ingresos")){
                    tipomovimiento.setSelection(0);
                }else{
                    tipomovimiento.setSelection(1);
                }

            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Categoria").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listcategoria.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Categoria c = objSnapshot.getValue(Categoria.class);
                    listcategoria.add(c);

                    arrayAdapterCategoria = new ArrayAdapter<Categoria>(GestionarCategoria.this, android.R.layout.simple_list_item_1, listcategoria);
                    listacategoria.setAdapter(arrayAdapterCategoria);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarfirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombrecat = nombre.getText().toString();
        String desccat = descripcion.getText().toString();
        String tipocat= tipomovimiento.getSelectedItem().toString();


        switch (item.getItemId()) {
            case R.id.icon_add: {
                if(nombrecat.equals("")||desccat.equals("")){
                    validacion();
                }else{
                    Categoria c = new Categoria();
                    c.setId_cat(UUID.randomUUID().toString());
                    c.setNomb_cat(nombrecat);
                    c.setDesc_cat(desccat);
                    c.setTipo_cat(tipocat);
                    databaseReference.child("Categoria").child(c.getId_cat()).setValue(c);
                    limpiarcajas();
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Categoría Añadida", Toast.LENGTH_SHORT);
                    toast1.show();

                }
                break;
            }


            case R.id.icon_save: {
                AlertDialog.Builder confirmacion1= new AlertDialog.Builder(GestionarCategoria.this);
                confirmacion1.setMessage("¿Seguro que quiere modificar esta categoría?").setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Categoria c = new Categoria();
                        c.setId_cat(categoriaSelected.getId_cat());
                        c.setNomb_cat(nombre.getText().toString());
                        c.setDesc_cat(descripcion.getText().toString());
                        c.setTipo_cat(tipomovimiento.getSelectedItem().toString());
                        databaseReference.child("Categoria").child(c.getId_cat()).setValue(c);
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Categoría Modificada", Toast.LENGTH_SHORT);
                        toast1.show();
                        limpiarcajas();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert =confirmacion1.create();
                alert.setTitle("Modificar Categoría");
                alert.show();
                break;
            }


            case R.id.icon_delete: {
                AlertDialog.Builder confirmacion= new AlertDialog.Builder(GestionarCategoria.this);
                confirmacion.setMessage("¿Seguro que quiere eliminar esta categoría?").setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Categoria c = new Categoria();
                        c.setId_cat(categoriaSelected.getId_cat());
                        databaseReference.child("Categoria").child(c.getId_cat()).removeValue();
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Categoría Borrada", Toast.LENGTH_SHORT);
                        toast1.show();
                        limpiarcajas();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert =confirmacion.create();
                alert.setTitle("Eliminar Categoría");
                alert.show();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarcajas() {
        nombre.setText("");
        descripcion.setText("");
    }

    private void validacion() {
        String nombrecat = nombre.getText().toString();
        String desccat = descripcion.getText().toString();
        if(nombrecat.equals("")){
            nombre.setError("Required");
        }else{
            if(desccat.equals("")){
                descripcion.setError("Required");}
        }
    }




}

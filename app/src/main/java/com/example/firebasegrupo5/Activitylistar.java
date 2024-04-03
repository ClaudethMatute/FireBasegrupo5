package com.example.firebasegrupo5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activitylistar extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> listaPersonas;

    private ArrayList<Personas> listPersons;
    private ArrayAdapter<String> adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button btnVerImagen, btnEliminar, btnEditar;
    Personas PersonaSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitylistar);
        listPersons = new ArrayList<Personas>();
        listView = findViewById(R.id.listView);
        inicializarFirabase();
        listarDatos();
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);

        btnVerImagen = (Button) findViewById(R.id.btnVerFoto);
        btnVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Convierte la cadena Base64 a un Bitmap
                    byte[] decodedString = Base64.decode(PersonaSelected.getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    AlertDialog.Builder builderFoto = new AlertDialog.Builder(Activitylistar.this);
                    LayoutInflater inflater = LayoutInflater.from(Activitylistar.this);
                    View viewFotoContacto = inflater.inflate(R.layout.fotopersona, null);

                    // Obtiene la referencia a la vista ImageView dentro del layout inflado
                    ImageView imageView = viewFotoContacto.findViewById(R.id.imgFotoPersona);
                    imageView.setImageBitmap(bitmap);

                    builderFoto.setView(viewFotoContacto);
                    AlertDialog dialogFoto = builderFoto.create();
                    dialogFoto.show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonaSelected = listPersons.get(position);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    parent.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                //view.setBackgroundResource(com.google.android.material.R.color.material_dynamic_neutral90);
                view.setBackgroundColor(Color.parseColor("#80ADD8E6"));
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PersonaSelected != null) {
                    // Eliminar la persona seleccionada de la base de datos
                    databaseReference.child("Personas").child(PersonaSelected.getId()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    listarDatos();
                                    Toast.makeText(Activitylistar.this, "Registro eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Activitylistar.this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Activitylistar.this, "Seleccione una persona para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PersonaSelected != null) {
                    // Editar la persona seleccionada de la base de datos
                    EnviarInfoActualizar();
                } else {
                    Toast.makeText(Activitylistar.this, "Seleccione una persona para editar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void listarDatos(){
        listaPersonas = new ArrayList<String>();
        databaseReference.child("Personas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot objSnapshot : snapshot.getChildren()){
                   Personas p = objSnapshot.getValue(Personas.class);
                    if (p != null) {
                        String datosPersona = "Nombre: " + p.getNombres() + "\n" +
                                "Apellidos: " + p.getApellidos() + "\n" +
                                "Correo: " + p.getCorreo() + "\n" +
                                "Fecha de Nacimiento: " + p.getFechanac() + "\n";
                        listaPersonas.add(datosPersona);
                        listPersons.add(p);
                    }
                    adapter = new ArrayAdapter<String>(Activitylistar.this, android.R.layout.simple_list_item_1, listaPersonas);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void EnviarInfoActualizar(){
        Intent intent = new Intent(Activitylistar.this, MainActivity.class);

        intent.putExtra("Id", PersonaSelected.getId().toString());
        intent.putExtra("Nombres", PersonaSelected.getNombres());
        intent.putExtra("Apellidos", PersonaSelected.getApellidos());
        intent.putExtra("Correo", PersonaSelected.getCorreo());
        intent.putExtra("Fechanac", PersonaSelected.getFechanac());
        intent.putExtra("Foto", PersonaSelected.getFoto());
        startActivity(intent);
    }

    private void inicializarFirabase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
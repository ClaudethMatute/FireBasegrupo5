package com.example.firebasegrupo5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class Activitylistar extends AppCompatActivity {
   Button buttonatras;

    private ListView listView;
    private ArrayList<String> listaPersonas;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitylistar);


        listView = findViewById(R.id.listView);
        listaPersonas = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPersonas);
        listView.setAdapter(adapter);

        // Obtener una referencia a la base de datos de Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("personas");

        // Agregar un ValueEventListener para escuchar los cambios en los datos
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersonas.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Personas persona = snapshot.getValue(Personas.class);
                    if (persona != null) {
                        String datosPersona = "Nombre: " + persona.getNombres() + "\n" +
                                "Apellidos: " + persona.getApellidos() + "\n" +
                                "Correo: " + persona.getCorreo() + "\n" +
                                "Fecha de Nacimiento: " + persona.getFechanac() + "\n";
                        listaPersonas.add(datosPersona);
                    }
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Activitylistar.this, "Error al cargar los datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
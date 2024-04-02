package com.example.firebasegrupo5;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    EditText editTextNombres, editTextApellidos, editTextCorreo, editTextFechaNac ;
    ImageView imageView;
    Button buttonAgregar, buttonActualizar, buttonEliminar, buttonObtener,btntakefoto;
    static final int  peticion_foto = 101;
    static final int ACCESS_CAMERA =  201;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String imagenBase64;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarFirabase();
        editTextNombres =(EditText) findViewById(R.id.editTextNombres);
        editTextApellidos =(EditText)  findViewById(R.id.editTextApellidos);
        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextFechaNac = (EditText) findViewById(R.id.editTextFechaNac);
        imageView =(ImageView) findViewById(R.id.imageView);
        btntakefoto = (Button) findViewById(R.id.btntakefoto);

        buttonAgregar = (Button) findViewById(R.id.buttonAgregar);
        buttonActualizar =(Button)  findViewById(R.id.buttonActualizar);
        buttonEliminar =(Button)  findViewById(R.id.buttonEliminar);
        buttonObtener = (Button) findViewById(R.id.buttonObtener);


        // Agregar listener al botón Agregar
        buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPersona();
            }
        });
        btntakefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermisosCamara();
            }
        });

        buttonObtener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activitylistar.class);
                startActivity(intent);
            }
        });
    }
    //AGREGADOS NUEVOS
    private void PermisosCamara() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},ACCESS_CAMERA);
        }
        else
        {
            CapturarFoto();
        }
    }

    private void inicializarFirabase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void CapturarFoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, peticion_foto);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == peticion_foto && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imagen);

            /*---------Convertir imagen a base64-------*/
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imagen.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imagenBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }

    private void agregarPersona() {
        Personas persona = new Personas();
        persona.setId(UUID.randomUUID().toString());
        persona.setNombres(editTextNombres.getText().toString().trim());
        persona.setApellidos(editTextApellidos.getText().toString().trim());
        persona.setCorreo(editTextCorreo.getText().toString().trim());
        persona.setFechanac(editTextFechaNac.getText().toString().trim());
        persona.setFoto(imagenBase64);


        databaseReference.child("Personas").child(persona.getId()).setValue(persona, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    // Limpia los campos y muestra el aviso
                    limpiarCampos();
                    Toast.makeText(MainActivity.this, "Persona agregada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al agregar persona", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void limpiarCampos() {
        editTextNombres.setText("");
        editTextApellidos.setText("");
        editTextCorreo.setText("");
        editTextFechaNac.setText("");
        imageView.setImageBitmap(null);
    }

}
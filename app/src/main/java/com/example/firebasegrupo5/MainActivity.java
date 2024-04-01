package com.example.firebasegrupo5;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
     EditText editTextNombres, editTextApellidos, editTextCorreo, editTextFechaNac ;
    ImageView imageView;
     Button buttonAgregar, buttonActualizar, buttonEliminar, buttonObtener,btntakefoto;
    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA =  201;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            dispatchTakePictureIntent();

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.firebasegrupo5.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE)
        {
            try {
                File Foto = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(Foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
        }
    }

    private void agregarPersona() {
        String nombres = editTextNombres.getText().toString().trim();
        String apellidos = editTextApellidos.getText().toString().trim();
        String correo = editTextCorreo.getText().toString().trim();
        String fechaNac = editTextFechaNac.getText().toString().trim();


        // Validar que todos los campos excepto la foto no estén vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || fechaNac.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener una referencia a la base de datos de Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("personas");

        // Generar un nuevo ID para la persona
        String id = dbRef.push().getKey();

        // Crear un objeto Personas con los datos ingresados
        Personas persona = new Personas(id, nombres, apellidos, correo, fechaNac);

        // Guardar la persona en la base de datos
        dbRef.child(id).setValue(persona, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(MainActivity.this, "Persona agregada exitosamente", Toast.LENGTH_SHORT).show();
                    // Limpiar los campos después de agregar la persona
                    editTextNombres.setText("");
                    editTextApellidos.setText("");
                    editTextCorreo.setText("");
                    editTextFechaNac.setText("");

                } else {
                    Toast.makeText(MainActivity.this, "Error al agregar la persona: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



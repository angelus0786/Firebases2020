package mx.edu.itsmt.firebases2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuarioActivity extends AppCompatActivity {


    EditText nombre,correo,password;
    Button registrar,iniciarSesion;

    String sNombre="";
    String sEmail="";
    String sPassword="";

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        nombre = findViewById(R.id.txtNombre);
        correo = findViewById(R.id.txtCorreo);
        password=findViewById(R.id.txtPassword);
        registrar = findViewById(R.id.btRegistrar);
        iniciarSesion = findViewById(R.id.btIniciarSesion);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNombre = nombre.getText().toString();
                sEmail= correo.getText().toString();
                sPassword=password.getText().toString();

                if (!sNombre.isEmpty()&&!sEmail.isEmpty()&&!sPassword.isEmpty()){
                    //contemplar los caracteres de firebase
                    if (sPassword.length()>=6){
                        registrarUsuario();
                    }else{
                        Toast.makeText(RegistroUsuarioActivity.this,"debe tener almenos 6 caracteres",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(RegistroUsuarioActivity.this,"debe llenar todos los campos",Toast.LENGTH_LONG).show();
                }

            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroUsuarioActivity.this,LoginActivity.class));
            }
        });


    }
    private void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Map<String,Object> map = new HashMap<>();
                    map.put("nombre",sNombre);
                    map.put("correo",sEmail);
                    map.put("password",sPassword);

                    String id = mAuth.getCurrentUser().getUid();

                    databaseReference.child("usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(
                                        new Intent(RegistroUsuarioActivity.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(RegistroUsuarioActivity.this,"No se pudieron crear datos correctos",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }else{
                    Toast.makeText(RegistroUsuarioActivity.this,"No se pudo Registrar Usuario",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

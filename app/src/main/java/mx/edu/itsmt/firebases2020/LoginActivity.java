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


public class LoginActivity extends AppCompatActivity {

    EditText correo,password;
    Button registrar,iniciar;


    String sEmail="";
    String sPassword="";

     FirebaseAuth mAuth;
     DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       correo = findViewById(R.id.txtCorreo);
       password=findViewById(R.id.txtPassword);
       registrar = findViewById(R.id.btRegistrar);
       iniciar = findViewById(R.id.btIniciar);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();


       iniciar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            sEmail= correo.getText().toString();
            sPassword=password.getText().toString();

            if (!sEmail.isEmpty()&&!sPassword.isEmpty()){
                //contemplar los caracteres de firebase
                if (sPassword.length()>=6){
                    loginUser();
                }else{
                    Toast.makeText(LoginActivity.this,"debe tener almenos 6 caracteres",Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(LoginActivity.this,"debe llenar todos los campos",Toast.LENGTH_LONG).show();
            }

           }
       });
       registrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this,RegistroUsuarioActivity.class));
               finish();
           }
       });

    }

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                  startActivity(new Intent(LoginActivity.this,MainActivity.class));
                  finish();
                }else{
                    Toast.makeText(LoginActivity.this,"No se pudo iniciar sesion ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

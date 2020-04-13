package mx.edu.itsmt.firebases2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mx.edu.itsmt.firebases2020.Model.Persona;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    EditText nomP, appP,correoP,passwordP;
    ListView list_Vpersonas;
    Button btCerrarSesion;
    TextView usuario;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    Persona personaSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nomP = findViewById(R.id.txt_nombrePersona);
        appP = findViewById(R.id.txt_appPersona);
        correoP =findViewById(R.id.txt_correoPersona);
        passwordP= findViewById(R.id.txt_passwordPersona);
        list_Vpersonas = findViewById(R.id.lv_datosPersonas);
        btCerrarSesion = findViewById(R.id.btcerrarSesion);
        usuario = findViewById(R.id.txtusuario);
          inicializarFirebase();
          listarDatos();
          obtenerInfoUser();

        list_Vpersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                nomP.setText(personaSelected.getNombre());
                appP.setText(personaSelected.getApellido());
                correoP.setText(personaSelected.getCorreo());
                passwordP.setText(personaSelected.getPassword());
            }
        });

        btCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mAuth.signOut();
             startActivity(new Intent(MainActivity.this,LoginActivity.class));
             finish();
            }
        });
    }

    private void obtenerInfoUser() {
        String id = mAuth.getCurrentUser().getUid();
        databaseReference.child("usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    String correo =  dataSnapshot.child("correo").getValue().toString();
                usuario.setText("Bienvenido: "+nombre+"\n"+correo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    list_Vpersonas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       String nombre = nomP.getText().toString();
       String correo = correoP.getText().toString();
       String password = passwordP.getText().toString();
       String app = appP.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nombre.equals("")||correo.equals("")||password.equals("")||app.equals("")){
                   validacion();
                }else{
                    Persona p = new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(app);
                    p.setCorreo(correo);
                    p.setPassword(password);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    System.out.println(p.toString());
                    Toast.makeText(this,"Agregar", Toast.LENGTH_LONG).show();
                    limpiarCajas();

                }
                break;
            }
            case R.id.icon_save:{
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombre(nomP.getText().toString().trim());
                p.setApellido(appP.getText().toString().trim());
                p.setCorreo(correoP.getText().toString().trim());
                p.setPassword(passwordP.getText().toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_delete:{
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
    nomP.setText("");
    appP.setText("");
    correoP.setText("");
    passwordP.setText("");
    }

    private void validacion(){
        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();
        String app = appP.getText().toString();

        if (nombre.equals("")){
            nomP.setError("Required");
        }
        else if( app.equals("")){
            appP.setError("Required");
        }
        else if( correo.equals("")){
            correoP.setError("Required");
        }
        else if( password.equals("")){
            passwordP.setError("Required");
        }
    }
}

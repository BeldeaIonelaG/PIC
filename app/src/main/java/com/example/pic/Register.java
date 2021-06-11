package com.example.pic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private EditText email;
    private EditText password1;
    private EditText password2;
    private Button buttonRegister;
    private TextView gotoLogin;

    private String email_string;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.editTextEmail);
        password1 = findViewById(R.id.editTextTextPassword);
        password2 = findViewById(R.id.editTextTextPassword2);
        buttonRegister = findViewById(R.id.button_register);
        gotoLogin = findViewById(R.id.goToLogIn);



        fAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

    }

    private void register(){
        email_string = email.getText().toString();//chiar trebuie trim?
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if(TextUtils.isEmpty(email_string)){
            email.setError("Email adress is required");
            return;
        }

        if(TextUtils.isEmpty(pass1)){
            password1.setError("Password is required");
            return;
        }

        if(TextUtils.isEmpty(pass2)){
            password2.setError("Password confirmation is required");
            return;
        }

        if(pass1.length() < 8){
            password1.setError("Password must contain at least 8 characters");
            return;
        }

        if(!pass1.equals(pass2)){
            password1.setError("Password confirmation must be the same with the password");
            password2.setError("Password confirmation must be the same with the password");
            return;
        }

        // register the user in firebase

        fAuth.createUserWithEmailAndPassword(email_string,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    CollectionReference userRef = FirebaseFirestore.getInstance()
                            .collection("Users");
                    userRef.add(new User(email_string));
                    Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else{
                    Toast.makeText(Register.this, "The email is already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
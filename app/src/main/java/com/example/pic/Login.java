package com.example.pic;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private TextView gotoRegister;
    private ImageView verify;

    private long backPressedTime;
    private Toast backToast;
    ObjectAnimator objectAnimator;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.button_login);
        gotoRegister =findViewById(R.id.goToRegister);
        verify = findViewById(R.id.img_verify);
        verify.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        objectAnimator = ObjectAnimator.ofFloat(verify,"rotation", 360);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        /*pentru logout
        private void logout(){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();

        }
         */

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });



    }

    private void login(){

        String email_string = email.getText().toString().trim();
        String password1 = password.getText().toString();

        if(TextUtils.isEmpty(email_string)){
            email.setError("Email adress is required");
            return;
        }

        if(TextUtils.isEmpty(password1)){
            password.setError("Password is required");
            return;
        }

        //autentificare

        fAuth.signInWithEmailAndPassword(email_string,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    verify.setVisibility(View.VISIBLE);
                    objectAnimator.setDuration(2000);
                    objectAnimator.start();
                    Handler handler = new Handler(Looper.myLooper());
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 10 seconds
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }, 3000);

                }
                else{
                    Toast.makeText(Login.this,"ERROR: " + task.getException().getMessage(),Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            this.finishAffinity();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
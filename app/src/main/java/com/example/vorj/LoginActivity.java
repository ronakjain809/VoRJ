package com.example.vorj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email,pass;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.editmail);
        pass=(EditText)findViewById(R.id.editpass);
        auth=FirebaseAuth.getInstance();

    }

    public void Login(View view)
    {

        String email1 = email.getText().toString();
        String password = pass.getText().toString();
        if(!email1.equals("")&& !password.equals(""))
        {
            auth.signInWithEmailAndPassword(email1,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        //open another activity
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"User Could Not Be Logged In",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    public void goToRegister(View view)
    {
        Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }
}

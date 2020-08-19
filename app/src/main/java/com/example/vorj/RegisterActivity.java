package com.example.vorj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vorj.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText nam,id,passw;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nam=(EditText)findViewById(R.id.editname);
        id=(EditText)findViewById(R.id.editmail);
        passw=(EditText)findViewById(R.id.editpass);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
    }
    public void register(View view)
    {
        final String name= nam.getText().toString();
        final String email=id.getText().toString();
        final String password=passw.getText().toString();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    User user=new User(name,email,password,firebaseUser.getUid());
                    reference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())

                            {
                                finish();
                                Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(),"User Created Successfully",Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"User Could Not Be Registered",Toast.LENGTH_LONG).show();


                            }
                        }
                    });

                }
            }
        });

    }
}

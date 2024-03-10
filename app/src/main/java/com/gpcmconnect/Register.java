package com.gpcmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText name, email, password, username;
    Spinner designation;
    Button register;
    TextView login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        designation = findViewById(R.id.designation);
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);

        String[] designations = {"Select Designation", "Student", "Faculty"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, designations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designation.setAdapter(adapter);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                register.setVisibility(View.GONE);
                Log.d("userCred", email.getText().toString() + " " + password.getText().toString());

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    uploadUserDetails(user);

                                } else {

                                    Toast.makeText(getApplicationContext(), "User Creation failed.",Toast.LENGTH_SHORT).show();
                                    Log.d("userCreationError", String.valueOf(task.getException()));
                                    progressBar.setVisibility(View.GONE);
                                    register.setVisibility(View.VISIBLE);
                                    name.setText("");
                                    email.setText("");
                                    password.setText("");
                                    designation.setSelection(0);
                                }
                            }
                        });
            }
        });

    }

    public void uploadUserDetails(FirebaseUser user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!name.getText().toString().equals("") &&
                !email.getText().toString().equals("") &&
                    !password.getText().toString().equals("") &&
                        !designation.getSelectedItem().toString().equals("Select Designation")) {

            Map<String, String> u = new HashMap<>();
            u.put("name", name.getText().toString());
            u.put("username", username.getText().toString());
            u.put("designation", designation.getSelectedItem().toString());
            u.put("email", email.getText().toString());

            db.collection("users").document(user.getUid())
                    .set(u)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "User Created! Redirecting..", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("errorsInUpload", e.toString());
                            Toast.makeText(getApplicationContext(), "User Details Upload failed.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);
                            name.setText("");
                            email.setText("");
                            password.setText("");
                            designation.setSelection(0);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Fill all the details!", Toast.LENGTH_SHORT).show();
        }

    }


}
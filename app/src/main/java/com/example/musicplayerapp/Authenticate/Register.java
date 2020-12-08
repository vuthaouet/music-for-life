package com.example.musicplayerapp.Authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerapp.MainActivity;
import com.example.musicplayerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText rFullname, rEmail, rPassword, rPassword2;
    Button register, redirectLogin;
    ProgressBar regLoading;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = rEmail.getText().toString().trim();
                String password = rPassword.getText().toString().trim();
                final String fullName = rFullname.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    rEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    rPassword.setError("Password is Required");
                    return;
                }

                regLoading.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);

                            final Map<String, Object> user = new HashMap<>();
                            user.put("fullName", fullName);
                            user.put("email", email);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("newuser", "onSuccess: " + userID);

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName).build();
                                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            regLoading.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });

        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void initView() {
        rFullname = findViewById(R.id.textPersonNameReg);
        rEmail = findViewById(R.id.textEmailAddressReg);
        rPassword = findViewById(R.id.textPasswordReg);
        rPassword2 = findViewById(R.id.textPassword2Reg);

        register = findViewById(R.id.register_btn_reg);
        redirectLogin = findViewById(R.id.login_btn_reg);

        regLoading = findViewById(R.id.progressBar_reg);
    }
}
package com.example.projectactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectactivity.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*mAuth.signInWithEmailAndPassword("test@gmail.com","123456")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"登入成功",
                                            Toast.LENGTH_SHORT).show();
                                    Intent it = new Intent(MainActivity.this,Profile.class);
                                    startActivity(it);
                                }
                            }
                        });*/

                if("".equals(binding.usernameInput.getText().toString().trim())||"".equals(binding.pass.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this,"請輸入帳密", Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = binding.usernameInput.getText().toString();
                    String password = binding.pass.getText().toString();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        if(mAuth.getCurrentUser().isEmailVerified()) {
                                            Toast.makeText(MainActivity.this,"登入成功", Toast.LENGTH_SHORT).show();
                                            Intent it = new Intent(MainActivity.this,Home.class);
                                            startActivity(it);
                                        }
                                        else {
                                            mAuth.signOut();
                                            Toast.makeText(MainActivity.this,"尚未認證", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(e.toString().contains("TooManyRequest")) {
                                        Toast.makeText(MainActivity.this,"嘗試太多次，請稍後再嘗試", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(e.toString().contains("The password is invaild")) {
                                        Toast.makeText(MainActivity.this,"密碼錯誤", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (e.toString().contains("There is no user")) {
                                        Toast.makeText(MainActivity.this,"帳號不存在", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(e.toString().contains("formatted")) {
                                        Toast.makeText(MainActivity.this,"請輸入正確格式", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this,"登入失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        binding.sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,signin.class);
                startActivity(it);
            }
        });

        binding.forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("".equals(binding.usernameInput.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this,"請輸入帳號", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(binding.usernameInput.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this,"已送出更新連結", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (e.toString().contains("formatted")) {
                                        Toast.makeText(MainActivity.this,"請輸入正確格式",Toast.LENGTH_SHORT).show();
                                    }
                                    else if (e.toString().contains("There is no user")) {
                                        Toast.makeText(MainActivity.this,"帳號不存在",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"寄送失敗",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
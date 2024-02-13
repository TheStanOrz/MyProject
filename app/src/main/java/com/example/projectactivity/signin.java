package com.example.projectactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projectactivity.databinding.ActivityMainBinding;
import com.example.projectactivity.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signin extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ActivitySigninBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//隱藏上方Bar
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(binding.useraccSign.getText().toString().trim()) ||"".equals(binding.usernameSign.getText().toString().trim())
                        ||"".equals(binding.password.getText().toString().trim()) ||"".equals(binding.userphoneSign.getText().toString().trim())
                        ||"".equals(binding.userclassSign.getText().toString().trim())){
                    Toast.makeText(signin.this,"請輸入資料",Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(binding.useraccSign.getText().toString(),binding.password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("Name",binding.usernameSign.getText().toString());
                                        user.put("PhoneNumber",binding.userphoneSign.getText().toString());
                                        user.put("Rank","0");
                                        user.put("FriendCount",1);
                                        user.put("RequestCount",1);
                                        user.put("GoalCount",1);
                                        user.put("Class",binding.userclassSign.getText().toString());
                                        user.put("Points",0);
                                        db.collection(binding.useraccSign.getText().toString())
                                                .document("profile")
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        mAuth.signInWithEmailAndPassword(binding.useraccSign.getText().toString(),binding.password.getText().toString());
                                                        mAuth.getCurrentUser().sendEmailVerification();
                                                        mAuth.signOut();
                                                        Toast.makeText(signin.this,"註冊成功，已送出認證信",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(e.toString().contains("already")) {
                                        Toast.makeText(signin.this,"帳號已被註冊",Toast.LENGTH_LONG).show();
                                    }
                                    else if (e.toString().contains("formatted")) {
                                        Toast.makeText(signin.this,"請輸入正確格式",Toast.LENGTH_LONG).show();
                                    }
                                    else if (e.toString().contains("characters")) {
                                        Toast.makeText(signin.this,"密碼須至少六位",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(signin.this,"註冊失敗",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
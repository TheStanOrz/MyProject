package com.example.projectactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectactivity.databinding.ActivityProfileBinding;
import com.example.projectactivity.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private BottomNavigationView nav;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//隱藏上方Bar
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        String email = mAuth.getCurrentUser().getEmail();
        update(email);
        //登出
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent it = new Intent(Profile.this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
        //更改圖片
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker,101);
            }
        });
        //更改班級
        binding.Class.setOnClickListener(new View.OnClickListener() {
            LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);
            View addView = layoutInflater.inflate(R.layout.updateclass,null);
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Profile.this)
                        .setTitle("更改班級")
                        .setView(addView)
                        .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText infor = addView.findViewById(R.id.infor);
                                if("".equals(infor.getText().toString().trim())) {
                                    Toast.makeText(Profile.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection(email)
                                            .document("profile")
                                            .update("Class",infor.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Profile.this,"更新成功", Toast.LENGTH_SHORT).show();
                                                    update(email);
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
        //更改名稱
        binding.Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);
                View addView = layoutInflater.inflate(R.layout.updatename,null);
                new AlertDialog.Builder(Profile.this)
                        .setTitle("更改名稱")
                        .setView(addView)
                        .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText infor = addView.findViewById(R.id.infor);
                                if("".equals(infor.getText().toString().trim())) {
                                    Toast.makeText(Profile.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection(email)
                                            .document("profile")
                                            .update("Name",infor.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Profile.this,"更新成功", Toast.LENGTH_SHORT).show();
                                                    update(email);
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
        //更改電話
        binding.PhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);
                View addView = layoutInflater.inflate(R.layout.updateinfor,null);
                new AlertDialog.Builder(Profile.this)
                        .setTitle("更改電話")
                        .setView(addView)
                        .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText infor = addView.findViewById(R.id.infor);
                                if("".equals(infor.getText().toString().trim())) {
                                    Toast.makeText(Profile.this,"請輸入資料",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection(email)
                                            .document("profile")
                                            .update("PhoneNumber",infor.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Profile.this,"更新成功",Toast.LENGTH_SHORT).show();
                                                    update(email);
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        //切換頁面
        nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.profile);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.goal:
                        startActivity(new Intent(getApplicationContext(),Goal.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.friend:
                        startActivity(new Intent(getApplicationContext(),Friend.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==101){
            Uri uri = data.getData();
            storageRef.child("user").child(mAuth.getCurrentUser().getEmail().toString()+".png")
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Glide.with(Profile.this)
                                    .load(uri)
                                    .into(binding.imageView);
                            Toast.makeText(Profile.this,"上傳成功",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this,"上傳失敗",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    //更新畫面資料
    void update(String email)
    {
        db.collection(email)
                .document("profile")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            binding.Name.setText(documentSnapshot.getString("Name"));
                            binding.HighestRank.setText(documentSnapshot.getString("Rank"));
                            binding.Class.setText(documentSnapshot.getString("Class"));
                            binding.PhoneNumber.setText(documentSnapshot.getString("PhoneNumber"));
                            binding.Points.setText(documentSnapshot.getLong("Points").toString());
                            binding.UserEmail.setText(email);
                        }
                    }
                });
        storageRef.child("user").child(mAuth.getCurrentUser().getEmail().toString()+".png")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(Profile.this)
                                .load(uri)
                                .into(binding.imageView);
                    }
                });
    }
}
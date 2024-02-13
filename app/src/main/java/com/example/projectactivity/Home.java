package com.example.projectactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.projectactivity.databinding.ActivityHomeBinding;
import com.example.projectactivity.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends AppCompatActivity {

    private BottomNavigationView nav;
    private ActivityHomeBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private Date dt=new Date();
    private String dts=sdf.format(dt);
    private Long dd=Long.parseLong(dts)%100;
    private Long MM=(Long.parseLong(dts)/100)%100;
    private Long yy=(Long.parseLong(dts)/10000);

    private long cntT=0,cntF=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//隱藏上方Bar
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        String email = mAuth.getCurrentUser().getEmail();
        update(email);

        nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.home);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
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
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    void update(String email) {
        db.collection(email)
                .whereNotEqualTo("finish","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                if((doc.getLong("yy").toString().equals(yy.toString())&doc.getLong("MM")<MM)
                                        ||(doc.getLong("MM")==MM&doc.getLong("dd")<dd) ||(doc.getLong("yy")<yy)) {
                                    if(doc.getLong("glcnt")<10) {
                                        db.collection(email)
                                                .document("goal0"+doc.getLong("glcnt"))
                                                .delete();
                                    }
                                    else {
                                        db.collection(email)
                                                .document("goal"+doc.getLong("glcnt"))
                                                .delete();
                                    }
                                }
                                else {
                                    if(doc.getString("finish").equals("T")) {
                                        cntT+=1;
                                    }
                                    else {
                                        cntF+=1;
                                    }
                                    binding.progress.setProgress((100/((int)cntT+(int)cntF))*((int)cntT));
                                    binding.goal.setText("完成進度 : "+cntT+"/"+(cntT+cntF));
                                }
                            }
                        }
                    }
                });
    }
}
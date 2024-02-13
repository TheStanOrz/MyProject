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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectactivity.databinding.ActivityGoalBinding;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Goal extends AppCompatActivity {
    private BottomNavigationView nav;
    private ActivityGoalBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private String BigSmall="";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private Date dt=new Date();
    private String dts=sdf.format(dt);
    private Long dd=Long.parseLong(dts)%100;
    private Long MM=(Long.parseLong(dts)/100)%100;
    private Long yy=(Long.parseLong(dts)/10000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//隱藏上方Bar
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        String email = mAuth.getCurrentUser().getEmail();
        update(email);

        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Goal.this);
                View addView = layoutInflater.inflate(R.layout.addgoal,null);
                new AlertDialog.Builder(Goal.this)
                        .setTitle("新增目標")
                        .setView(addView)
                        .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText goal = addView.findViewById(R.id.goal);
                                EditText date = addView.findViewById(R.id.date);

                                if("".equals(goal.getText().toString().trim()) ||"".equals(date.getText().toString().trim())) {
                                    Toast.makeText(Goal.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Long MMadd;
                                    Long ddadd;
                                    for(int cnt=0;cnt<date.getText().toString().length();cnt++) {
                                        if(!Character.isDigit(date.getText().toString().charAt(cnt))) {
                                            Toast.makeText(Goal.this,"請輸入數字", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        else {
                                            if(date.getText().toString().length()!=4) {
                                                Toast.makeText(Goal.this,"請輸入正確格式(MMDD)", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            else {
                                                ddadd = Long.parseLong(date.getText().toString())%100;
                                                MMadd = (Long.parseLong(date.getText().toString())/100);
                                                if(MMadd>12|MMadd<01) {
                                                    Toast.makeText(Goal.this,"月份錯誤", Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                                else {
                                                    //判斷大小月
                                                    if(MMadd==1||MMadd==3||MMadd==5||MMadd==7||MMadd==8||MMadd==10||MMadd==12) {
                                                        BigSmall="big";
                                                    }
                                                    else if(MMadd==4||MMadd==6||MMadd==9||MMadd==11) {
                                                        BigSmall="small";
                                                    }
                                                    //2月
                                                    else {
                                                        if(yy%4==0)
                                                        {
                                                            BigSmall="29";
                                                        }
                                                        else {
                                                            BigSmall="28";
                                                        }
                                                    }

                                                    if(BigSmall=="big"&ddadd>31) {
                                                        Toast.makeText(Goal.this,"日期錯誤(>31)", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    else if(BigSmall=="small"&ddadd>30) {
                                                        Toast.makeText(Goal.this,"日期錯誤(>30)", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    else if(BigSmall=="29"&ddadd>29) {
                                                        Toast.makeText(Goal.this,"日期錯誤(>29)", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    else if(BigSmall=="28"&ddadd>28) {
                                                        Toast.makeText(Goal.this,"日期錯誤(>28)", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    else if(ddadd<1) {
                                                        Toast.makeText(Goal.this,"日期錯誤(<1)", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    else {
                                                        if((MMadd>MM&&MMadd-MM>1)||(MMadd<MM&&(MMadd+12)-MM>1)) {
                                                            Toast.makeText(Goal.this,"月份不能超過一個月", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                        else if(MMadd==MM&ddadd<dd) {
                                                            Toast.makeText(Goal.this,"日期已過", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                        else {
                                                            db.collection(email)
                                                                    .document("profile")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                                Long GoalCount = documentSnapshot.getLong("GoalCount");
                                                                                Map<String, Object> goaladd = new HashMap<>();
                                                                                goaladd.put("goal",goal.getText().toString());
                                                                                if(MMadd<MM) goaladd.put("yy",yy+1);
                                                                                else {
                                                                                    goaladd.put("yy",yy);}
                                                                                    goaladd.put("MM",MMadd);
                                                                                    goaladd.put("dd",ddadd);
                                                                                    goaladd.put("glcnt",GoalCount);
                                                                                    goaladd.put("finish","F");
                                                                                if(GoalCount<10) {
                                                                                    db.collection(email)
                                                                                            .document("goal0"+GoalCount)
                                                                                            .set(goaladd);
                                                                                }
                                                                                else {
                                                                                    db.collection(email)
                                                                                            .document("goal"+GoalCount)
                                                                                            .set(goaladd);
                                                                                }

                                                                                db.collection(email)
                                                                                        .document("profile")
                                                                                        .update("GoalCount",GoalCount+1);
                                                                                Toast.makeText(Goal.this,"新增成功，"+MMadd+"月"+ddadd+"日", Toast.LENGTH_SHORT).show();
                                                                                update(email);
                                                                            }
                                                                        }
                                                                    });
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
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
        binding.fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Goal.this);
                View addView = layoutInflater.inflate(R.layout.finishgoal,null);
                new AlertDialog.Builder(Goal.this)
                        .setTitle("完成目標或作業")
                        .setMessage("完成後請上傳證明圖片，\n若未上傳則顯示(!)。")
                        .setView(addView)
                        .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText goal = addView.findViewById(R.id.goal);
                                if("".equals(goal.getText().toString().trim())) {
                                    Toast.makeText(Goal.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    try {
                                        if(Long.parseLong(goal.getText().toString())<10) {
                                            db.collection(email)
                                                    .document("goal0"+goal.getText().toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            try {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                if(documentSnapshot.getString("finish").equals("T")) {
                                                                    Toast.makeText(Goal.this,"目標已完成", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else {
                                                                    db.collection(email)
                                                                            .document("goal0"+goal.getText().toString())
                                                                            .update("finish","pic")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    update(email);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                            catch(Exception e) {
                                                                Toast.makeText(Goal.this,"不存在此資料", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else {
                                            db.collection(email)
                                                    .document("goal"+goal.getText().toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            try {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                if(documentSnapshot.getString("finish").equals("T")) {
                                                                    Toast.makeText(Goal.this,"目標已完成", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else {

                                                                    db.collection(email)
                                                                            .document("goal"+goal.getText().toString())
                                                                            .update("finish","pic")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    update(email);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                            catch(Exception e) {
                                                                Toast.makeText(Goal.this,"不存在此資料", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(Goal.this,"請輸入正確格式",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
        nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.goal);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.goal:
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            Uri uri = data.getData();
            storageRef.child("goal").child(mAuth.getCurrentUser().getEmail().toString()).child("goal"+requestCode+".png")
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Goal.this,"上傳成功",Toast.LENGTH_SHORT).show();
                            if(requestCode<10) {
                                db.collection(mAuth.getCurrentUser().getEmail())
                                        .document("goal0"+requestCode)
                                        .update("finish","T")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                update(mAuth.getCurrentUser().getEmail());
                                            }
                                        });
                                db.collection(mAuth.getCurrentUser().getEmail())
                                        .document("goal0"+requestCode)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                Long yygoal = documentSnapshot.getLong("yy"); //2023
                                                Long MMgoal = documentSnapshot.getLong("MM"); //01
                                                Long ddgoal = documentSnapshot.getLong("dd"); //31
                                                Long gap;
                                                if(MMgoal!=MM) {
                                                    if(MM==1|MM==3|MM==5|MM==7|MM==8|MM==10|MM==12) {
                                                        gap=31-dd+ddgoal;
                                                        points(gap);
                                                    }
                                                    else if(MM==4|MM==6|MM==9|MM==11) {
                                                        gap=30-dd+ddgoal;
                                                        points(gap);
                                                    }
                                                    else {
                                                        if(yy%4==0) {
                                                            gap=29-dd+ddgoal;
                                                            points(gap);
                                                        }
                                                        else {
                                                            gap=28-dd+ddgoal;
                                                            points(gap);
                                                        }
                                                    }
                                                }
                                                else if(MMgoal==MM) {
                                                    gap=ddgoal-dd;
                                                    points(gap);
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                db.collection(mAuth.getCurrentUser().getEmail())
                                        .document("goal"+requestCode)
                                        .update("finish","T")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                update(mAuth.getCurrentUser().getEmail());
                                            }
                                        });
                                db.collection(mAuth.getCurrentUser().getEmail())
                                        .document("goal"+requestCode)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                Long yygoal = documentSnapshot.getLong("yy");
                                                Long MMgoal = documentSnapshot.getLong("MM");
                                                Long ddgoal = documentSnapshot.getLong("dd");
                                                Long gap;
                                                if(MMgoal!=MM) {
                                                    if(MM==1|MM==3|MM==5|MM==7|MM==8|MM==10|MM==12) {
                                                        gap=31-dd+ddgoal;
                                                        points(gap);
                                                    }
                                                    else if(MM==4|MM==6|MM==9|MM==11) {
                                                        gap=30-dd+ddgoal;
                                                        points(gap);
                                                    }
                                                    else {
                                                        if(yy%4==0) {
                                                            gap=29-dd+ddgoal;
                                                            points(gap);
                                                        }
                                                        else {
                                                            gap=28-dd+ddgoal;
                                                            points(gap);
                                                        }
                                                    }
                                                }
                                                else if(MMgoal==MM) {
                                                    gap=ddgoal-dd;
                                                    points(gap);
                                                }

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Goal.this,"上傳失敗",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if (resultCode==RESULT_CANCELED)
        {
            Toast.makeText(Goal.this,"上傳失敗",Toast.LENGTH_SHORT).show();
        }
    }
    void points(Long gap)
    {
        db.collection(mAuth.getCurrentUser().getEmail())
                .document("profile")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Long points = documentSnapshot.getLong("Points");
                        if(gap>=21) {
                            db.collection(mAuth.getCurrentUser().getEmail())
                                    .document("profile")
                                    .update("Points",points+10);
                        }
                        else if (gap<21&gap>=14) {
                            db.collection(mAuth.getCurrentUser().getEmail())
                                    .document("profile")
                                    .update("Points",points+7);
                        }
                        else if(gap<14&gap>=7) {
                            db.collection(mAuth.getCurrentUser().getEmail())
                                    .document("profile")
                                    .update("Points",points+5);
                        }
                        else if(gap<7&gap>=4) {
                            db.collection(mAuth.getCurrentUser().getEmail())
                                    .document("profile")
                                    .update("Points",points+3);
                        }
                        else {
                            db.collection(mAuth.getCurrentUser().getEmail())
                                    .document("profile")
                                    .update("Points",points+1);
                        }
                    }
                });

    }
    void check(String email)
    {
        db.collection(email)
                .whereEqualTo("finish","pic")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                new AlertDialog.Builder(Goal.this)
                                        .setTitle("上傳圖片")
                                        .setMessage("請上傳目標NO."+doc.getLong("glcnt")+"的完成圖片")
                                        .setPositiveButton("上傳", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(doc.getLong("glcnt")<10) {
                                                    db.collection(email)
                                                            .document("goal0"+doc.getLong("glcnt"))
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                                                                    picker.setType("image/*");
                                                                    Integer requestCode = doc.getLong("glcnt").intValue();
                                                                    startActivityForResult(picker,requestCode);
                                                                }
                                                            });
                                                }
                                                else {
                                                    db.collection(email)
                                                            .document("goal"+doc.getLong("glcnt"))
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                                                                    picker.setType("image/*");
                                                                    Integer requestCode = doc.getLong("glcnt").intValue();
                                                                    startActivityForResult(picker,requestCode);
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
                        }
                    }
                });
    }
    void update(String email) {
        ArrayList<String> yygoal = new ArrayList<>();
        ArrayList<String> MMgoal = new ArrayList<>();
        ArrayList<String> ddgoal = new ArrayList<>();
        ArrayList<String> goal = new ArrayList<>();
        ArrayList<String> finish = new ArrayList<>();
        ArrayList<String> glcnt = new ArrayList<>();
        db.collection(email)
                .whereNotEqualTo("finish","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                if((doc.getLong("yy").toString().equals(yy.toString())&doc.getLong("MM")<MM)
                                ||(doc.getLong("MM")==MM&doc.getLong("dd")<dd)
                                ||(doc.getLong("yy")<yy))
                                {
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
                                    yygoal.add(doc.getLong("yy").toString());
                                    MMgoal.add(doc.getLong("MM").toString());
                                    ddgoal.add(doc.getLong("dd").toString());
                                    finish.add(doc.getString("finish"));
                                    goal.add(doc.getString("goal"));
                                    glcnt.add(doc.getLong("glcnt").toString());
                                    Adapter2 adapter = new Adapter2(Goal.this,yygoal.toArray(new String[0]),MMgoal.toArray(new String[0])
                                            ,ddgoal.toArray(new String[0]),finish.toArray(new String[0]),goal.toArray(new String[0]),glcnt.toArray(new String[0]));
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(Goal.this));
                                    binding.recyclerView.setAdapter(adapter);
                                }
                            }
                        }
                    }
                });
        check(email);
    }
}
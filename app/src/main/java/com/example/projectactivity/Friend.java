package com.example.projectactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectactivity.databinding.ActivityFriendBinding;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Friend extends AppCompatActivity {
    private BottomNavigationView nav;
    private ActivityFriendBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private long cntA = 0;
    private long cntB = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//隱藏上方Bar
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.orange));//狀態列顏色

        String email = mAuth.getCurrentUser().getEmail();
        update(email);
        request(email);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Friend.this);
                View addView = layoutInflater.inflate(R.layout.friendadd,null);
                new AlertDialog.Builder(Friend.this)
                        .setTitle("新增好友")
                        .setView(addView)
                        .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText useracc = addView.findViewById(R.id.useracc);
                                if( "".equals(useracc.getText().toString().trim())) {
                                    Toast.makeText(Friend.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else if (mAuth.getCurrentUser().getEmail().equals(useracc.getText().toString())) {
                                    Toast.makeText(Friend.this,"無法新增自己", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection(useracc.getText().toString())
                                            .whereEqualTo("request",email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        db.collection(useracc.getText().toString())
                                                                .whereEqualTo("Email",email)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                        if(task.getResult().isEmpty()&task2.getResult().isEmpty()) {
                                                                            db.collection(useracc.getText().toString())
                                                                                    .document("profile")
                                                                                    .get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                            try {
                                                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                                                cntA = documentSnapshot.getLong("RequestCount");
                                                                                                Map<String, Object> request = new HashMap<>();
                                                                                                request.put("request", email);
                                                                                                request.put("rqcnt",cntA);
                                                                                                db.collection(useracc.getText().toString())
                                                                                                        .document("request" + cntA)
                                                                                                        .set(request);
                                                                                                db.collection(useracc.getText().toString())
                                                                                                        .document("profile")
                                                                                                        .update("RequestCount",cntA+1);
                                                                                                Toast.makeText(Friend.this,"已寄出好友邀請",
                                                                                                        Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                            catch (Exception e) {
                                                                                                Toast.makeText(Friend.this,"帳號不存在或資料輸入錯誤",
                                                                                                        Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                        else if(task2.getResult().isEmpty()==false) {
                                                                            Toast.makeText(Friend.this,"已有此人好友", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        else {
                                                                            Toast.makeText(Friend.this,"已寄出好友邀請", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
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

        binding.fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Friend.this);
                View addView = layoutInflater.inflate(R.layout.friendadd,null);
                new AlertDialog.Builder(Friend.this)
                        .setTitle("刪除好友")
                        .setView(addView)
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText useracc = addView.findViewById(R.id.useracc);
                                if( "".equals(useracc.getText().toString().trim())) {
                                    Toast.makeText(Friend.this,"請輸入資料", Toast.LENGTH_SHORT).show();
                                }
                                else if (mAuth.getCurrentUser().getEmail().equals(useracc.getText().toString())) {
                                    Toast.makeText(Friend.this,"無法刪除自己", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection(email)
                                            .whereEqualTo("Email",useracc.getText().toString())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        for(QueryDocumentSnapshot doc:task.getResult()) {
                                                            db.collection(mAuth.getCurrentUser().getEmail())
                                                                    .document("friend"+doc.getLong("frdcnt"))
                                                                    .delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                    db.collection(useracc.getText().toString())
                                            .whereEqualTo("Email",email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        for(QueryDocumentSnapshot doc:task.getResult()) {
                                                            db.collection(useracc.getText().toString())
                                                                    .document("friend"+doc.getLong("frdcnt"))
                                                                    .delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(Friend.this,"刪除成功", Toast.LENGTH_SHORT).show();
                                                                            update(email);
                                                                        }
                                                                    });
                                                        }
                                                    }
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

        nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.friend);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.goal:
                        startActivity(new Intent(getApplicationContext(), Goal.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.friend:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    void request(String useremail)
    {
        db.collection(useremail)
                .whereNotEqualTo("request","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                new AlertDialog.Builder(Friend.this)
                                        .setTitle("好友請求")
                                        .setMessage("要接受"+doc.getString("request")+"的好友請求嗎?")
                                        .setPositiveButton("接受", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                db.collection(useremail)
                                                        .document("profile")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                cntA = documentSnapshot.getLong("FriendCount");
                                                                Map<String, Object> userA = new HashMap<>();
                                                                userA.put("Email", doc.getString("request"));
                                                                userA.put("frdcnt", cntA);
                                                                db.collection(useremail)
                                                                        .document("friend"+cntA)
                                                                        .set(userA);
                                                                db.collection(useremail)
                                                                        .document("profile")
                                                                        .update("FriendCount", cntA + 1);
                                                                db.collection(useremail)
                                                                        .document("request"+doc.getLong("rqcnt"))
                                                                        .delete();
                                                                update(useremail);
                                                            }
                                                        });
                                                db.collection(doc.getString("request"))
                                                        .document("profile")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                cntB = documentSnapshot.getLong("FriendCount");
                                                                Map<String, Object> userB = new HashMap<>();
                                                                userB.put("Email", useremail);
                                                                userB.put("frdcnt", cntB);
                                                                db.collection(doc.getString("request"))
                                                                        .document("friend" + cntB)
                                                                        .set(userB);
                                                                db.collection(doc.getString("request"))
                                                                        .document("profile")
                                                                        .update("FriendCount", cntB + 1);
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("拒絕", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                db.collection(useremail)
                                                        .document("request"+doc.getLong("rqcnt"))
                                                        .delete();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                });
    }
    void update(String useremail)
    {
        ArrayList<String> username = new ArrayList<>();
        ArrayList<String> usermail = new ArrayList<>();
        ArrayList<String> points = new ArrayList<>();
        db.collection(useremail)
                .document("profile")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            username.add(documentSnapshot.getString("Name"));
                            usermail.add(useremail);
                            points.add(documentSnapshot.getLong("Points").toString());
                        }
                    }
                });
        db.collection(useremail)
                .whereNotEqualTo("Email","")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                db.collection(doc.getString("Email"))
                                        .document("profile")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    username.add(documentSnapshot.getString("Name"));
                                                    usermail.add(doc.getString("Email"));
                                                    points.add(documentSnapshot.getLong("Points").toString());
                                                    String[] pointssort = new String[points.size()];
                                                    pointssort = points.toArray(new String[0]);
                                                    String[] usernamesort = new String[points.size()];
                                                    usernamesort = username.toArray(new String[0]);
                                                    String[] usermailsort = new String[points.size()];
                                                    usermailsort = usermail.toArray(new String[0]);
                                                    String[] Rank = new String[points.size()];

                                                    String copy="";
                                                    for (int i=pointssort.length-1;i>0;i--) {
                                                        for (int j=0;j<=i-1;j++) {
                                                            if(Integer.parseInt(pointssort[j])<Integer.parseInt(pointssort[j+1])) {
                                                                copy = pointssort[j];
                                                                pointssort[j]=pointssort[j+1];
                                                                pointssort[j+1]=copy;

                                                                copy = usermailsort[j];
                                                                usermailsort[j]=usermailsort[j+1];
                                                                usermailsort[j+1]=copy;

                                                                copy = usernamesort[j];
                                                                usernamesort[j]=usernamesort[j+1];
                                                                usernamesort[j+1]=copy;
                                                            }
                                                        }
                                                    }
                                                    for(int i=0;i<usermailsort.length;i++) {
                                                        Rank[i]=String.valueOf(i+1);
                                                        if(usermailsort[i]==useremail) {
                                                            db.collection(useremail)
                                                                    .document("profile")
                                                                    .update("Rank",Rank[i]);
                                                        }
                                                    }
                                                    Adapter adapter = new Adapter(Friend.this,usernamesort, usermailsort,pointssort,Rank);
                                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(Friend.this));
                                                    binding.recyclerView.setAdapter(adapter);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
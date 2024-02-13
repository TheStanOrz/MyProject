package com.example.projectactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context mContext;
    private String[] username;
    private String[] usermail;
    private String[] points;
    private String[] Rank;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private StorageReference storageRef;



    public Adapter(Context mContext, String[] username,String[] usermail, String[] points,String[] Rank) {
        this.mContext = mContext;
        this.username = username;
        this.usermail = usermail;
        this.Rank=Rank;
        this.points = points;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.friend,parent,false);
        MyViewHolder holder = new MyViewHolder(itemView);
        holder.textView = itemView.findViewById(R.id.textView);
        holder.textView2 = itemView.findViewById(R.id.textView2);
        holder.textView3 = itemView.findViewById(R.id.textView3);
        holder.textView7 = itemView.findViewById(R.id.textView7);
        holder.imageView = itemView.findViewById(R.id.imageView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        holder.textView.setText(username[position]);
        holder.textView2.setText(points[position]);
        holder.textView3.setText(usermail[position]);
        holder.textView7.setText(Rank[position]);
        storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("user").child(usermail[position]+".png")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(holder.imageView)
                                .load(uri)
                                .into(holder.imageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return username.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textView,textView2,textView3,textView7;
        public ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

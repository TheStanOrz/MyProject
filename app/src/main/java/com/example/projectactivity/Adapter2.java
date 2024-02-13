package com.example.projectactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.util.Observer;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.ViewHolder>{

    private Context mContext;
    private String[] yygoal;
    private String[] MMgoal;
    private String[] ddgoal;
    private String[] finish;
    private String[] goal;
    private String[] glcnt;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Adapter2(Context mContext,String[] yygoal,String[] MMgoal,String[] ddgoal,String[] finish,String[] goal,String[] glcnt) {
        this.mContext = mContext;
        this.yygoal=yygoal;
        this.MMgoal=MMgoal;
        this.ddgoal=ddgoal;
        this.finish=finish;
        this.goal=goal;
        this.glcnt=glcnt;
    }

    @NonNull
    @Override
    public Adapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.goal,parent,false);
        Adapter2.ViewHolder holder = new Adapter2.ViewHolder(itemView);
        holder.goal=itemView.findViewById(R.id.goal);
        holder.date=itemView.findViewById(R.id.date);
        holder.finish=itemView.findViewById(R.id.finish);
        holder.glcnt=itemView.findViewById(R.id.glcnt);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter2.ViewHolder holder, int position) {
        holder.goal.setText(goal[position]);
        holder.date.setText(yygoal[position]+"/"+MMgoal[position]+"/"+ddgoal[position]);
        holder.finish.setText(finish[position]);
        holder.glcnt.setText("NO."+glcnt[position]);
        if(finish[position].equals("T"))
        {
            holder.finish.setText("(✓)");
        }
        else if (finish[position].equals("pic"))
        {
            holder.finish.setText("(!)");
        }
        else if (finish[position].equals("F"))
        {
            holder.finish.setText("(✕)");
        }
    }

    @Override
    public int getItemCount() { return goal.length; }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView goal,date,finish,glcnt;
        public ViewHolder(@NonNull View itemView) { super(itemView); }
    }
}

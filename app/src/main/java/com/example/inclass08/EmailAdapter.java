//Assignment Inclass 08
//File Name: Group12_InClass08
//Sanika Pol
//Snehal Kekane

package com.example.inclass08;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.ViewHolder> {

    public static iEmail emailOps;
    final String TAG = "demo";
    ArrayList<Email> emails;


    public EmailAdapter(ArrayList<Email> emails, iEmail emailOps ) {
        this.emails = emails;
        this.emailOps = emailOps;
        Log.d(TAG,"size of emails =  " + emails.size());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.email,parent,false);
        Log.d(TAG,"view : " + view.getId() + "");
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"Emails size: " + emails.size());
        return emails.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Email email = emails.get(position);
        holder.tv_subject.setText(email.subject);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = outputFormat.format(email.date);
        holder.tv_date.setText(formattedDate);
        holder.email = email;
        Log.d(TAG, "onBindViewHolder: " + holder.email.toString());
    }




    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_subject,tv_date;
        ImageView iv_delete;
        Email email;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.email = email;
            tv_subject = (TextView) itemView.findViewById(R.id.tv_subject);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("demo", "onClick " + getAdapterPosition());
                    emailOps.deleteEmail(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("demo", "onClick " + getAdapterPosition());
                    emailOps.displayEmail(getAdapterPosition());
                }
            });

        }
    }

    public interface iEmail{
        public void deleteEmail(int position);
        public void displayEmail(int position);
    }

}

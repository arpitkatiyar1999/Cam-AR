package com.example.camar.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camar.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.viewHolder>{
    private ArrayList<String> data;
    private Context context;

    public PhotoAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.gallery_item_image,parent,false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String imageUri = data.get(position);
        Picasso
                .get()
                .load(Uri.parse(imageUri)).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            photo=itemView.findViewById(R.id.gallery_image);
        }
    }
}
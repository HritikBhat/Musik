package com.hritik.musik.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hritik.musik.Activities.PlayerActivity;
import com.hritik.musik.R;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{
    private String[] msList;
    private Context context;
    private ArrayList<File> mySongs;


    public MusicAdapter(Context context,String[] msList, ArrayList<File> mySongs) {
        this.msList = msList;
        this.context = context;
        this.mySongs = mySongs;
    }

    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        String songName = msList[position];
        holder.sg_tv.setText(songName);
        holder.sg_tv.setSelected(true);
        holder.ms_cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), PlayerActivity.class);
                intent.putExtra("songs",mySongs);
                intent.putExtra("songName",songName);
                intent.putExtra("pos",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView ms_cd;
        public TextView sg_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            this.ms_cd= itemView.findViewById(R.id.ms_card);
            this.sg_tv= itemView.findViewById(R.id.txtSongName);
        }
    }
}


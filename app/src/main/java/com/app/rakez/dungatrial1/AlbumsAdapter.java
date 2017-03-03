package com.app.rakez.dungatrial1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by RAKEZ on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.person_name);
        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.name.setText(album.getName());
        holder.name.setBackgroundResource(album.getThumbnail());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.getAdapterPosition();
                Bundle info = new Bundle();
                String state = albumList.get(holder.getAdapterPosition()).getState();
                String tableNo = albumList.get(holder.getAdapterPosition()).getName();

                if(state.equals("1")||state.equals("2")){
                    Intent i  = new Intent(mContext.getApplicationContext(),MenuActivity.class);
                    info.putString("tableNo",tableNo);
                    info.putString("state",state);
                    i.putExtras(info);
                    mContext.startActivity(i);
                }

            }
        });


    }



    @Override
    public int getItemCount() {
        return albumList.size();
    }
}

package com.vpnmaster.vpnmasterinc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class PowerModeAdapter extends RecyclerView.Adapter<PowerModeAdapter.MyViewHolder> {

    public List<ItemPower> apps;

    public PowerModeAdapter(List<ItemPower> apps)
    {
        this.apps = apps;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.powe_itemlist_willdev, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ItemPower app= apps.get(position);
        holder.size.setText(app.getText());
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView size;


        public MyViewHolder(View view) {
            super(view);
            size = (TextView) view.findViewById(R.id.items);
        }
    }
}

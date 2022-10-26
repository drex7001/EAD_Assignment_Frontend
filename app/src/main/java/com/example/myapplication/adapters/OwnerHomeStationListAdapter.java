package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ViewShedDataActivity;
import com.example.myapplication.models.FuelStation;
import com.google.gson.Gson;

import java.util.ArrayList;

public class OwnerHomeStationListAdapter extends RecyclerView.Adapter<OwnerHomeStationListAdapter.ViewHolder> {
    private ArrayList<FuelStation> fuelStationArrayList;
    private Context context;

    public OwnerHomeStationListAdapter(ArrayList<FuelStation> fuelModalArrayList, Context context) {
        this.fuelStationArrayList = fuelModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shed_owner_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerHomeStationListAdapter.ViewHolder holder, int position) {
        FuelStation fuelStation = fuelStationArrayList.get(position);
        holder.stationName.setText(fuelStation.getName());
        holder.stationAddress.setText(fuelStation.getAddress());

        Gson gson = new Gson();
        String fuelStationString = gson.toJson(fuelStation);

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ViewShedDataActivity.class);
                intent.putExtra("FUEL_STATION_DATA", fuelStationString);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fuelStationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView stationName, stationAddress;
        private Button viewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.owner_home_viewStation_station_name);
            stationAddress = itemView.findViewById(R.id.owner_home_viewStation_station_address);
            viewBtn = itemView.findViewById(R.id.owner_home_viewStation_btn);
        }
    }
}


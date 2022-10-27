package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Fuel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class OwnerViewFuelListAdapter extends RecyclerView.Adapter<OwnerViewFuelListAdapter.ViewHolder> {

    private ArrayList<Fuel> fuelArrayList;
    private Context context;

    public OwnerViewFuelListAdapter(ArrayList<Fuel> fuelModalArrayList, Context context) {
        this.fuelArrayList = fuelModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shed_owner_view_shed_fuel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerViewFuelListAdapter.ViewHolder holder, int position) {
        Fuel fuelData = fuelArrayList.get(position);
        holder.fuelName.setText("Fuel Name: "+fuelData.getName());
        holder.fuelType.setText("Fuel Type: "+fuelData.getType());

        String dateTimeUTC = fuelData.getArrivalTime();
        String[] parts = dateTimeUTC.split("T");
        String timeRem = parts[1].toString();
        String[] parts2 = timeRem.split("\\.");
        String time = parts2[0];
        String datetime = parts[0] +":"+ time;

        holder.arrivalTime.setText("Fuel Arrival Time: "+datetime);

        String fuelAmount = "Available Amount: "+fuelData.getAmount() + " L";
        holder.fuelAmount.setText(fuelAmount);

        Map<String, String> vehicleCountList = fuelData.getVehicleCountList();

        String vehicleListCount = "Q Completed:"+vehicleCountList.get("QCompleted")+"\n"+ "Q Left:"+ vehicleCountList.get("QLeft")+"\n"+"IN Q:"+ vehicleCountList.get("QIn");
        holder.vehicleCount.setText(vehicleListCount);

//        Gson gson = new Gson();
//        String fuelStationString = gson.toJson(fuelData);

//        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Intent intent = new Intent(context, ViewShedDataActivity.class);
//                intent.putExtra("FUEL_STATION_DATA", fuelStationString);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return fuelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fuelName, fuelType, fuelAmount, arrivalTime, vehicleCount;
        private Button updateBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fuelName = itemView.findViewById(R.id.viewStation_fuel_name);
            fuelType = itemView.findViewById(R.id.viewStation_fuel_type);
            fuelAmount = itemView.findViewById(R.id.viewStation_fuel_amount);
            arrivalTime = itemView.findViewById(R.id.viewStation_fuel_arrivalTime);
            vehicleCount = itemView.findViewById(R.id.viewStation_fuel_vehicle_count);
            updateBtn = itemView.findViewById(R.id.update_fuel_finish_time_btn);
        }
    }
}

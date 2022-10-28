package com.example.myapplication.adapters;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_ID_KEY;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.NukeSSLCerts;
import com.example.myapplication.OwnerAddNewShed;
import com.example.myapplication.OwnerHomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.ViewShedDataActivity;
import com.example.myapplication.models.Fuel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
//                Intent intent = new Intent(context, ViewShedDataActivity.class);
//                context.startActivity(intent);

                //update status
                updateData(fuelData.getId());
            }
        });
    }

    private void updateData(String fuelId) {
        NukeSSLCerts.nuke();
        String UPDATE_FUEL_STATUS = BACKEND_URI+"fuel/finish/"+fuelId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, UPDATE_FUEL_STATUS, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show();
                try {
                    boolean resultUpdate = response.getBoolean("success");
                    Log.i("Updated Fuel Status", "Updated!");
                    dialogBox("Success!", "Fuel Status Updated Successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialogBox("Error!", "Please try again later!");
            }
        });
        requestQueue.add(request);
    }

    public void dialogBox(String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(type);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(context, OwnerHomeActivity.class);
                        context.startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

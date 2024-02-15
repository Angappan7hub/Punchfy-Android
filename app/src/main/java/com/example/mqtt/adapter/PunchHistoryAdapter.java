package com.example.mqtt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mqtt.R;
import com.example.mqtt.ara.AraHomeActivity;
import com.example.mqtt.ara.model.EmpLog;
import com.example.mqtt.dependency.ItemChoiceListener;

import java.util.List;

public class PunchHistoryAdapter extends RecyclerView.Adapter<PunchHistoryAdapter.PunchHistoryViewHolder> {
    List<EmpLog> empLogs;
    AraHomeActivity context;
    ItemChoiceListener itemChoiceListener;


    public PunchHistoryAdapter(AraHomeActivity context, List<EmpLog> empLogs, ItemChoiceListener itemChoiceListener) {
        this.context = context;
        this.empLogs = empLogs;
        this.itemChoiceListener = itemChoiceListener;

    }


    @NonNull
    @Override
    public PunchHistoryAdapter.PunchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_punch_history,parent,false);
        return new PunchHistoryAdapter.PunchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PunchHistoryAdapter.PunchHistoryViewHolder holder, int position) {
           EmpLog empLog=empLogs.get(position);
           holder.title.setText(empLog.time);
           holder.address.setText(empLog.address);
    }

    @Override
    public int getItemCount() {
        return empLogs.size();
    }

    public class PunchHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView address;
        CardView cardView;
        public PunchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewReferFields(itemView);
        }
        private void viewReferFields(View itemView) {
            title=itemView.findViewById(R.id.item_punch_his_title);
            address=itemView.findViewById(R.id.item_punch_his_address);
        }
    }


}

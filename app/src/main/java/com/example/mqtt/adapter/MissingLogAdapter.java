package com.example.mqtt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mqtt.R;
import com.example.mqtt.dependency.ItemChoiceListener;
import com.example.mqtt.model.SampleData;

import org.w3c.dom.Text;

import java.util.List;

public class MissingLogAdapter extends RecyclerView.Adapter<MissingLogAdapter.MissingLogViewHolder> {

    List<SampleData> attendanceLists;
    Fragment context;
    ItemChoiceListener itemChoiceListener;


    public MissingLogAdapter(Fragment context, List<SampleData> attendanceLists, ItemChoiceListener itemChoiceListener) {
        this.context = context;
        this.attendanceLists = attendanceLists;
        this.itemChoiceListener = itemChoiceListener;

    }


    @NonNull
    @Override
    public MissingLogAdapter.MissingLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_missing_log_list,parent,false);
        return new MissingLogAdapter.MissingLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissingLogAdapter.MissingLogViewHolder holder, int position) {
           SampleData sampleData=attendanceLists.get(position);
           String lat=String.valueOf(sampleData.latitude);
           String lon=String.valueOf(sampleData.longitude);
           holder.title.setText(sampleData.datetime);
           holder.address.setText(sampleData.address);

           holder.arrowBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   itemChoiceListener.onItemChoosed(sampleData, position);
               }
           });
    }

    @Override
    public int getItemCount() {
        return attendanceLists.size();
    }

    public class MissingLogViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView address;
        CardView cardView;
        ImageButton arrowBtn;

        public MissingLogViewHolder(@NonNull View itemView) {
            super(itemView);
            viewReferFields(itemView);
        }

        private void viewReferFields(View itemView) {
            title=itemView.findViewById(R.id.item_missing_log_title);
            address=itemView.findViewById(R.id.item_missing_log_address);
            cardView=itemView.findViewById(R.id.item_missing_log_card);
            arrowBtn=itemView.findViewById(R.id.item_missing_log_arrow_btn);
        }
    }
}

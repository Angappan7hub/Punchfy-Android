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
import com.example.mqtt.model.History;
import com.example.mqtt.model.SampleData;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    List<History> historyList;
    Fragment context;
    ItemChoiceListener itemChoiceListener;


    public HistoryAdapter(Fragment context, List<History> historyList, ItemChoiceListener itemChoiceListener) {
        this.context = context;
        this.historyList = historyList;
        this.itemChoiceListener = itemChoiceListener;

    }


    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
           History history=historyList.get(position);

           holder.title.setText(history.date);


//           holder.arrowBtn.setOnClickListener(new View.OnClickListener() {
//               @Override
//               public void onClick(View view) {
//                   itemChoiceListener.onItemChoosed(sampleData, position);
//               }
//           });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView address;
        CardView cardView;
        ImageButton arrowBtn;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewReferFields(itemView);
        }

        private void viewReferFields(View itemView) {
            title=itemView.findViewById(R.id.item_history_title);
//            address=itemView.findViewById(R.id.item_missing_log_address);
//            cardView=itemView.findViewById(R.id.item_missing_log_card);
//            arrowBtn=itemView.findViewById(R.id.item_missing_log_arrow_btn);
        }
    }
}

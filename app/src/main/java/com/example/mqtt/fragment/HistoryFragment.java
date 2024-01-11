package com.example.mqtt.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mqtt.MainActivity;
import com.example.mqtt.R;
import com.example.mqtt.ViewModel.MainViewModel;
import com.example.mqtt.adapter.HistoryAdapter;
import com.example.mqtt.adapter.MissingLogAdapter;
import com.example.mqtt.dependency.ItemChoiceListener;
import com.example.mqtt.dependency.SessionManager;
import com.example.mqtt.model.History;
import com.example.mqtt.model.SampleData;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment implements ItemChoiceListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    HistoryAdapter missingLogAdapter;
    MainViewModel mainViewModel;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_history, container, false);
        inject();
        referViewFields(view);
        setData();
        return view;
    }

    private void setData() {
        progressBar.setVisibility(View.VISIBLE);
        List<History> historyList=new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        History history=new History();
        history.date=currentDateAndTime;
        historyList.add(history);
        if(historyList!=null && historyList.size()!=0){
            updateRecyclerView(historyList);
//            Gson gson1=new Gson();
//            String jsonData1=gson1.toJson(historyList);
//            System.out.print("JsonData "+jsonData1);
//            Log.i("json",jsonData1);

        }
        else {
            if(historyList!=null) {
               historyList.clear();
            }
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }
    private void inject() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    private void updateRecyclerView(List<History> historyList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        missingLogAdapter = new HistoryAdapter(this, historyList, this);
        recyclerView.setAdapter(missingLogAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("History");
    }

    private void referViewFields(View view) {
        recyclerView=view.findViewById(R.id.frag_history_recycle);
        progressBar=view.findViewById(R.id.frag_history_progress_bar);

    }

    @Override
    public void onItemChoosed(Object selectedItem, int position) {
    }
}
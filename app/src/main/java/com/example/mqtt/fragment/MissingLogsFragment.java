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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mqtt.MainActivity;
import com.example.mqtt.R;
import com.example.mqtt.ViewModel.MainViewModel;
import com.example.mqtt.adapter.MissingLogAdapter;
import com.example.mqtt.dependency.ItemChoiceListener;
import com.example.mqtt.dependency.SessionManager;
import com.example.mqtt.model.SampleData;
import com.google.gson.Gson;

import java.util.List;

public class MissingLogsFragment extends Fragment implements ItemChoiceListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    MissingLogAdapter missingLogAdapter;
    SwipeRefreshLayout rootLayout;
    MainViewModel mainViewModel;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_missing_logs, container, false);
        inject();
        referViewFields(view);
        setData();
        setListener();
        return view;
    }

    private void setListener() {
        rootLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            setData();
            }
        });

    }

    private void setData() {
        progressBar.setVisibility(View.VISIBLE);
        SessionManager sessionManager= new SessionManager(getContext());
        List<SampleData> sampleDataList= sessionManager.getData();
        if(sampleDataList!=null && sampleDataList.size()!=0){
            updateRecyclerView(sampleDataList);
            Gson gson1=new Gson();
            String jsonData1=gson1.toJson(sampleDataList);
            System.out.print("JsonData "+jsonData1);
            Log.i("json",jsonData1);

        }
        else {
            sampleDataList.clear();
            rootLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }
    private void inject() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    private void updateRecyclerView(List<SampleData> sampleDataList) {
        rootLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        missingLogAdapter = new MissingLogAdapter(this, sampleDataList, this);
        recyclerView.setAdapter(missingLogAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Missing Logs");
    }

    private void referViewFields(View view) {
        recyclerView=view.findViewById(R.id.frag_missing_log_recycle);
        progressBar=view.findViewById(R.id.frag_missing_log_progress_bar);
        rootLayout = view.findViewById(R.id.root_layout);
    }

    @Override
    public void onItemChoosed(Object selectedItem, int position) {
        SampleData selectedData=(SampleData) selectedItem;
        mainViewModel.sampleData=selectedData;
        navController.navigate(R.id.markAttendanceFragment);

    }
}
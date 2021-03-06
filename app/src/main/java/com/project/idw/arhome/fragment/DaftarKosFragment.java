package com.project.idw.arhome.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.project.idw.arhome.R;
import com.project.idw.arhome.adapter.DaftarKosAdapter;
import com.project.idw.arhome.model.Kos;
import com.project.idw.arhome.response.ResponseKoses;
import com.project.idw.arhome.rest.ApiClient;
import com.project.idw.arhome.rest.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarKosFragment extends Fragment {
    private RecyclerView recyclerView;
    private DaftarKosAdapter adapter;
    private ArrayList<Kos> KosArrayList = new ArrayList<>();
    public static final String ARG_STATUS = "status";
    private ShimmerFrameLayout mShimmerViewContainer;
    ImageView iv_kos_kosong;

    ApiInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG = DaftarKosFragment.class.getSimpleName();
    private String status;

    //membuat fungsi menambahkan fragment di activity
    public static DaftarKosFragment newInstance(String status){
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);

        DaftarKosFragment fragment = new DaftarKosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DaftarKosFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null){
            status = extras.getString(ARG_STATUS);
        }

        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daftar_kos, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        iv_kos_kosong = view.findViewById(R.id.iv_kos_kosong);

        getKos(status);
        initView(view);
        return view;
    }

    //fungsi saat layar diswipe keatas
    private void initView(View view) {
        swipeRefreshLayout      = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);

        refresh();

    }

    private void refresh() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        KosArrayList.clear();
                        getKos(status);
                    }
                }, 1000);
            }
        });

    }

    private void getKos(String status) {
        if (status.equalsIgnoreCase("Kos")){
            apiService.kosGetAll().enqueue(new Callback<ResponseKoses>() {
                @Override
                public void onResponse(Call<ResponseKoses> call, Response<ResponseKoses> response) {
                    if (response.isSuccessful()){

                        System.out.println("responseSize"+response.body().getMaster().size());
                        System.out.println("responseIn"+response);

                        if (response.body().getMaster().size()>0){
                            KosArrayList.addAll(response.body().getMaster());
                            System.out.println(response.body().getMaster().get(0).getNama());

                            adapter = new DaftarKosAdapter(getContext(),KosArrayList);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                        }else {
                            Toast.makeText(getContext(), "Data Kos Kosong", Toast.LENGTH_SHORT).show();
                            iv_kos_kosong.setVisibility(View.VISIBLE);
                        }

                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);


                    }else{
                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseKoses> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();

    }


}

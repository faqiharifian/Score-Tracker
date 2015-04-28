package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MakulMhsFragment extends Fragment {

    private ArrayAdapter<String> mMakulAdapter;
    public MakulMhsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_mhs,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar Item clicks here, the action bar will
        // automatically handle clicks on the Home/up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        if (id == com.digit.safian.scoretracker.R.id.action_refresh){
            updateMakulMhs();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMakulMhs(){
        FetchMakulMhsTask makulTask = new FetchMakulMhsTask(getActivity());
        makulTask.execute();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMakulMhs();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mhs, container, false);
        mMakulAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_makul,
                        R.id.list_item_makul_textview,
                        new ArrayList<String>()
                );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_makul_mhs);
        listView.setAdapter(mMakulAdapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mMakulAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });*/
        /*String[] makulArray = {
                "Proyek Perangkat Lunak",
                "Analisis dan Perancangan Sistem Informasi",
                "Keamanan Jaringan",
                "Data Warehouse",
                "Pemrogramman Konkuren",
                "Metode Tangkas Perangkat Lunak",
                "Topik Khusus",
                "Sistem Temu Balik Informasi"
        };

        List<String> daftarMakul = new ArrayList<String>(
                Arrays.asList(makulArray)
        );
        mMakulAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_makul,
                R.id.list_item_makul_textview,
                daftarMakul
        );
        ListView listView = (ListView) rootView.findViewById(R.id.listview_makul_mhs);
        listView.setAdapter(mMakulAdapter);*/
        return rootView;
    }
}
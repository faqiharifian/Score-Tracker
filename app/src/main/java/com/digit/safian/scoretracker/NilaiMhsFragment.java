package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by faqih_000 on 4/28/2015.
 */
public class NilaiMhsFragment extends Fragment {

    public NilaiMhsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nilai_mhs, container, false);
        return rootView;
    }

    private void updateMakulMhs(){
        FetchNilaiMhsTask nilaiTask = new FetchNilaiMhsTask(getActivity());
        nilaiTask.execute("8");
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMakulMhs();
    }
}

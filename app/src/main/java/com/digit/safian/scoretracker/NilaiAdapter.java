package com.digit.safian.scoretracker;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by faqih_000 on 4/29/2015.
 */
public class NilaiAdapter extends ArrayAdapter<Cursor> {
    private int position;
    private int countNew;
    private int countBind;
    private boolean firstView = true;
    private boolean firstBind = true;
    private LayoutInflater mInflater;
    private Set<String> header = new HashSet<String>();
    public NilaiAdapter(Context context){
        super(context, R.layout.container_table_header);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        countNew = 0;
        position = 0;
        countBind = 0;
        header.add("Nama");

        Log.v("NilaiAdapter ", "created");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = mInflater.inflate(R.layout.container_table_content, parent, false);
        String name = getItem(0).getString(getItem(0).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA));
        TextView nameView = (TextView) view.findViewById(R.id.nama);
        TextView utsView = (TextView) view.findViewById(R.id.uts);
        TextView uasView = (TextView) view.findViewById(R.id.uas);
        nameView.setText(name);
        for(int i = 0; i<this.getCount(); i++){
            if(i == 0){
                utsView.setText(getItem(i).getString(getItem(i).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
            }else{
                uasView.setText(getItem(i).getString(getItem(i).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
            }
        }
        return view;
    }
    public Map<String, String> convertCursor(Cursor c){
        Map<String, String> content = new HashMap<>();
        content.put(ScoreContract.NilaiEntry.COLUMN_MAHASISWA, c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA)));
        content.put(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)), c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
        header.add(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
        return content;
    }


    public static class ViewHolder {
        public final TextView nameView;
        public final TextView utsView;
        public final TextView uasView;


        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.nama);
            utsView = (TextView) view.findViewById(R.id.uts);
            uasView = (TextView) view.findViewById(R.id.uas);
        }
    }

    public void setData(List<Cursor> data){
        clear();
        if(data != null){
            for(int i=0; i<data.size(); i++){
                add(data.get(i));
            }
        }
    }
}

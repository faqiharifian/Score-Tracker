package com.digit.safian.scoretracker;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by faqih_000 on 4/29/2015.
 */
public class NilaiAdapter extends ArrayAdapter<Map<String, String>> {

    private LayoutInflater mInflater;
    public NilaiAdapter(Context context){
        super(context, R.layout.container_table_header);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = mInflater.inflate(R.layout.container_table_content, parent, false);
        TableRow header = (TableRow) view.findViewById(R.id.content);

        final float scale = getContext().getResources().getDisplayMetrics().density;

        Map<String, String> item = getItem(position);
        Set<String> keys = item.keySet();

        TextView nameView = new TextView(getContext());
        String name = formatName(item.get("nama"));
        nameView.setText(name);
        nameView.setGravity(Gravity.CENTER);

        header.addView(nameView);
        nameView.setWidth((int) (150*scale));
        nameView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        for(String key : keys){
            if(!key.equals("nama")){
                TextView tv = new TextView(getContext());
                tv.setText(item.get(key));
                tv.setGravity(Gravity.CENTER);
                tv.setWidth((int) (75*scale));

                header.addView(tv);
                tv.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }

        return view;
    }
    public String formatName(String fullName){
        String[] arrayName = fullName.split(" ");
        String formatedName = arrayName[0];
        for(int i = 1; i<arrayName.length; i++){
            String firstChar = arrayName[i].substring(0,1);
            if(!firstChar.equals("(")){
                formatedName += " " + firstChar + ".";
            }
        }
        return formatedName;
    }

    public void setData(List<Cursor> data){
        clear();
        Map<String, String> row;
        if(data != null){
            if(!data.isEmpty()){
                for(int i =0; i<data.get(0).getCount(); i++){
                    row = new HashMap<>();
                    data.get(0).moveToPosition(i);
                    String nama = data.get(0).getString(data.get(0).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA));
                    row.put("nama", nama);

                    for(int j=0; j<data.size(); j++){
                        data.get(j).moveToPosition(i);
                        String judul = data.get(j).getString(data.get(j).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL));
                        String nilai = data.get(j).getString(data.get(j).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI));
                        row.put(judul, nilai);

                    }
                    add(row);
                }
            }
        }

    }
}

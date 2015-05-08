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
public class NilaiAdapter extends ArrayAdapter<Map<String, String>> {
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
        //String name = getItem(0).moveToPosition(position).getString(getItem(0).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA));
        /*TextView nameView = (TextView) view.findViewById(R.id.nama);
        TextView utsView = (TextView) view.findViewById(R.id.uts);
        TextView uasView = (TextView) view.findViewById(R.id.uas);
        nameView.setText("something"+position);
        for(int i = 0; i<this.getCount(); i++){
            getItem(i).moveToPosition(position);
            if(i == 0){
                utsView.setText(getItem(i).getString(getItem(i).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
            }else{
                uasView.setText(getItem(i).getString(getItem(i).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
            }
        }*/
        TextView nameView = (TextView) view.findViewById(R.id.nama);
        TextView utsView = (TextView) view.findViewById(R.id.uts);
        TextView uasView = (TextView) view.findViewById(R.id.uas);
        Map<String, String> item = getItem(position);
        Set<String> keys = item.keySet();
        /*for(String key : keys){
            Log.v(key, item.get(key));
        }*/
        /*Log.v("view nama", getItem(position).g);
        Log.v("view uts", getItem(position).get("uts"));
        Log.v("view uas", getItem(position).get("uts"));*/
        //Log.v("---", "view-------------- ");
        //Log.v("getCount", String.valueOf(this.getCount()));
        nameView.setText(item.get("nama"));
        utsView.setText(item.get("uts"));
        uasView.setText(item.get("uas"));
        /*Bitmap b = null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        b = Bitmap.createBitmap(view.getDrawingCache());
        Canvas c = new Canvas(b);
        view.draw(c);
        c.save();*/
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
        Map<String, String> row;
        if(data != null){
            if(!data.isEmpty()){
                for(int i =0; i<data.get(0).getCount(); i++){
                    row = new HashMap<>();
                    data.get(0).moveToPosition(i);
                    String nama = data.get(0).getString(data.get(0).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA));
                    row.put("nama", nama);

                    //Log.v("nama", nama);
                    for(int j=0; j<data.size(); j++){
                        data.get(j).moveToPosition(i);
                        String judul = data.get(j).getString(data.get(j).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL));
                        String nilai = data.get(j).getString(data.get(j).getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI));
                        //Log.v("data "+judul, nilai);
                        row.put(judul, nilai);

                    }
                    //Log.v("row nama", row.get("nama"));
                    /*Log.v("row uts", row.get("uts"));
                    Log.v("row uas", row.get("uas"));
                    Log.v("a", "data-----------");*/
                    add(row);
                }
            }
        }
        /*for(int i=0; i<getCount(); i++){
            Map<String, String> item = getItem(i);
            Set<String> keys = item.keySet();
            for(String key : keys){
                Log.v(key, item.get(key));
            }
        }
        Log.v("END", "END");*/
    }
}

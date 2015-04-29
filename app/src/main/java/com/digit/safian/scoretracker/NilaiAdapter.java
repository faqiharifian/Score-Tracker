package com.digit.safian.scoretracker;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by faqih_000 on 4/29/2015.
 */
public class NilaiAdapter extends CursorAdapter {
    private int countNew = 0;
    private int countBind = 0;
    private boolean firstView = true;
    private boolean firstBind = true;
    private Set<String> header = new HashSet<String>();
    public NilaiAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        header.add("Nama");
        Log.v("NilaiAdapter ", "created");
    }

    public Map<String, String> convertCursor(Cursor c){
        Map<String, String> content = new HashMap<>();
        content.put(ScoreContract.NilaiEntry.COLUMN_MAHASISWA, c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA)));
        content.put(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)), c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
        header.add(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
        return content;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v("newView: ", String.valueOf(++countNew));
        int layoutId = -1;
        if(firstView){
            Log.v("newView ", "first");
            layoutId = R.layout.container_table_header;
            firstView = false;
        }else{
            Log.v("newView ", "first");
            layoutId = R.layout.container_table_content;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //View view = LayoutInflater.from(context).inflate(R.layout.row_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v("bindView: ", String.valueOf(++countBind));
        /*TableLayout new_view= (TableRow) findViewById(R.id.container);
        Map<String, String> content = convertCursor(cursor);
        Set<String> keys = content.keySet();
        for(String key : keys){
            TextView textview = new TextView(context);
            textview.setText(content.get(key));
            textview.setTextColor(Color.YELLOW);
            new_view.addView(textview);
        }*/
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(firstBind){
            Log.v("bindView ", "first");
            viewHolder.nameView.setText("Nama");
            viewHolder.nameView.setText("Judul");
            viewHolder.nameView.setText("Nilai");
        }else{
            Log.v("bindView ", "first");
            viewHolder.nameView.setText(cursor.getString(0));
            viewHolder.nameView.setText(cursor.getString(1));
            viewHolder.nameView.setText(cursor.getString(2));
        }

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
}

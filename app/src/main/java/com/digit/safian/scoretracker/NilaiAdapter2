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
public class NilaiAdapter2 extends CursorAdapter {
    private int position;
    private int countNew;
    private int countBind;
    private boolean firstView = true;
    private boolean firstBind = true;
    private Set<String> header = new HashSet<String>();
    public NilaiAdapter2(Context context, Cursor c, int flags){
        super(context,c,flags);
        countNew = 0;
        position = 0;
        countBind = 0;
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
        //Log.v("count cursor:", String.valueOf(cursor.getCount()));

        int layoutId = -1;
        /*if(countNew == 0){
            Log.v("newView ", "first");
            Log.v("newView: ", String.valueOf(++countNew));
            layoutId = R.layout.container_table_header;
            firstView = false;
        }else{*/
            Log.v("newView ", "first");
            layoutId = R.layout.container_table_content;
        //}

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //View view = LayoutInflater.from(context).inflate(R.layout.row_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        ++countNew;
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*TableLayout new_view= (TableRow) findViewById(R.id.container);
        Map<String, String> content = convertCursor(cursor);
        Set<String> keys = content.keySet();
        for(String key : keys){
            TextView textview = new TextView(context);
            textview.setText(content.get(key));
            textview.setTextColor(Color.YELLOW);
            new_view.addView(textview);
        }*/
        Log.v("judul", cursor.getString(3));
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        /*if(countBind == 0){
            Log.v("bindView ", "first");
            viewHolder.nameView.setText("Nama");
            viewHolder.utsView.setText("Judul");
            viewHolder.uasView.setText("Nilai");
            firstBind = false;
        }else{*/
        Log.v("bindView ", "not first");
        //if(cursor.getCount() > 0){
            viewHolder.nameView.setText(cursor.getString(cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_MAHASISWA)));
            viewHolder.uasView.setText(cursor.getString(cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_NILAI)));
            viewHolder.utsView.setText(cursor.getString(cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));

        //}
            Log.v("cursor position", String.valueOf(cursor.getPosition()));



        //}
        ++countBind;

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

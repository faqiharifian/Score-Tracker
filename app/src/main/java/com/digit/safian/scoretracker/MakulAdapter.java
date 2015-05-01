package com.digit.safian.scoretracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;

/**
 * Created by faqih_000 on 4/28/2015.
 */

public class MakulAdapter extends CursorAdapter{

    public MakulAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(ScoreContract.MakulEntry.COLUMN_NAMA_MAKUL));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_makul, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view;
        tv.setId(cursor.getInt(cursor.getColumnIndex(ScoreContract.MakulEntry.COLUMN_ID_MAKUL)));
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}

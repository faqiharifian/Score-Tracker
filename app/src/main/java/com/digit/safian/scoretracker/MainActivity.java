package com.digit.safian.scoretracker;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView usernameTextView = (TextView) findViewById(R.id.username);
                TextView passwordTextView = (TextView) findViewById(R.id.password);
                final String username = usernameTextView.getText().toString();
                final String password = passwordTextView.getText().toString();
                Date now = new Date();
                try {
                    //Integer yearIn = Integer.parseInt(username.substring(6,8));
                    //Integer yearNow = Integer.parseInt(new SimpleDateFormat("yy").format(now));
                    //Integer smtNow = yearNow-yearIn;
                    //Log.v(LOG_TAG, "semester sekarang: "+smtNow);
                    //if(smtNow > 0){
                        String result = check(username, password);
                        if(result == "dosen"){
                            Intent intent = new Intent(MainActivity.this, DosenActivity.class);
                            startActivity(intent);
                        }else if(result == "mhs"){
                            Intent intent = new Intent(MainActivity.this, MhsActivity.class);
                            startActivity(intent);

                        //}
                    }
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment{
        public PlaceholderFragment(){}
    }

    private String check(String username, String password){
        if(username.contains("240103")){
            return "mhs";
        }else{
            return "dosen";
        }
    }
}

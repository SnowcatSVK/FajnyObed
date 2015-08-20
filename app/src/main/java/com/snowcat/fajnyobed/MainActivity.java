package com.snowcat.fajnyobed;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.snowcat.fajnyobed.io.SecureDataClient;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =(TextView) findViewById(R.id.textview);
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

    @Override
    protected void onResume() {
        super.onResume();
        /*{"function":"GetRestaurantDetail","restaurant_id":"4884"}*/
        new AsyncTask<Void, Void, Void>() {
            SecureDataClient client = new SecureDataClient();
            JSONObject request = new JSONObject();

            @Override
            protected Void doInBackground(Void... params) {
                String response = null;
                try {
                    request.put("function", "GetRestaurantDetail");
                    request.put("restaurant_id", "4884");
                    response = client.createRetriever().execute(request);
                    JSONObject jsonObject = new JSONObject(response);
                    textView.setText(jsonObject.getString("name"));
                    Log.e("response", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}

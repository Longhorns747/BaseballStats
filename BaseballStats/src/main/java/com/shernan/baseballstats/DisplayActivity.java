package com.shernan.baseballstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.shernan.baseballstats.utils.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    private static String read_db_url = "http://54.213.67.121/db_read.php";

    ArrayList<HashMap<String, String>> statsList;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STATS = "stats";
    private static final String TAG_YEAR = "yearID";
    private static final String TAG_AVG = "AVG";
    private static final String TAG_SLG = "SLG";

    //Column information arrays
    private static final String[] COLUMN_TAGS = {TAG_YEAR, TAG_AVG, TAG_SLG};
    private static final String[] COLUMN_NAMES = {"Year", "AVG", "SLG"};

    //Player name from the input screen
    String inputFName;
    String inputLName;

    // stats JSONArray
    JSONArray stats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //Getting the name of the player from query screen
        Intent queryIntent = getIntent();
        inputFName = queryIntent.getStringExtra(QueryActivity.PLAYER_FIRST_NAME);
        inputLName = queryIntent.getStringExtra(QueryActivity.PLAYER_LAST_NAME);

        statsList = new ArrayList<HashMap<String, String>>();

        new LoadStats().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    /**
     * Background Async Task to Load all stats by making HTTP Request
     * */
    class LoadStats extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayActivity.this);
            pDialog.setMessage("Loading stats. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting stats
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("firstName", inputFName));
            params.add(new BasicNameValuePair("lastName", inputLName));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(read_db_url, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Stats: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Stats found
                    // Getting Array of stats
                    stats = json.getJSONArray(TAG_STATS);

                    // looping through stats
                    for (int i = 0; i < stats.length(); i++) {
                        JSONObject jsonStats = stats.getJSONObject(i);
                        HashMap<String, String> mapStats = new HashMap<String, String>();

                        // Storing each json item in variable
                        for(String tag: COLUMN_TAGS){
                            mapStats.put(tag, jsonStats.getString(tag));
                        }

                        // adding HashList to ArrayList
                        statsList.add(mapStats);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Put JSON data into table format
                     * */
                    TableLayout table = new TableLayout(DisplayActivity.this);
                    table.setBackgroundResource(getResources().getIdentifier("blankbg" , "drawable", getPackageName()));

                    //Set up name header
                    TableRow tableHeader = new TableRow(DisplayActivity.this);
                    tableHeader.setGravity(Gravity.CENTER);

                    TextView fName = createColumn(inputFName, 20, Color.BLACK);
                    fName.setPadding(0, 0, 10, 0);

                    TextView lName = createColumn(inputLName, 20, Color.BLACK);
                    tableHeader.addView(fName);
                    tableHeader.addView(lName);

                    table.addView(tableHeader);

                    //Set up table header
                    TableRow statsHeader = new TableRow(DisplayActivity.this);
                    statsHeader.setGravity(Gravity.CENTER);

                    for(int i = 0; i < COLUMN_NAMES.length; i++){
                        TextView header = createColumn(COLUMN_NAMES[i], 20, Color.BLACK);
                        header.setGravity(Gravity.CENTER);

                        if(i != COLUMN_NAMES.length - 1)
                            header.setPadding(0, 0, 10, 0);

                        statsHeader.addView(header);
                    }

                    table.addView(statsHeader);

                    //Get data from JSON response and put into table
                    for(HashMap<String, String> yearResult: statsList){
                        TableRow statsRow = new TableRow(DisplayActivity.this);
                        statsRow.setGravity(Gravity.CENTER);

                        for(int i = 0; i < COLUMN_TAGS.length; i++){
                            TextView stat = createColumn(yearResult.get(COLUMN_TAGS[i]), 20, Color.BLACK);

                            if(i != COLUMN_TAGS.length - 1)
                                stat.setPadding(0, 0, 10, 0);

                            statsRow.addView(stat);
                        }

                        table.addView(statsRow);
                    }

                    setContentView(table);
                }
            });

        }

    }

    public TextView createColumn(String text, int size, int color){
        TextView view = new TextView(DisplayActivity.this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }
    
}

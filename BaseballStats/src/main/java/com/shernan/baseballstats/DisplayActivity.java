package com.shernan.baseballstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.shernan.baseballstats.utils.*;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
    private static final String TAG_FNAME = "nameFirst";
    private static final String TAG_LNAME = "nameLast";

    // stats JSONArray
    JSONArray stats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

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
     * Background Async Task to Load all product by making HTTP Request
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
                        JSONObject c = stats.getJSONObject(i);

                        // Storing each json item in variable
                        String firstName = c.getString(TAG_FNAME);
                        String lastName = c.getString(TAG_LNAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap
                        map.put(TAG_FNAME, firstName);
                        map.put(TAG_LNAME, lastName);

                        // adding HashList to ArrayList
                        statsList.add(map);
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
                     * Updating parsed JSON data into ListView
                     * */
                    TableLayout table = new TableLayout(DisplayActivity.this);

                    //Set up table header
                    TableRow titleRow = new TableRow(DisplayActivity.this);
                    titleRow.setGravity(Gravity.CENTER);
                    TextView fName = new TextView(DisplayActivity.this);
                    fName.setText("First Name");
                    TextView lName = new TextView(DisplayActivity.this);
                    lName.setText("Last Name");
                    titleRow.addView(fName);
                    titleRow.addView(lName);
                    table.addView(titleRow);

                    //Get data from JSON response and put into table
                    for(HashMap<String, String> player: statsList){
                        TableRow playerRow = new TableRow(DisplayActivity.this);
                        playerRow.setGravity(Gravity.CENTER);

                        TextView playerFName = new TextView(DisplayActivity.this);
                        playerFName.setText(player.get(TAG_FNAME));
                        TextView playerLName = new TextView(DisplayActivity.this);
                        playerLName.setText(player.get(TAG_LNAME));
                        playerRow.addView(playerFName);
                        playerRow.addView(playerLName);
                        table.addView(playerRow);
                    }

                    setContentView(table);
                }
            });

        }

    }
    
}

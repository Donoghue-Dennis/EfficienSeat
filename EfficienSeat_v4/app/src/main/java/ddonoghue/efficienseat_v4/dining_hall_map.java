package ddonoghue.efficienseat_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static ddonoghue.efficienseat_v4.MyContext.getContext;

public class dining_hall_map extends AppCompatActivity {

    final List<localTable> Tables = new ArrayList<localTable>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    Thread mythread;
    int intPartySize = 0;
    CustomView mCustomView;
    AmazonDynamoDBClient ddbClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_hall_map);

        //create and store session ID
        String android_id = Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
        int deviceId = android_id.hashCode() % 10000;

        if (containsData("deviceId")) {
            getData("deviceId");
        } else setData("deviceId", deviceId);

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:20683c3f-19dc-43cd-9f92-0fd149d55078", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);

        //write sample tables
        //writeTestTables();


        //initialize refresh and custom view
        mCustomView = (CustomView) findViewById(R.id.custom_view);

        //Get Selected Dining Hall
        Intent intent = getIntent();
        String diningHall = intent.getStringExtra("DINING_HALL") + " Selected";
        Toast.makeText(getApplicationContext(), diningHall, Toast.LENGTH_SHORT).show();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //refresh tables
                renderTables(mCustomView, intPartySize);
                Log.d("db", "Tables Refreshed Automatically");
            }
        };
        timer.schedule(timerTask, 1, 15000);

        //render tables at activity launch, and start automatic table rendering
        renderTables(mCustomView, intPartySize);

        //get party size edittext
        final EditText partySize = findViewById(R.id.party_search);

        //textchange listener
        partySize.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {

                //fetch and set party size
                String strPartySize = (String) partySize.getText().toString();
                if (strPartySize.length() < 1) strPartySize = "0";
                intPartySize = Integer.parseInt(strPartySize);

                renderTables(mCustomView, intPartySize);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Toast.makeText(getApplicationContext(), "Refresh Tables", Toast.LENGTH_SHORT).show();
                renderTables(mCustomView, intPartySize);
                return true;
            case (MotionEvent.ACTION_MOVE):
                return true;
            case (MotionEvent.ACTION_UP):
                return true;
            case (MotionEvent.ACTION_CANCEL):
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void writeTestTables() {
        //sample tables
        localTable testTable1 = new localTable(ddbClient, 100, 700, 1, 0, 0, 45, 1, 0, 0, 0);
        localTable testTable2 = new localTable(ddbClient, 100, 500, 2, 1, 0, 135, 1, 1, 1, 1);
        localTable testTable3 = new localTable(ddbClient, 100, 300, 3, 0, 0, 225, 1, 0, 1, 0);
        localTable testTable4 = new localTable(ddbClient, 100, 100, 4, 0, 0, 315, 0, 1, 0, 0);
        localTable testTable5 = new localTable(ddbClient, 350, 600, 5, 0, 0, 45, 1, 0, 2, 2);
        localTable testTable6 = new localTable(ddbClient, 550, 600, 6, 0, 1, 0, 0, 0, 0, 0);
        localTable testTable7 = new localTable(ddbClient, 600, 450, 7, 0, 0, 45, 0, 0, 0, 0);
        localTable testTable8 = new localTable(ddbClient, 350, 400, 8, 1, 1, 0, 1, 1, 1, 1);
        localTable testTable9 = new localTable(ddbClient, 550, 300, 9, 0, 1, 0, 0, 0, 0, 0);
        localTable testTable10 = new localTable(ddbClient, 350, 150, 10, 2, 1, 0, 2, 2, 2, 2);

        testTable1.writeTable();
        testTable2.writeTable();
        testTable3.writeTable();
        testTable4.writeTable();
        testTable5.writeTable();
        testTable6.writeTable();
        testTable7.writeTable();
        testTable8.writeTable();
        testTable9.writeTable();
        testTable10.writeTable();

        int sentInt = 7777;
        //testTable1.setData("foo",sentInt);
        int recInt = testTable1.getData("foo");
        Log.d("note", "Sent Data: " + sentInt + "Recieved Data: " + recInt);
    }

    public void scanTables() {
        Runnable runnable = new Runnable() {
            public void run() {
                ScanRequest scanRequest = new ScanRequest().withTableName("Tables");

                ScanResult result = ddbClient.scan(scanRequest);
                for (Map<String, AttributeValue> item : result.getItems()) {
                    localTable tempTable = new localTable();
                    tempTable.updateTable(item);
                    tempTable.updateClient(ddbClient);
                    Tables.add(tempTable);
                }
            }
        };
        mythread = new Thread(runnable);
        mythread.start();
    }

    public localTable readTable(final String id) {
        final localTable tempTable = new localTable();

        Runnable runnable = new Runnable() {
            public void run() {
                Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
                key.put("tableID", new AttributeValue().withN(id));
                GetItemResult result = ddbClient.getItem("Tables", key);
                Map<String, AttributeValue> item = result.getItem();
                tempTable.updateTable(item);
                tempTable.updateClient(ddbClient);
                Tables.add(tempTable);
            }
        };
        mythread = new Thread(runnable);
        mythread.start();

        return tempTable;
    }

    public void renderTables(final CustomView mCustomView, int PartySize) {
        //set party size
        mCustomView.setPartySize(PartySize);

        //download and set tables
        scanTables();
        while (mythread.isAlive()) ;
        mCustomView.addTables(Tables);

        //render
        mCustomView.postInvalidate();
    }

    public void setData(String key, int value) {
        SharedPreferences sharedPreferences;
        sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getData(String key) {
        SharedPreferences sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    public boolean containsData(String key) {
        SharedPreferences sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

}

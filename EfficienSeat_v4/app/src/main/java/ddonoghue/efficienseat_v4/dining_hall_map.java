package ddonoghue.efficienseat_v4;

import android.content.Intent;
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

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class dining_hall_map extends AppCompatActivity {

    final List<Table> Tables = new ArrayList<Table>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    Thread mythread;
    int intPartySize = 0;
    CustomView mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_hall_map);

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
                Log.d("db","Tables Refreshed Automatically");
            }
        };
        timer.schedule(timerTask, 1, 30000);

        //render tables at activity launch, and start automatic table rendering
        renderTables(mCustomView, intPartySize);

        //get party size edittext
        final EditText partySize = findViewById(R.id.party_search);

        //textchange listener
        partySize.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) {

                //fetch and set party size
                String strPartySize = (String) partySize.getText().toString();
                if(strPartySize.length() < 1) strPartySize = "0";
                intPartySize = Integer.parseInt(strPartySize);

                renderTables(mCustomView, intPartySize);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action =  MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Toast.makeText(getApplicationContext(), "Refresh Tables", Toast.LENGTH_SHORT).show();
                renderTables(mCustomView, intPartySize);
                return true;
            case (MotionEvent.ACTION_MOVE) :
                return true;
            case (MotionEvent.ACTION_UP) :
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    public void writeTestTables(){
        //sample tables
        Table testTable1 = new Table(100, 100, 12, 0, 1, 4, 4, 0,0,0,0);
        Table testTable2 = new Table(250, 500, 23, 0, 1, 4, 3, 0,0,0,1);
        Table testTable3 = new Table(500, 1000, 34, 1, 1, 4, 3, 0,0,1,0);
        Table testTable4 = new Table(350, 750, 45, 0, 1, 4, 2, 0,0,1,1);
        Table testTable5 = new Table(1000, 1000, 56, 1, 1, 4, 3, 0,1,0,0);
        Table testTable6 = new Table(600, 300, 67, 1, 1, 4, 2, 0,1,0,1);
        Table testTable7 = new Table(700, 1000, 78, 0, 1, 4, 2, 0,1,1,0);
        Table testTable8 = new Table(800, 100, 89, 0, 1, 4, 1, 0,1,1,1);
        Table testTable9 = new Table(900, 500, 90, 0, 1, 4, 3, 1,0,0,0);

        pushTable(testTable1);
        pushTable(testTable2);
        pushTable(testTable3);
        pushTable(testTable4);
        pushTable(testTable5);
        pushTable(testTable6);
        pushTable(testTable7);
        pushTable(testTable8);
        pushTable(testTable9);
    }

    public void pullTables(){
        Runnable runnable = new Runnable() {
            public void run() {

                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:20683c3f-19dc-43cd-9f92-0fd149d55078", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                ScanRequest scanRequest = new ScanRequest().withTableName("Tables");

                ScanResult result = ddbClient.scan(scanRequest);
                for (Map<String, AttributeValue> item : result.getItems()){
                    Table tempTable = new Table();
                    tempTable.setTableCap(Integer.parseInt(item.get("tableCap").getN()));
                    tempTable.setTableID(Integer.parseInt(item.get("tableID").getN()));
                    tempTable.setTableX(Integer.parseInt(item.get("tableX").getN()));
                    tempTable.setTableY(Integer.parseInt(item.get("tableY").getN()));
                    tempTable.setTableStatus(Integer.parseInt(item.get("tableStatus").getN()));
                    tempTable.setTableAvail(Integer.parseInt(item.get("tableAvail").getN()));
                    tempTable.setTableType(Integer.parseInt(item.get("tableType").getN()));
                    tempTable.setSeat1(Integer.parseInt(item.get("seat1").getN()));
                    tempTable.setSeat2(Integer.parseInt(item.get("seat2").getN()));
                    tempTable.setSeat3(Integer.parseInt(item.get("seat3").getN()));
                    tempTable.setSeat4(Integer.parseInt(item.get("seat4").getN()));
                    Tables.add(tempTable);
                }
            }
        };
        mythread = new Thread(runnable);
        mythread.start();
    }

    public void pushTable(final Table table){
        Runnable runnable = new Runnable() {
            public void run() {

                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:20683c3f-19dc-43cd-9f92-0fd149d55078", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                mapper.save(table);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    public void renderTables(final CustomView mCustomView, int PartySize){
        //set party size
        mCustomView.setPartySize(PartySize);

        //download and set tables
        pullTables();
        while(mythread.isAlive());
        mCustomView.setTable(Tables);

        //render
        mCustomView.postInvalidate();
    }
}

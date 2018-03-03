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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
        localTable testTable1 = new localTable(this,100,100,12,0,1,0,0,0,0,0);
        localTable testTable2 = new localTable(this,250,500,23,0,1,15,0,0,0,1);
        localTable testTable3 = new localTable(this,500,1000,34,1,1,30,1,1,1,1);
        localTable testTable4 = new localTable(this,350,750,45,0,1,45,0,0,1,1);
        localTable testTable5 = new localTable(this,1000,1000,56,1,1,60,0,1,0,0);
        localTable testTable6 = new localTable(this,600,300,67,1,1,75,0,1,0,1);
        localTable testTable7 = new localTable(this,700,1000,78,0,1,90,0,1,1,0);
        localTable testTable8 = new localTable(this,800,100,89,0,1,270,0,1,1,1);
        localTable testTable9 = new localTable(this,900,500,90,1,1,359,1,0,0,0);
        localTable testTable10 = new localTable(this,770,770,1,0,1,360,1,0,0,0);

        writeTable(testTable1);
        writeTable(testTable2);
        writeTable(testTable3);
        writeTable(testTable4);
        writeTable(testTable5);
        writeTable(testTable6);
        writeTable(testTable7);
        writeTable(testTable8);
        writeTable(testTable9);

        //condWriteTable(testTable10, "tableStatus","0");
    }

    public void scanTables(){
        Runnable runnable = new Runnable() {
            public void run() {
                ScanRequest scanRequest = new ScanRequest().withTableName("Tables");

                ScanResult result = ddbClient.scan(scanRequest);
                for (Map<String, AttributeValue> item : result.getItems()){
                    localTable tempTable = new localTable();
                    tempTable.updateTable(item);
                    Tables.add(tempTable);
                }
            }
        };
        mythread = new Thread(runnable);
        mythread.start();
    }

    public localTable readTable(final String id){
        final localTable tempTable = new localTable();

         Runnable runnable = new Runnable() {
            public void run() {
                Map<String,AttributeValue> testKey = new HashMap<String,AttributeValue>();
                testKey.put("tableID", new AttributeValue().withN(id));
                GetItemResult result = ddbClient.getItem("Tables",testKey);
                Map<String,AttributeValue> item = result.getItem();
                tempTable.updateTable(item);
            }
        };
        mythread = new Thread(runnable);
        mythread.start();

        return tempTable;
    }

    public void condWriteTable(final localTable table, final String key, final String expectedValue){
        Runnable runnable = new Runnable() {
            public void run() {
                String tStat = Integer.toString(table.getTableStatus());
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
                Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<String, ExpectedAttributeValue>();

                expectedAttributes.put(key, new ExpectedAttributeValue(new AttributeValue().withN(expectedValue)).withExists(true));

                saveExpression.setExpected(expectedAttributes);


                try {
                    mapper.save(table, saveExpression);
                } catch (Exception e) {
                    //Handle conditional check
                    Log.d("err","Conditional save failed: " + e.toString());
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    public void writeTable(final localTable table){
        Runnable runnable = new Runnable() {
            public void run() {
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
        scanTables();
        while(mythread.isAlive());
        mCustomView.addTables(Tables);

        //render
        mCustomView.postInvalidate();
    }
}

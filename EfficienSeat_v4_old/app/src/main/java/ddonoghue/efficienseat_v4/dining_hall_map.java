package ddonoghue.efficienseat_v4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;

import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;

import java.net.InetAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static ddonoghue.efficienseat_v4.MyContext.getContext;

public class dining_hall_map extends AppCompatActivity {

    private ProfileCredentialsProvider credentialsProvider;
    Thread mythread;
    int intPartySize = 0;
    TableMapView mTableMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_hall_map);

        //create and store session ID
        String android_id = Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
        int deviceId = android_id.hashCode() % 10000;
        deviceId = Math.abs(deviceId);
        setData("deviceId", deviceId);

        //initialize credentials for stream
        credentialsProvider = new ProfileCredentialsProvider();
        try{
            credentialsProvider.getCredentials();
        }catch (Exception e){
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (~/.aws/credentials), and is in valid format.", e);
        }

        //start stream
        try{
            startStream();
        }catch (Exception e){
            Log.d("e",e.toString());
        }

        //write sample tables
        //writeTestTables();

        //initialize refresh and custom view
        mTableMapView = (TableMapView) findViewById(R.id.custom_view);

        //Get Selected Dining Hall
        Intent intent = getIntent();
        mTableMapView.currentIntent = intent;
        String diningHall = intent.getStringExtra("DINING_HALL") + " Selected";
        Toast.makeText(getApplicationContext(), diningHall, Toast.LENGTH_SHORT).show();

        //render tables at activity launch, and start automatic table rendering
        renderTables(mTableMapView, intPartySize);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //refresh tables
                renderTables(mTableMapView, intPartySize);
                Log.d("db", "Tables Refreshed Automatically");
            }
        };
        timer.schedule(timerTask, 1, 15000);

        //textchange listener
        final EditText partySize = findViewById(R.id.party_search);
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
                mTableMapView.activateTableSearch(intPartySize);

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        renderTables(mTableMapView, intPartySize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Toast.makeText(getApplicationContext(), "Refresh Tables", Toast.LENGTH_SHORT).show();
                renderTables(mTableMapView, intPartySize);
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
        AmazonDynamoDBClient tempClient = MyDDBClient.getInstance().ddbClient;

        //sample tables
        localTable testTable1 = new localTable(100, 700, 1, 0, 0, 45, 1, 0, 0, 0);
        localTable testTable2 = new localTable(100, 500, 2, 1, 0, 135, 0, 0, 0, 0);
        localTable testTable3 = new localTable(100, 300, 3, 0, 0, 225, 1, 0, 1, 0);
        localTable testTable4 = new localTable(100, 100, 4, 0, 0, 315, 0, 1, 0, 0);
        localTable testTable5 = new localTable(350, 600, 5, 0, 0, 45, 1, 0, 2, 2);
        localTable testTable6 = new localTable(550, 600, 6, 0, 1, 0, 0, 0, 0, 0);
        localTable testTable7 = new localTable(600, 450, 7, 0, 0, 45, 0, 0, 0, 0);
        localTable testTable8 = new localTable(350, 400, 8, 1, 1, 0, 0, 0, 0, 0);
        localTable testTable9 = new localTable(550, 300, 9, 0, 1, 0, 0, 0, 0, 0);
        localTable testTable10 = new localTable(350, 150, 10, 2, 1, 0, 0, 0, 0, 0);

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
            AmazonDynamoDBClient tempClient = MyDDBClient.getInstance().ddbClient;
            public void run() {
                ScanRequest scanRequest = new ScanRequest().withTableName("Tables");

                ScanResult result = tempClient.scan(scanRequest);
                for (Map<String, AttributeValue> item : result.getItems()) {
                    localTable tempTable = new localTable();
                    tempTable.updateTable(item);
                    MyTables.getInstance().tables.add(tempTable);
                }
            }
        };
        mythread = new Thread(runnable);
        mythread.start();
    }

    public void startStream() throws Exception{
        //Initialize Amazon Kinesis client
        AmazonKinesis client = AmazonKinesisClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-1")
                .build();

        String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
        KinesisClientLibConfiguration kinesisClientLibConfiguration =
                new KinesisClientLibConfiguration("testApplication",
                        "testStreamName",
                        credentialsProvider,
                        workerId);
        kinesisClientLibConfiguration.withInitialPositionInStream(InitialPositionInStream.LATEST);

        IRecordProcessorFactory recordProcessorFactory = new AmazonKinesisApplicationRecordProcessorFactory();
        Worker worker = new Worker(recordProcessorFactory, kinesisClientLibConfiguration);

        int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
            System.err.println("Caught throwable while processing data.");
            t.printStackTrace();
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    public void renderTables(final TableMapView mTableMapView, int PartySize) {
        //set party size
        mTableMapView.setPartySize(PartySize);

        //download and set tables
        scanTables();
        while (mythread.isAlive()) ;

        //render
        mTableMapView.postInvalidate();
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

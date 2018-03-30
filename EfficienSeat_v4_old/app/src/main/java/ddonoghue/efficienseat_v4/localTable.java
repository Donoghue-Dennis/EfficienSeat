package ddonoghue.efficienseat_v4;

/**
 * Created by DDonoghue on 12/3/2017.
 */

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

import java.util.HashMap;
import java.util.Map;

@DynamoDBTable(tableName = "Tables")
public class localTable {

    private int tableID,tableStatus,tableX,tableY,tableAvail,tableType,tableAngle,seat1,seat2,seat3,seat4;
    AmazonDynamoDBClient ddbClient;

    public localTable(AmazonDynamoDBClient databaseClient, int x, int y, int id, int status, int type, int angle, int seat1, int seat2, int seat3, int seat4){
        this.tableStatus = status;
        this.tableX = x;
        this.tableY = y;
        this.tableID = id;
        this.tableType = type;
        this.tableAngle = angle;
        this.seat1 = seat1;
        this.seat2 = seat2;
        this.seat3 = seat3;
        this.seat4 = seat4;
        this.ddbClient = databaseClient;
    }

    public localTable(){}

    public void printTable(){
        Log.d("db","Table ID: " + tableID);
        Log.d("db","Table Status: " + tableStatus);
        Log.d("db","Table X: " + tableX);
        Log.d("db","Table Y: " + tableY);
        Log.d("db","Table Type: " + tableType);
        Log.d("db","Table Angle: " + tableAngle);
        Log.d("db","Seat One Status: " + seat1);
        Log.d("db","Seat Two Status: " + seat2);
        Log.d("db","Seat Three Status: " + seat3);
        Log.d("db","Seat Four Status: " + seat4);
    }

    public void setData(String key, int value){
        SharedPreferences sharedPreferences;
        sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getData(String key){
        SharedPreferences sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }


    public void updateTable(Map<String, AttributeValue> item){
        this.setTableID(Integer.parseInt(item.get("tableID").getN()));
        this.setTableX(Integer.parseInt(item.get("tableX").getN()));
        this.setTableY(Integer.parseInt(item.get("tableY").getN()));
        this.setTableStatus(Integer.parseInt(item.get("tableStatus").getN()));
        this.setTableAngle(Integer.parseInt(item.get("tableAngle").getN()));
        this.setTableType(Integer.parseInt(item.get("tableType").getN()));
        this.setSeat1(Integer.parseInt(item.get("seat1").getN()));
        this.setSeat2(Integer.parseInt(item.get("seat2").getN()));
        this.setSeat3(Integer.parseInt(item.get("seat3").getN()));
        this.setSeat4(Integer.parseInt(item.get("seat4").getN()));
    }

    public void condWriteTable(final String key, final String expectedValue){
        final localTable tempTable = this;
        Runnable runnable = new Runnable() {
            public void run() {
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
                Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<String, ExpectedAttributeValue>();

                expectedAttributes.put(key, new ExpectedAttributeValue(new AttributeValue().withN(expectedValue)).withExists(true));

                saveExpression.setExpected(expectedAttributes);

                try{
                    mapper.save(tempTable, saveExpression);
                }catch (ConditionalCheckFailedException e){
                    //Handle conditional check
                    Log.d("err","Conditional save failed: " + e.toString());
                }

            }
        };
        Thread mythread = new Thread(runnable);
        try{
            mythread.start();
        }catch (ConditionalCheckFailedException e){
            //Handle conditional check
            Log.d("err","Conditional save failed: " + e.toString());
        }
    }

    public void writeTable(){
        final localTable tempTable = this;
        Runnable runnable = new Runnable() {
            public void run() {
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                mapper.save(tempTable);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    public void updateClient(AmazonDynamoDBClient newClient){
        ddbClient = newClient;
    }

    //GETS
    public int getTableAvail() {
        tableAvail = 0;
        int seshInt = getData("deviceId");
        if(seat1 == 0 || seat1 == seshInt) tableAvail++;
        if(seat2 == 0 || seat2 == seshInt) tableAvail++;
        if(seat3 == 0 || seat3 == seshInt) tableAvail++;
        if(seat4 == 0 || seat4 == seshInt) tableAvail++;
        return tableAvail;
    }

    @DynamoDBHashKey(attributeName = "tableID")
    public int getTableID() {
        return tableID;
    }

    @DynamoDBAttribute(attributeName = "tableStatus")
    public int getTableStatus() {
        return tableStatus;
    }

    @DynamoDBAttribute(attributeName = "tableType")
    public int getTableType() {
        return tableType;
    }

    @DynamoDBAttribute(attributeName = "tableAngle")
    public int getTableAngle() {
        return tableAngle;
    }

    @DynamoDBAttribute(attributeName = "tableX")
    public int getTableX(){
        return tableX;
    }

    @DynamoDBAttribute(attributeName = "tableY")
    public int getTableY(){
        return tableY;
    }

    @DynamoDBAttribute(attributeName = "seat1")
    public int getSeat1(){
        return seat1;
    }

    @DynamoDBAttribute(attributeName = "seat2")
    public int getSeat2(){
        return seat2;
    }

    @DynamoDBAttribute(attributeName = "seat3")
    public int getSeat3(){
        return seat3;
    }

    @DynamoDBAttribute(attributeName = "seat4")
    public int getSeat4(){
        return seat4;
    }

    //SETS
    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public void setTableStatus(int tableStatus) {
        this.tableStatus = tableStatus;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    public void setTableAngle(int tableAngle) {
        this.tableAngle = tableAngle;
    }

    public void setTableX(int tableX) {
        this.tableX = tableX;
    }

    public void setTableY(int tableY) {
        this.tableY = tableY;
    }

    public void setSeat1(int seat1) {
        this.seat1 = seat1;
    }

    public void setSeat2(int seat2) {
        this.seat2 = seat2;
    }

    public void setSeat3(int seat3) {
        this.seat3 = seat3;
    }

    public void setSeat4(int seat4) {
        this.seat4 = seat4;
    }
}


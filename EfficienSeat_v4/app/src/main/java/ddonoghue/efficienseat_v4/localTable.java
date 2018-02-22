package ddonoghue.efficienseat_v4;

/**
 * Created by DDonoghue on 12/3/2017.
 */

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

@DynamoDBTable(tableName = "Tables")
public class localTable {

    private int tableID,tableStatus,tableX,tableY,tableCap,tableAvail,tableType,tableAngle,seat1,seat2,seat3,seat4;

    private Context tableContext;

    public localTable(Context context, int x, int y, int id, int status, int type, int angle, int seat1, int seat2, int seat3, int seat4){
        this.tableStatus = status;
        this.tableX = x;
        this.tableY = y;
        this.tableID = id;
        this.tableAvail = 4-seat1-seat2-seat3-seat4;
        this.tableType = type;
        this.tableAngle = angle;
        this.seat1 = seat1;
        this.seat2 = seat2;
        this.seat3 = seat3;
        this.seat4 = seat4;
        this.tableContext = context;
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

    //GETS
    public int getTableAvail() {
        tableAvail = 4-seat1-seat2-seat3-seat4;
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


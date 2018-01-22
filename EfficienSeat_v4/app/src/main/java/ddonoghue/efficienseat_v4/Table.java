package ddonoghue.efficienseat_v4;

/**
 * Created by DDonoghue on 12/3/2017.
 */

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Tables")
public class Table {

    private int tableID, tableStatus,tableX,tableY,tableCap,tableAvail,tableType,seat1,seat2,seat3,seat4;

    public Table(int x, int y, int id, int status, int type, int cap, int avail, int seat1, int seat2, int seat3, int seat4){
        this.tableStatus = status;
        this.tableX = x;
        this.tableY = y;
        this.tableID = id;
        this.tableCap = cap;
        this.tableAvail = avail;
        this.tableType = type;
        this.seat1 = seat1;
        this.seat2 = seat2;
        this.seat3 = seat3;
        this.seat4 = seat4;
    }

    public Table(){}

    public void printTable(){
        Log.d("db","Table ID: " + tableID);
        Log.d("db","Table Status: " + tableStatus);
        Log.d("db","Table X: " + tableX);
        Log.d("db","Table Y: " + tableY);
        Log.d("db","Table Capacity: " + tableCap);
        Log.d("db","Table Availability: " + tableAvail);
        Log.d("db","Table Type: " + tableType);
        Log.d("db","Seat One Status: " + seat1);
        Log.d("db","Seat Two Status: " + seat2);
        Log.d("db","Seat Three Status: " + seat3);
        Log.d("db","Seat Four Status: " + seat4);
    }

    //GETS
    @DynamoDBAttribute(attributeName = "tableAvail")
    public int getTableAvail() {
        return tableAvail;
    }

    @DynamoDBAttribute(attributeName = "tableCap")
    public int getTableCap() {
        return tableCap;
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
    public void setTableAvail(int tableAvail) {
        this.tableAvail = tableAvail;
    }

    public void setTableCap(int tableCap) {
        this.tableCap = tableCap;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public void setTableStatus(int tableStatus) {
        this.tableStatus = tableStatus;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
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


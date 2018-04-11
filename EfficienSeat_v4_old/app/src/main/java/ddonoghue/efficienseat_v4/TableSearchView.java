package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class TableSearchView extends View {

    //paint color settings
    private Paint paintClaimed;
    private Paint paintReserved;
    private Paint paintOpen;
    private Paint paintError;
    private Paint paintText;

    //table size, table offset
    int[] seatX = new int[4];
    int[] seatY = new int[4];
    int tSize = 150;
    float off = Math.round(tSize*1.65);
    float justOff = Math.round(tSize*1.5);
    float miniLabelOff = Math.round(off*0.1);
    int canvasWidth, canvasHeight;

    //foo
    localTable currentTable = new localTable();
    int partySize = 0;
    int seshInt = 0;
    int tableIterator = 0;

    //scaling variables
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;

    private void init(@Nullable AttributeSet set){
        //initialize variables
        seshInt = getData("deviceId");

        //Initialize paints
        paintClaimed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintClaimed.setColor(getResources().getColor(R.color.claimed));
        paintReserved = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintReserved.setColor(getResources().getColor(R.color.reserved));
        paintOpen = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOpen.setColor(getResources().getColor(R.color.open));
        paintError = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintError.setColor(getResources().getColor(R.color.gray));
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.canvasWidth = (int) (w - 2*off);
        this.canvasHeight = (int) (h - 2*off);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //Clear previous canvas
        canvas.drawColor(Color.WHITE);

        //canvas scaling stuff
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        //draw table
        drawNextTable(canvas);

        //render
        canvas.restore();
    }

    public void drawNextTable(Canvas canvas){
        if(tableIterator < MyTables.getInstance().tables.size()){
            //grab oft-used variables
            currentTable = MyTables.getInstance().tables.get(tableIterator);
            if(currentTable.getTableAvail() >= partySize){
                TextView tableName = findViewById(R.id.table_name);
                if(tableName != null) tableName.setText("Table " + currentTable.getTableID());
                float tableX = canvasWidth;
                float tableY = (float) (canvasHeight*0.75);
                int tableStatus = currentTable.getTableStatus();
                int seatOne = currentTable.getSeat1();
                int seatTwo = currentTable.getSeat2();
                int seatThree = currentTable.getSeat3();
                int seatFour = currentTable.getSeat4();

                //print central table with label
                if(partySize < currentTable.getClassicTableAvail() && currentTable.getTableType() == 0){
                    canvas.drawCircle(tableX, tableY, tSize, paintOpen);
                }else canvas.drawCircle(tableX, tableY, tSize, paintReserved);

                paintText.setTextSize(tSize);
                int textOff = 0;
                if(String.valueOf(currentTable.getTableID()).length() == 1){
                    textOff = tSize/4;
                }
                if(String.valueOf(currentTable.getTableID()).length() == 2){
                    textOff = tSize/2;
                }
                canvas.drawText(currentTable.getTableID() + "",tableX-textOff, tableY+13, paintText);

                //print seats
                paintText.setTextSize(tSize/2);
                if(currentTable.getTableType() == 0){
                    //create seats
                    seatX[0] = (int)(tableX + (off * sin(toRadians(currentTable.getTableAngle()-135))));
                    seatY[0] = (int)(tableY + (off * cos(toRadians(currentTable.getTableAngle()-135))));
                    seatX[1] = (int)(tableX + (off * sin(toRadians(currentTable.getTableAngle()+135))));
                    seatY[1] = (int)(tableY + (off * cos(toRadians(currentTable.getTableAngle()+135))));
                    seatX[2] = (int)(tableX + (off * sin(toRadians(currentTable.getTableAngle()+45))));
                    seatY[2] = (int)(tableY + (off * cos(toRadians(currentTable.getTableAngle()+45))));
                    seatX[3] = (int)(tableX + (off * sin(toRadians(currentTable.getTableAngle()-45))));
                    seatY[3] = (int)(tableY + (off * cos(toRadians(currentTable.getTableAngle()-45))));

                    int seatOffer = partySize;
                    //Seat One
                    if(seatOne == 0) {
                        canvas.drawCircle(seatX[0], seatY[0], tSize/2, paintReserved);
                        seatOffer--;
                    }else{
                        canvas.drawCircle(seatX[0], seatY[0], tSize/2, paintClaimed);
                    }
                    canvas.drawText( "1",(seatX[0] - miniLabelOff), (seatY[0] + miniLabelOff), paintText);

                    //Seat Two
                    if(seatTwo == 0) {
                        if(seatOffer>0){
                            canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintReserved);
                            seatOffer--;
                        }else canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintOpen);

                    }else{
                        canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintClaimed);
                    }
                    canvas.drawText( "2",(seatX[1] - miniLabelOff), (seatY[1] + miniLabelOff), paintText);

                    //Seat Three
                    if(seatThree == 0) {
                        if(seatOffer>0){
                            canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintReserved);
                            seatOffer--;
                        }else canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintOpen);
                    }else{
                        canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintClaimed);
                    }
                    canvas.drawText( "3",(seatX[2] - miniLabelOff), (seatY[2] + miniLabelOff), paintText);

                    //Seat Four
                    if(seatFour == 0) {
                        if(seatOffer>0){
                            canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintReserved);
                        }else canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintOpen);
                    }else{
                        canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintClaimed);
                    }
                    canvas.drawText( "4",(seatX[3] - miniLabelOff), (seatY[3] + miniLabelOff), paintText);
                }else{
                    seatX[0] = (int)(tableX + (justOff * sin(toRadians(currentTable.getTableAngle()-135))));
                    seatY[0] = (int)(tableY + (justOff * cos(toRadians(currentTable.getTableAngle()-135))));
                    seatX[1] = (int)(tableX + (justOff * sin(toRadians(currentTable.getTableAngle()+135))));
                    seatY[1] = (int)(tableY + (justOff * cos(toRadians(currentTable.getTableAngle()+135))));
                    seatX[2] = (int)(tableX + (justOff * sin(toRadians(currentTable.getTableAngle()+45))));
                    seatY[2] = (int)(tableY + (justOff * cos(toRadians(currentTable.getTableAngle()+45))));
                    seatX[3] = (int)(tableX + (justOff * sin(toRadians(currentTable.getTableAngle()-45))));
                    seatY[3] = (int)(tableY + (justOff * cos(toRadians(currentTable.getTableAngle()-45))));

                    for(int i = 0; i < 4; i++) {
                        canvas.drawCircle(seatX[i], seatY[i], tSize/2, paintReserved);
                    }
                }
            } else {
                tableIterator++;
                drawNextTable(canvas);
            }
        } else{
            tableIterator = 0;
            drawNextTable(canvas);
        }
    }

    public void reserveCurrentTable(){
        if(currentTable.getTableType() == 0){
            int seatOne = currentTable.getSeat1();
            int seatTwo = currentTable.getSeat2();
            int seatThree = currentTable.getSeat3();
            int seatFour = currentTable.getSeat4();
            int seatOffer = partySize;

            //Seat One
            if(seatOne == 0) {
                if(seatOffer>0) {
                    seatOffer--;
                    currentTable.setSeat1(seshInt);
                    try {
                        currentTable.reserveSeat("seat1", "0");
                    } catch (ConditionalCheckFailedException e) {
                        currentTable.setSeat1(0);
                    }
                }
            }

            //Seat Two
            if(seatTwo == 0) {
                if(seatOffer>0){
                    seatOffer--;
                    currentTable.setSeat2(seshInt);
                    try{
                        currentTable.reserveSeat("seat2","0");
                    }catch(ConditionalCheckFailedException e){
                        currentTable.setSeat2(0);
                    }                }
            }

            //Seat Three
            if(seatThree == 0) {
                if (seatOffer > 0) {
                    seatOffer--;
                    currentTable.setSeat3(seshInt);
                    try{
                        currentTable.reserveSeat("seat3","0");
                    }catch(ConditionalCheckFailedException e){
                        currentTable.setSeat3(0);
                    }
                }
            }

            //Seat Four
            if(seatFour == 0) {
                if(seatOffer>0){
                    seatOffer--;
                    currentTable.setSeat4(seshInt);
                    try{
                        currentTable.reserveSeat("seat4","0");
                    }catch(ConditionalCheckFailedException e){
                        currentTable.setSeat4(0);
                    }
                }
            }
        } else {
            int tableStatus = currentTable.getTableStatus();

            if(tableStatus == 0){
                currentTable.setTableStatus(seshInt);
                try{
                    currentTable.reserveTable("tableStatus", "0");
                }catch(ConditionalCheckFailedException e){
                    currentTable.setTableStatus(0);
                }
            }
        }
    }

    public void nextTable(){
        tableIterator++;
        invalidate();
    }

    public void setPartySize(int size){
        this.partySize = size;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));

            invalidate();
            return true;
        }
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

    public boolean containsData(String key){
        SharedPreferences sharedPreferences = MyContext.getContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    public TableSearchView(Context context){
        super(context);

        init(null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableSearchView(Context context, AttributeSet attrs){
        super(context, attrs);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableSearchView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @TargetApi(21)
    public TableSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
}

package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import java.util.Collections;

import static java.lang.Math.*;

/**
 * Created by DDonoghue on 11/26/2017.z
 */

public class TableMapView extends View{

    //paint color settings
    private Paint paintClaimed;
    private Paint paintReserved;
    private Paint paintOpen;
    private Paint paintError;
    private Paint paintText;

    //tables, table size, table offset
    int tableSearchCount;
    int[] seatX = new int[4];
    int[] seatY = new int[4];
    int tSize = 50;
    float off = Math.round(tSize*1.65);
    float miniLabelOff = Math.round(off*0.1);
    int canvasWidth, canvasHeight;

    //foo
    Intent currentIntent;
    int seshInt = 0;
    int partySize = 0;

    //click variables
    boolean clickStatus;
    boolean seatSearch = false;
    float clickX,clickY;

    //scaling variables
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;
    

    private void init(@Nullable AttributeSet set){
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
        //Set seshInt if necessary
        if(seshInt == 0) seshInt = getData("deviceId");

        //Clear previous canvas
        canvas.drawColor(Color.WHITE);

        //canvas scaling stuff
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        //establish canvas info
        int inBoundX = 0;
        int inBoundY = 0;
        double ratioX;
        double ratioY;
        double finalRatio;
        tableSearchCount = 0;

        //Check for timers


        for(final localTable tempTable : MyTables.getInstance().tables){
            if(tempTable != null){
                if (tempTable.getTableAvail() >= partySize){
                    tableSearchCount++;
                }

                int temptableX = tempTable.getTableX();
                int temptableY = tempTable.getTableY();

                if(temptableX > inBoundX) inBoundX = temptableX;
                if(temptableY > inBoundY) inBoundY = temptableY;
            }
        }

        ratioX = ((double)canvasWidth)/((double)inBoundX);
        ratioY = ((double)canvasHeight)/((double)inBoundY);

        if(ratioX<=ratioY)finalRatio = ratioX;
        else finalRatio = ratioY;

        if(!seatSearch){
            for(final localTable tempTable : MyTables.getInstance().tables){
                if(tempTable != null){
                    int x = ((int)(tempTable.getTableX() * finalRatio)) + tSize;
                    int y = canvasHeight-((int)(tempTable.getTableY() * finalRatio)) + tSize;

                    drawTable(x,y,tempTable,canvas);
                }
            }
        }else if(tableSearchCount < 1){
            printToast("No tables fit the search criteria");
            seatSearch = false;
            for(final localTable tempTable : MyTables.getInstance().tables){
                if(tempTable != null){
                    int x = ((int)(tempTable.getTableX() * finalRatio)) + tSize;
                    int y = canvasHeight-((int)(tempTable.getTableY() * finalRatio)) + tSize;

                    drawTable(x,y,tempTable,canvas);
                }
            }
        }else{
            displaySearchTable(canvas);
        }

        clickStatus = false;
        canvas.restore();
    }

    public void drawTable(int tableX, int tableY, localTable table, Canvas canvas){
        //establish party fit
        boolean partyFits = table.getTableAvail() >= partySize;

        //grab oft-used variables
        int tableStatus = table.getTableStatus();
        int seatOne = table.getSeat1();
        int seatTwo = table.getSeat2();
        int seatThree = table.getSeat3();
        int seatFour = table.getSeat4();

        //Draw central table
        //if table is unclaimed and party fits
        if(tableStatus == 0 && partyFits)
            canvas.drawCircle(tableX, tableY, tSize, paintOpen);
            //if table is reserved and partyFits
        else if(tableStatus == seshInt)
            canvas.drawCircle(tableX, tableY, tSize, paintReserved);
            //if table is claimed or party doesn't fit and table isn't errored
        else if(tableStatus >= 1 || (!partyFits))
            canvas.drawCircle(tableX, tableY, tSize, paintClaimed);
            //table is errored
        else canvas.drawCircle(tableX, tableY, tSize, paintError);
        //print label
        paintText.setTextSize(tSize);
        int textOff = 0;
        if(String.valueOf(table.getTableID()).length() == 1){
            textOff = tSize/4;
        }
        if(String.valueOf(table.getTableID()).length() == 2){
            textOff = tSize/2;
        }
        canvas.drawText(table.getTableID() + "",tableX-textOff, tableY+13, paintText);

        //draw seats
        if(partyFits && table.getTableAvail() > 0){
            //adjust text size
            paintText.setTextSize(tSize/2);

            //if table is multireserve
            if(table.getTableType() == 0){
                //create seats
                seatX[0] = (int)(tableX + (off * sin(toRadians(table.getTableAngle()-135))));
                seatY[0] = (int)(tableY + (off * cos(toRadians(table.getTableAngle()-135))));
                seatX[1] = (int)(tableX + (off * sin(toRadians(table.getTableAngle()+135))));
                seatY[1] = (int)(tableY + (off * cos(toRadians(table.getTableAngle()+135))));
                seatX[2] = (int)(tableX + (off * sin(toRadians(table.getTableAngle()+45))));
                seatY[2] = (int)(tableY + (off * cos(toRadians(table.getTableAngle()+45))));
                seatX[3] = (int)(tableX + (off * sin(toRadians(table.getTableAngle()-45))));
                seatY[3] = (int)(tableY + (off * cos(toRadians(table.getTableAngle()-45))));

                //Seat One
                    if(seatOne == 0) {
                        canvas.drawCircle(seatX[0], seatY[0], tSize/2, paintOpen);
                    }else if(seatOne == 1 || (seatOne != seshInt)){
                        canvas.drawCircle(seatX[0], seatY[0], tSize/2, paintClaimed);
                    }else if(seatOne == seshInt){
                        canvas.drawCircle(seatX[0], seatY[0], tSize/2, paintReserved);
                    }
                canvas.drawText( "1",(seatX[0] - miniLabelOff), (seatY[0] + miniLabelOff), paintText);

                //Seat Two
                if(seatTwo == 0) {
                    canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintOpen);
                }else if(seatTwo == 1 || (seatTwo != seshInt)){
                    canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintClaimed);
                }else if(seatTwo == seshInt){
                    canvas.drawCircle(seatX[1], seatY[1], tSize/2, paintReserved);
                }
                canvas.drawText( "2",(seatX[1] - miniLabelOff), (seatY[1] + miniLabelOff), paintText);

                //Seat Three
                if(seatThree == 0) {
                    canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintOpen);
                }else if(seatThree == 1 || (seatThree != seshInt)){
                    canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintClaimed);
                }else if(seatThree == seshInt){
                    canvas.drawCircle(seatX[2], seatY[2], tSize/2, paintReserved);
                }
                canvas.drawText( "3",(seatX[2] - miniLabelOff), (seatY[2] + miniLabelOff), paintText);

                //Seat Four
                if(seatFour == 0) {
                    canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintOpen);
                }else if(seatFour == 1 || (seatFour != seshInt)){
                    canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintClaimed);
                }else if(seatFour == seshInt){
                    canvas.drawCircle(seatX[3], seatY[3], tSize/2, paintReserved);
                }
                canvas.drawText( "4",(seatX[3] - miniLabelOff), (seatY[3] + miniLabelOff), paintText);
            }
            else {
                //table is single reserve
                seatX[0] = (int)(tableX + (tSize * sin(toRadians(table.getTableAngle()-135))));
                seatY[0] = (int)(tableY + (tSize * cos(toRadians(table.getTableAngle()-135))));
                seatX[1] = (int)(tableX + (tSize * sin(toRadians(table.getTableAngle()+135))));
                seatY[1] = (int)(tableY + (tSize * cos(toRadians(table.getTableAngle()+135))));
                seatX[2] = (int)(tableX + (tSize * sin(toRadians(table.getTableAngle()+45))));
                seatY[2] = (int)(tableY + (tSize * cos(toRadians(table.getTableAngle()+45))));
                seatX[3] = (int)(tableX + (tSize * sin(toRadians(table.getTableAngle()-45))));
                seatY[3] = (int)(tableY + (tSize * cos(toRadians(table.getTableAngle()-45))));

                //draw seats
                for(int i = 0; i < 4; i++) {
                    if(tableStatus == 0) {
                        canvas.drawCircle(seatX[i], seatY[i], tSize/2, paintOpen);
                    }else if(tableStatus == 1 || (tableStatus != seshInt)){
                        canvas.drawCircle(seatX[i], seatY[i], tSize/2, paintClaimed);
                    }else if(tableStatus == seshInt){
                        canvas.drawCircle(seatX[i], seatY[i], tSize/2, paintReserved);
                    }
                }
            }
        }

        if(clickStatus){
            clickHandler(tableX, tableY, table);
        }
    }

    public void displaySearchTable(Canvas canvas){
        //Organize tables
        Collections.shuffle(MyTables.getInstance().tables);

        Intent myIntent = new Intent(MyContext.getContext(), search.class);
        myIntent.putExtra("partySize",partySize);
        MyContext.getContext().startActivity(myIntent);
        seatSearch = false;
    }

    public void activateTableSearch(int partySize){
        setPartySize(partySize);
        seatSearch = true;
    }

    public void clickHandler(int tableX, int tableY, localTable table){
        //check if table or seats were clicked.
        int tableType = table.getTableType();
        if(tableType == 0){
            int distance1 = (int)Math.sqrt((seatX[0]-clickX)*(seatX[0]-clickX) + (seatY[0]-clickY)*(seatY[0]-clickY));
            int distance2 = (int)Math.sqrt((seatX[1]-clickX)*(seatX[1]-clickX) + (seatY[1]-clickY)*(seatY[1]-clickY));
            int distance3 = (int)Math.sqrt((seatX[2]-clickX)*(seatX[2]-clickX) + (seatY[2]-clickY)*(seatY[2]-clickY));
            int distance4 = (int)Math.sqrt((seatX[3]-clickX)*(seatX[3]-clickX) + (seatY[3]-clickY)*(seatY[3]-clickY));
            if(distance1 <= tSize){
                if(table.getSeat1() == 0){
                    if(table.getSeat2() == seshInt && table.getSeat3() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(seshInt);
                    table.setSeat1(seshInt);
                    try{
                        table.reserveSeat("seat1","0");
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 1 was changed before request was sent");
                        table.setSeat1(0);
                    }
                    invalidate();
                }else if(table.getSeat1() == seshInt){
                    if(table.getSeat2() == seshInt && table.getSeat3() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(0);
                    table.setSeat1(0);
                    try{
                        table.reserveSeat("seat1",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 1 was changed before request was sent");
                        table.setSeat1(seshInt);
                    }
                    invalidate();
                }else printToast("unable to reserve table " + table.getTableID());
            }else if(distance2 <= tSize){
                if(table.getSeat2() == 0){
                    if(table.getSeat1() == seshInt && table.getSeat3() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(seshInt);
                    table.setSeat2(seshInt);
                    try{
                        table.reserveSeat("seat2","0");
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 2 was changed before request was sent");
                        table.setSeat2(0);
                    }
                    invalidate();
                }else if(table.getSeat2() == seshInt){
                    if(table.getSeat1() == seshInt && table.getSeat3() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(0);
                    table.setSeat2(0);
                    try{
                        table.reserveSeat("seat2",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 2 was changed before request was sent");
                        table.setSeat2(seshInt);
                    }
                    invalidate();
                }else printToast("unable to reserve table " + table.getTableID());
            }else if(distance3 <= tSize){
                if(table.getSeat3() == 0){
                    if(table.getSeat1() == seshInt && table.getSeat2() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(seshInt);
                    table.setSeat3(seshInt);
                    try{
                        table.reserveSeat("seat3","0");
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 3 was changed before request was sent");
                        table.setSeat3(0);
                    }
                    invalidate();
                }else if(table.getSeat3() == seshInt){
                    if(table.getSeat1() == seshInt && table.getSeat2() == seshInt && table.getSeat4() == seshInt)   table.setTableStatus(0);
                    table.setSeat3(0);
                    try{
                        table.reserveSeat("seat3",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 3 was changed before request was sent");
                        table.setSeat3(seshInt);
                    }
                    invalidate();
                }else printToast("unable to reserve table " + table.getTableID());
            }else if(distance4 <= tSize){
                if(table.getSeat4() == 0){
                    if(table.getSeat1() == seshInt && table.getSeat2() == seshInt && table.getSeat3() == seshInt)   table.setTableStatus(seshInt);
                    table.setSeat4(seshInt);
                    try{
                        table.reserveSeat("seat4","0");
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 4 was changed before request was sent");
                        table.setSeat4(0);
                    }
                    invalidate();
                }else if(table.getSeat4() == seshInt){
                    if(table.getSeat1() == seshInt && table.getSeat2() == seshInt && table.getSeat3() == seshInt)   table.setTableStatus(0);
                    table.setSeat4(0);
                    try{
                        table.reserveSeat("seat4",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + ", seat 4 was changed before request was sent");
                        table.setSeat4(seshInt);
                    }
                    invalidate();
                }else printToast("unable to reserve table " + table.getTableID());
            }
        }else {
            int distance =(int)Math.sqrt((tableX-clickX)*(tableX-clickX) + (tableY-clickY)*(tableY-clickY));
            if(distance <= 2*tSize){
                if(table.getTableStatus() == 0){
                    table.setTableStatus(seshInt);
                    try{
                        table.reserveTable("tableStatus","0");
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + " was changed before request was sent");
                        table.setTableStatus(0);
                    }
                    invalidate();
                }
                else if(table.getTableStatus() == seshInt) {
                    table.setTableStatus(0);
                    try{
                        table.reserveTable("tableStatus",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        printToast("table " + table.getTableID() + " was changed before request was sent");
                        table.setTableStatus(seshInt);
                    }
                    invalidate();
                }else printToast("unable to reserve table " + table.getTableID());
            }
        }
    }

    public void setPartySize(int partySize)
    {
        this.partySize=partySize;
    }

    public void printToast(String text){
        Toast.makeText(MyContext.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        int action = ev.getAction();
        switch (action){
            case (MotionEvent.ACTION_DOWN):
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                clickStatus = true;
                clickX = ev.getX();
                clickY = ev.getY();
                invalidate();
                break;
            default:
        }
        return true;
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

    public TableMapView(Context context){
        super(context);

        init(null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableMapView(Context context, AttributeSet attrs){
        super(context, attrs);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableMapView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @TargetApi(21)
    public TableMapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
}
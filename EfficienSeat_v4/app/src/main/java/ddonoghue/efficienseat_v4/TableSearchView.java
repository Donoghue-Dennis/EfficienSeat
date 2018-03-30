package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.app.Activity;
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

import java.util.Calendar;
import java.util.TimeZone;

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
    int tSize = 100;
    float off = Math.round(tSize*1.65);
    float miniLabelOff = Math.round(off*0.1);
    int canvasWidth, canvasHeight;

    //foo
    Context currentContext;
    int partySize = 0;
    int seshInt;

    //scaling variables
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;



    private void init(@Nullable AttributeSet set){
        seshInt = getData("deviceId");
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

        //canvas.drawCircle(off, off, tSize, paintOpen);
        canvas.drawCircle((float)(canvasHeight/2), canvasWidth/2, tSize, paintReserved);
        //canvas.drawCircle((float)(canvasHeight*0.75), (float)(canvasWidth*0.75), tSize, paintError);
        //canvas.drawCircle(canvasHeight - off, canvasWidth - off, tSize, paintClaimed);

        canvas.restore();
    }

    public void drawTable(int tableX, int tableY, localTable table, Canvas canvas){
        //grab oft-used variables
        int tableStatus = table.getTableStatus();
        int seatOne = table.getSeat1();
        int seatTwo = table.getSeat2();
        int seatThree = table.getSeat3();
        int seatFour = table.getSeat4();

        //Draw central table
        //if table is unclaimed and party fits
        if(tableStatus == 0)
            canvas.drawCircle(tableX, tableY, tSize, paintOpen);
        //if table is reserved and partyFits
        else if(tableStatus == seshInt)
            canvas.drawCircle(tableX, tableY, tSize, paintReserved);
        //if table is claimed or party doesn't fit and table isn't errored
        else if(tableStatus >= 1)
            canvas.drawCircle(tableX, tableY, tSize, paintClaimed);
        //table is errored

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
        if(table.getTableAvail() > 0){
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
    }

    public void printToast(String text){
        Toast.makeText(currentContext, text, Toast.LENGTH_SHORT).show();
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
        currentContext = context;

        init(null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableSearchView(Context context, AttributeSet attrs){
        super(context, attrs);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public TableSearchView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @TargetApi(21)
    public TableSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
}

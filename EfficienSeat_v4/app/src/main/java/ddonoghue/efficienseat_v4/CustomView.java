package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by DDonoghue on 11/26/2017.z
 */

public class CustomView extends View{
    //paint color settings
    private Paint paintClaimed;
    private Paint paintReserved;
    private Paint paintOpen;
    private Paint paintError;
    private Paint paintText;

    //tables, table size, table offset
    List<localTable> tables = new ArrayList<>();
    int x1,x2,x3,x4,y1,y2,y3,y4;
    int tSize = 50;
    float off = Math.round(tSize*1.65);
    float miniOff = Math.round(off*0.1);
    int canvasWidth, canvasHeight;

    //foo
    Context currentContext;
    int partySize = 0;

    //click variables
    boolean clickStatus;
    float clickX,clickY;

    //scaling variables
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector mScaleDetector;

    public CustomView(Context context){
        super(context);
        currentContext = context;

        init(null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public CustomView(Context context, AttributeSet attrs){
        super(context, attrs);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @TargetApi(21)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);
        currentContext = context;

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

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
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //canvas scaling stuff
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        //establish canvas info
        canvasWidth = this.getWidth() - 200;
        canvasHeight = this.getHeight() - 200;
        int inBoundX = 0;
        int inBoundY = 0;
        double ratioX;
        double ratioY;
        double finalRatio;

        for(final localTable tempTable : tables){
            if(tempTable != null){
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

        for(final localTable tempTable : tables){
            if(tempTable != null){
                int x = ((int)(tempTable.getTableX() * finalRatio)) + 100;
                int y = canvasHeight-((int)(tempTable.getTableY() * finalRatio)) + 100;

                drawTable(x,y,tempTable,canvas);
            }
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
        else if(tableStatus >= 2 && partyFits)
            canvas.drawCircle(tableX, tableY, tSize, paintReserved);
        //if table is claimed or party doesn't fit and table isn't errored
        else if(tableStatus == 1 || (!partyFits))
            canvas.drawCircle(tableX, tableY, tSize, paintClaimed);
        //table is errored
        else canvas.drawCircle(tableX, tableY, tSize, paintError);
        //print label
        paintText.setTextSize(tSize);
        canvas.drawText(table.getTableID() + "",tableX-25, tableY+13, paintText);

        //draw seats
        if(partyFits && table.getTableType() == 0 && table.getTableAvail() > 0) {
            //adjust text size
            paintText.setTextSize(tSize/2);

            //Seat One
            x1 = (int)(tableX + (off * sin(toRadians(table.getTableAngle()-135))));
            y1 = (int)(tableY + (off * cos(toRadians(table.getTableAngle()-135))));
            if(seatOne == 0) {
                canvas.drawCircle(x1, y1, tSize/2, paintOpen);
            }else if(seatOne == 1){
                canvas.drawCircle(x1, y1, tSize/2, paintClaimed);
            }else if(seatOne >= 2){
                canvas.drawCircle(x1, y1, tSize/2, paintReserved);
            }
            canvas.drawText( "1",(x1 - miniOff), (y1 + miniOff), paintText);

            //Seat Two
            x2 = (int)(tableX + (off * sin(toRadians(table.getTableAngle()+135))));
            y2 = (int)(tableY + (off * cos(toRadians(table.getTableAngle()+135))));
            if(seatTwo == 0) {
                canvas.drawCircle(x2, y2, tSize/2, paintOpen);
            }else if(seatTwo == 1){
                canvas.drawCircle(x2, y2, tSize/2, paintClaimed);
            }else if(seatTwo >= 2){
                canvas.drawCircle(x2, y2, tSize/2, paintReserved);
            }
            canvas.drawText( "2",(x2 - miniOff), (y2 + miniOff), paintText);

            //Seat Three
            x3 = (int)(tableX + (off * sin(toRadians(table.getTableAngle()+45))));
            y3 = (int)(tableY + (off * cos(toRadians(table.getTableAngle()+45))));
            if(seatThree == 0) {
                canvas.drawCircle(x3, y3, tSize/2, paintOpen);
            }else if(seatThree == 1){
                canvas.drawCircle(x3, y3, tSize/2, paintClaimed);
            }else if(seatThree >= 2){
                canvas.drawCircle(x3, y3, tSize/2, paintReserved);
            }
            canvas.drawText( "3",(x3 - miniOff), (y3 + miniOff), paintText);

            //Seat Four
            x4 = (int)(tableX + (off * sin(toRadians(table.getTableAngle()-45))));
            y4 = (int)(tableY + (off * cos(toRadians(table.getTableAngle()-45))));
            if(seatFour == 0) {
                canvas.drawCircle(x4, y4, tSize/2, paintOpen);
            }else if(seatFour == 1){
                canvas.drawCircle(x4, y4, tSize/2, paintClaimed);
            }else if(seatFour >= 2){
                canvas.drawCircle(x4, y4, tSize/2, paintReserved);
            }
            canvas.drawText( "4",(x4 - miniOff), (y4 + miniOff), paintText);
        }

        if(clickStatus){
            clickHandler(tableX, tableY, table);
        }
    }

    public void addTable(localTable sentTable) {
        this.tables.add(sentTable);
    }

    public void addTables(List<localTable> sentTables) {
        for(localTable table : sentTables){
            if(!this.tables.contains(table))
                this.tables.add(table);
        }
    }

    public void clickHandler(int tableX, int tableY, localTable table){
        //check if table or seats were clicked.
        int seshInt = sessionID.getInstance().getID();
        int tableType = table.getTableType();
        if(tableType == 0){
            int distance1 = (int)Math.sqrt((x1-clickX)*(x1-clickX) + (y1-clickY)*(y1-clickY));
            int distance2 = (int)Math.sqrt((x2-clickX)*(x2-clickX) + (y2-clickY)*(y2-clickY));
            int distance3 = (int)Math.sqrt((x3-clickX)*(x3-clickX) + (y3-clickY)*(y3-clickY));
            int distance4 = (int)Math.sqrt((x4-clickX)*(x4-clickX) + (y4-clickY)*(y4-clickY));
            if(distance1 <= tSize){
                if(table.getSeat1() == 0){
                    table.setSeat1(seshInt);
                    try{
                        table.condWriteTable("seat1","0");
                    }catch(Exception e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 1 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat1(0);
                    }
                    invalidate();
                }else if(table.getSeat1() == seshInt){
                    table.setSeat1(0);
                    try{
                        table.condWriteTable("seat1",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 1 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat1(seshInt);
                    }
                    invalidate();
                }else Toast.makeText(currentContext, "unable to reserve table " + table.getTableID() + ", seat 1" , Toast.LENGTH_SHORT).show();
            }else if(distance2 <= tSize){
                if(table.getSeat2() == 0){
                    table.setSeat2(seshInt);
                    try{
                        table.condWriteTable("seat2","0");
                    }catch(Exception e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 2 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat2(0);
                    }
                    invalidate();
                }else if(table.getSeat2() == seshInt){
                    table.setSeat2(0);
                    try{
                        table.condWriteTable("seat2",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 2 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat2(seshInt);
                    }
                    invalidate();
                }else Toast.makeText(currentContext, "unable to reserve table " + table.getTableID() + ", seat 2" , Toast.LENGTH_SHORT).show();
            }else if(distance3 <= tSize){
                if(table.getSeat3() == 0){
                    table.setSeat3(seshInt);
                    try{
                        table.condWriteTable("seat3","0");
                    }catch(Exception e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 3 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat3(0);
                    }
                    invalidate();
                }else if(table.getSeat3() == seshInt){
                    table.setSeat3(0);
                    try{
                        table.condWriteTable("seat3",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 3 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat3(seshInt);
                    }
                    invalidate();
                }else Toast.makeText(currentContext, "unable to reserve table " + table.getTableID() + ", seat 3" , Toast.LENGTH_SHORT).show();
            }else if(distance4 <= tSize){
                if(table.getSeat4() == 0){
                    table.setSeat4(seshInt);
                    try{
                        table.condWriteTable("seat4","0");
                    }catch(Exception e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 4 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat4(0);
                    }
                    invalidate();
                }else if(table.getSeat4() == seshInt){
                    table.setSeat4(0);
                    try{
                        table.condWriteTable("seat4",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + ", seat 4 was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setSeat4(seshInt);
                    }
                    invalidate();
                }else Toast.makeText(currentContext, "unable to reserve table " + table.getTableID() + ", seat 4" , Toast.LENGTH_SHORT).show();
            }
        }else {
            int distance =(int)Math.sqrt((tableX-clickX)*(tableX-clickX) + (tableY-clickY)*(tableY-clickY));
            if(distance <= 2*tSize){
                if(table.getTableStatus() == 0){
                    table.setTableStatus(seshInt);
                    try{
                        table.condWriteTable("tableStatus","0");
                    }catch(Exception e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + " was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setTableStatus(0);
                    }
                    invalidate();
                }
                else if(table.getTableStatus() == seshInt) {
                    table.setTableStatus(0);
                    try{
                        table.condWriteTable("tableStatus",Integer.toString(seshInt));
                    }catch(ConditionalCheckFailedException e){
                        Toast.makeText(currentContext, "table " + table.getTableID() + " was changed before request was sent", Toast.LENGTH_SHORT).show();
                        table.setTableStatus(seshInt);
                    }
                    invalidate();
                }else Toast.makeText(currentContext, "unable to reserve table " + table.getTableID() , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setPartySize(int partySize)
    {
        this.partySize=partySize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Let the ScaleGestureDetector inspect all events.
        //mScaleDetector.onTouchEvent(ev);

        int action = ev.getAction();
        switch (action){
        case MotionEvent.ACTION_DOWN:
            clickStatus = true;
            clickX = ev.getX();
            clickY = ev.getY();
            break;
        case MotionEvent.ACTION_UP:
            invalidate();
            break;
        case MotionEvent.ACTION_CANCEL:
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
}
package com.ranosys.andropaint.view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ranosys.andropaint.HomeActivity;
import com.ranosys.andropaint.PaintActivity;
import com.ranosys.andropaint.R;
import com.ranosys.andropaint.algorithm.FloodFill;
import com.ranosys.andropaint.algorithm.QueueLinearFloodFiller;
import com.ranosys.andropaint.callbacks.FloodFillCallback;
import com.ranosys.andropaint.constants.Constants;
import com.ranosys.andropaint.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.util.Stack;

/**
 * Created by Anhad on 7/10/15.
 */
public class DrawingView extends View {

    private final String TAG = DrawingView.class.getSimpleName();
    public int flag = Constants.drawPath;
    private float top, left, right, bottom;
    private Path drawPath, pointPath;
    private Paint drawPaint, canvasPaint, myPaint,textPaint,linePaint, eraserPaint;
    private int paintColor = Color.BLACK, eraserColor;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private float circleStartX, circleStartY,radius,textX,textY,lineXS,lineYS,lineXEnd,lineYEnd;
    private DrawingView mDrawingView;
    private boolean checkResize = true;
    private Context mContext;
    public Stack<String> bitmapArrayList = new Stack<>();
    public Stack<String> redoBitmapArrayList = new Stack<>();
    private String sharedPreferenceString;
    private ImageView imageView;
    private RelativeLayout.LayoutParams imageViewLayoutParams;
    private RelativeLayout relativeLayout;
    private Paint erasePaint = new Paint();

    public DrawingView(Context context) {
        super(context);
        Log.d(TAG, " Constructor called");
        this.mContext = context;
        setUpDrawingMethod();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDrawingMethod();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpDrawingMethod();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUpDrawingMethod();
    }

    public void setContext(Context context, DrawingView dr) {
        if(context instanceof PaintActivity){
            this.mContext = context;
            Log.d("Context","Entered");
        }
        mDrawingView = dr;
        imageView = ((PaintActivity)(mContext)).getImageViewRef();
        relativeLayout = ((PaintActivity)(mContext)).getRelativeLayoutRef();
        setColor("#FF000000");
        imageViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setContext(Context context, DrawingView dr, String preferenceString) {
        this.mContext = context;
        mDrawingView = dr;
        sharedPreferenceString = preferenceString;
        imageView = ((PaintActivity)(mContext)).getImageViewRef();
        relativeLayout = ((PaintActivity)(mContext)).getRelativeLayoutRef();
        imageViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setUpDrawingMethod() {

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPath = new Path();
        pointPath = new Path();
        drawPaint = new Paint();
        myPaint = new Paint();
        textPaint = new Paint();
        linePaint = new Paint();
        eraserPaint = new Paint();

        erasePaint.setColor(Color.TRANSPARENT);

        eraserPaint.setStrokeWidth(brushSize);
        eraserPaint.setColor(eraserColor);
        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);


        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(paintColor);
        myPaint.setStrokeWidth(brushSize);
        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(brushSize);

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(paintColor);
        textPaint.setTextSize(40);

        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(paintColor);
        linePaint.setStrokeWidth(brushSize);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeWidth(brushSize);

        canvasPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(checkResize)
        {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            this.setDrawingCacheEnabled(true);
            if(sharedPreferenceString != null && !sharedPreferenceString.equals("")) {
                drawCanvas.drawBitmap(stringToBitMap(sharedPreferenceString), 0, 0, null);
                Toast.makeText(mContext,"This is your previous work",Toast.LENGTH_LONG).show();
            }
           // getDrawingCacheBitmap();
           // addOverallScreen();
            checkResize = false;
        }
    }

    public void changeCanvasColor(int color)
    {
        setBackgroundColor(color);
        eraserColor = color;
        eraserPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (flag  == Constants.drawRectangle) {
            myPaint.setStyle(Paint.Style.STROKE);
            canvas.drawBitmap(canvasBitmap, 0, 0, myPaint);

            if(top<bottom)
            {
                if(left<right)
                    canvas.drawRect(left, top, right, bottom, myPaint);
                else if(right<left)
                    canvas.drawRect(right, top, left, bottom, myPaint);
            }
            else
            {
                if(left<right)
                    canvas.drawRect(left, bottom, right, top, myPaint);
                else if(right<left)
                    canvas.drawRect(right, bottom, left, top, myPaint);
            }
        }
        else if(flag == Constants.drawOval)
        {
            myPaint.setStyle(Paint.Style.STROKE);
            canvas.drawBitmap(canvasBitmap, 0, 0, myPaint);

            if(top<bottom)
            {
                if(left<right)
                    canvas.drawOval(new RectF(left, top, right, bottom), myPaint);
                else
                    canvas.drawOval(new RectF(right, top, left, bottom), myPaint);
            }
            else
            {
                if(left<right)
                    canvas.drawOval(new RectF(left, bottom, right, top), myPaint);
                else
                    canvas.drawOval(new RectF(right, bottom, left, top), myPaint);
            }
        }
        /*else if(flag == Constants.eraseCanvas)
        {

            *//*canvas.drawBitmap(canvasBitmap, 0, 0, eraserPaint);
            canvas.drawPath(drawPath, eraserPaint);*//*
*//*
            changeImageViewParams((int)left+15, (int)top+15);
*//*

//            Log.d("Line", "Line Called");
        }*/
        else if(flag == Constants.drawCircle)
        {
            myPaint.setStyle(Paint.Style.STROKE);
            canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
            canvas.drawCircle(circleStartX, circleStartY, Math.abs(radius), drawPaint);
            Log.d("MAINACTIVITY ", "x:" + circleStartX + " y:" + circleStartY + " radius:" + Math.abs(radius));
        }
        else if(flag == Constants.drawText)
        {
            textPaint.setColor(paintColor);
            canvas.drawBitmap(canvasBitmap, 0, 0, textPaint);
            canvas.drawText("This is sample text.", textX, textY, textPaint);
        }
        else if(flag == Constants.drawLine)
        {
            canvas.drawBitmap(canvasBitmap, 0, 0, linePaint);
            canvas.drawLine(lineXS, lineYS, lineXEnd, lineYEnd, linePaint);
        }
        else{
            if (!erase) {
                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
                canvas.drawPath(drawPath, drawPaint);
                //Log.d("Line","Line Called");
            }else {

                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

                canvas.drawPath(drawPath, erasePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float touchX = event.getX();
        final float touchY = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                Log.d("MAINACTIVITY ","X CO "+touchX+" Y CO"+touchY);
                if(flag == Constants.drawPath) {

                    if (!erase) {
                        pointPath.moveTo(touchX, touchY);
                        pointPath.lineTo(touchX + 1, touchY + 1);
                        drawCanvas.drawPath(pointPath, drawPaint);
                        // getDrawingCacheBitmap();
                        // addOverallScreen();
                        pointPath.reset();
                        drawPath.moveTo(touchX, touchY);
                    } else {
                        pointPath.moveTo(touchX, touchY);
                        pointPath.lineTo(touchX + 1, touchY + 1);
                        drawCanvas.drawPath(pointPath, drawPaint);
                        // getDrawingCacheBitmap();
                        // addOverallScreen();
                        pointPath.reset();
                        changeImageViewParams((int)touchX,(int)touchY);
                        drawPath.moveTo(touchX, touchY);
                    }
                }
                /*else if(flag == Constants.eraseCanvas)
                {

                    changeImageViewParams((int)touchX, (int)touchY);

                    eraserPaint.setColor(eraserColor);

                    pointPath.moveTo(touchX-10, touchY-15);
                    pointPath.lineTo(touchX - 10, touchY - 15);

                    drawCanvas.drawPath(pointPath, eraserPaint);



                    // getDrawingCacheBitmap();
                    // addOverallScreen();

                    pointPath.reset();

                    drawPath.moveTo(touchX, touchY);
                }*/
                else if(flag == Constants.drawCircle)
                {
                    radius = 2.0f;
                    circleStartX = touchX;
                    circleStartY = touchY;
                }
                else if(flag == Constants.drawOval){
                    left = touchX;
                    top = touchY;
                }
                else if(flag == Constants.drawRectangle)
                {
                    left = touchX;
                    top = touchY;
                }
                else if(flag == Constants.drawText)
                {
                    textX = touchX;
                    textY = touchY;
                }
                else if(flag == Constants.drawLine)
                {
                    lineXS = touchX;
                    lineYS = touchY;
                    lineXEnd = touchX;
                    lineYEnd = touchY;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (flag == Constants.drawPath ) {

                    if(!erase) {
                        drawPath.lineTo(touchX, touchY);
                    }
                    else
                    {
/*
                    drawPath.moveTo(touchX, touchY);
*/                          changeImageViewParams((int)touchX,(int)touchY);
                        drawCanvas.drawBitmap(canvasBitmap, 0, 0, null);

                        drawPath.lineTo(touchX + 1, touchY + 1);

                        drawCanvas.drawPath(drawPath, drawPaint);
                        // getDrawingCacheBitmap();
                        // addOverallScreen();
//                        pointPath.reset();
                    }
                }

                /*if (flag == Constants.eraseCanvas ) {
                    changeImageViewParams((int)touchX-10, (int)touchY-15);
                    drawPath.lineTo(touchX-10, touchY-15);
                }*/
                else if(flag == Constants.drawCircle)
                {
                    radius = ((touchX-circleStartX) + (touchY-circleStartY)) / 2;
                }
                else if(flag == Constants.drawRectangle){
                    right = touchX;
                    bottom = touchY;
                }
                else if(flag == Constants.drawOval){
                    right = touchX;
                    bottom = touchY;
                }
                else if(flag == Constants.drawLine){
                    lineXEnd = touchX;
                    lineYEnd = touchY;
                }
                else {}
                break;

            case MotionEvent.ACTION_UP:
                if(flag == Constants.drawRectangle){
                    setErase(false);

                    if(left<right)
                        drawCanvas.drawRect(left, top, right, bottom, drawPaint);
                    else
                        drawCanvas.drawRect(right,top,left,bottom,drawPaint);
                    // getDrawingCacheBitmap();
                  //  addOverallScreen();
                    flag = Constants.drawPath;
                }
                else if(flag == Constants.drawFloodFill)
                {
                    final Point p1 = new Point();
                    p1.x=(int) touchX; //x co-ordinate where the user touches on the screen
                    p1.y=(int) touchY; //y co-ordinate where the user touches on the screen
                    //final FloodFill f= new FloodFill();

                    final QueueLinearFloodFiller queueLinearFloodFiller = new QueueLinearFloodFiller(canvasBitmap,canvasBitmap.getPixel(p1.x, p1.y), paintColor);

                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            Log.i(TAG, " Progress started");
                            Utility.showProgress(mContext);
                        }
                        @Override
                        protected Void doInBackground(Void... params) {
                            Log.i(TAG, " Filling started");
                            //f.floodFillNew(canvasBitmap, p1, canvasBitmap.getPixel(p1.x, p1.y), paintColor);
                            queueLinearFloodFiller.setTolerance(0);
                            queueLinearFloodFiller.floodFill((int)touchX,(int)touchY);
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            invalidate();
                            Utility.hideProgress();
                            setErase(false);
                            Log.i(TAG, " Filling Done");
                            flag = Constants.drawPath;
                        }
                    }.execute();


                    //f.floodFill(canvasBitmap, p1, canvasBitmap.getPixel(p1.x, p1.y), paintColor);

                    // getDrawingCacheBitmap();
                   // addOverallScreen();
                }
                else if(flag == Constants.drawOval){

                    setErase(false);
                    if(left<right)
                        drawCanvas.drawOval(new RectF(left, top, right, bottom), myPaint);
                    else
                        drawCanvas.drawOval(new RectF(right, top, left, bottom), myPaint);
                    //getDrawingCacheBitmap();
                   // addOverallScreen();
                    flag = Constants.drawPath;
                }
                else if(flag == Constants.drawCircle)
                {
                    setErase(false);

                    drawCanvas.drawCircle(circleStartX, circleStartY, Math.abs(radius), drawPaint);
                   // getDrawingCacheBitmap();
                   // addOverallScreen();
                    flag = Constants.drawPath;
                }
                else if(flag == Constants.drawText)
                {
                    setErase(false);
                    drawCanvas.drawText("This is sample text.", textX, textY, textPaint);
                   // getDrawingCacheBitmap();
                   // addOverallScreen();
                    flag = Constants.drawPath;
                }

                else if(flag == Constants.drawLine)
                {
                    setErase(false);
                    drawCanvas.drawLine(lineXS,lineYS,lineXEnd,lineYEnd,drawPaint);
                   // getDrawingCacheBitmap();
                    //addOverallScreen();
                    flag = Constants.drawPath;
                    lineXS=0;
                    lineYS=0;
                    lineXEnd=0;
                    lineYEnd = 0;
                }
                /*else if(flag == Constants.eraseCanvas)
                {
                   *//* drawCanvas.drawPath(drawPath, eraserPaint);
                    drawPath.reset();
                    imageView.setVisibility(INVISIBLE);*//*

                }*/
                else{

                    if(!erase) {
                        drawCanvas.drawPath(drawPath, drawPaint);
                        // getDrawingCacheBitmap();
                        // addOverallScreen();
                        drawPath.reset();
                    }
                    else
                    {
                       // changeImageViewParams((int)touchX,(int)touchY);
                        imageView.setVisibility(GONE);
                        drawCanvas.drawPath(drawPath, drawPaint);
                        // getDrawingCacheBitmap();
                        // addOverallScreen();
                        drawPath.reset();
                    }
                }
                break;

            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        myPaint.setColor(paintColor);
        textPaint.setColor(paintColor);
        linePaint.setColor(paintColor);
        eraserPaint.setColor(eraserColor);
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
        eraserPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else{
            //drawPaint.setColor(paintColor);
            drawPaint.setXfermode(null);
            if(flag != Constants.drawFloodFill) {
                flag = Constants.drawPath;
            }
        }
    }


    private void changeImageViewParams(int left,int top)
    {
        imageViewLayoutParams.leftMargin = left-15;
        imageViewLayoutParams.topMargin = top-10;
        imageView.setVisibility(VISIBLE);
        imageView.setLayoutParams(imageViewLayoutParams);
    }

    private void invalidateVariables()
    {
        top=0;
        left=0;
        bottom=0;
        right=0;
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        top=0;
        left=0;
        bottom=0;
        right= 0;
        setColor("#FF000000");
        setBackgroundColor(Color.WHITE);
        //setErase(false);
        invalidate();
        removeFrame(mDrawingView);
    }

    public void addFrame(FrameLayout borderFrameLayout, ImageView borderImageView, DrawingView drawingView) {
        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)drawingView.getLayoutParams();
        lpt.setMargins(60, 60, 60, 60);
        drawingView.setLayoutParams(lpt);
        borderImageView.setImageResource(R.drawable.red_border_frame);
    }

    public void removeFrame(DrawingView drawingView) {
        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)drawingView.getLayoutParams();
        lpt.setMargins(0, 0, 0, 0);
        drawingView.setLayoutParams(lpt);
    }

    public void undo()
    {
        if(!bitmapArrayList.empty())
        {
            redoBitmapArrayList.push(bitmapArrayList.pop());
        }
        if(!bitmapArrayList.empty())
        {
            drawCanvas.drawBitmap(stringToBitMap(bitmapArrayList.peek()), 0, 0, drawPaint);
        }
    }

    public void redo() {
        /*if(!redoBitmapArrayList.empty())
            bitmapArrayList.push(redoBitmapArrayList.pop());*/

        if(!redoBitmapArrayList.empty())
        {
            bitmapArrayList.push(redoBitmapArrayList.pop());
        }
        if(!redoBitmapArrayList.empty())
        {
            drawCanvas.drawBitmap(stringToBitMap(redoBitmapArrayList.peek()), 0, 0, drawPaint);

        }

       /* if (!redoBitmapArrayList.empty()) {
            drawCanvas.drawBitmap(redoBitmapArrayList.peek(), 0, 0, drawPaint);
            redoBitmapArrayList.pop();
        }*/
    }

    public  Bitmap getDrawingCacheBitmap()
    {
        mDrawingView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mDrawingView.getDrawingCache());
        //     bitmapArrayList.push(bitmap);
        bitmapArrayList.push(bitMapToString(bitmap));
        bitmap = null;
        mDrawingView.destroyDrawingCache();
        return bitmap;
    }


    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap stringToBitMap(String encodedString){

        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void addOverallScreen()
    {
        mDrawingView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mDrawingView.getDrawingCache());
        if(bitmapArrayList.size()>0) {
            bitmapArrayList.pop();
        }
        bitmapArrayList.push(bitMapToString(bitmap));
        mDrawingView.destroyDrawingCache();
    }

    public String getOverallScreen()
    {
        String temp = bitmapArrayList.peek();
        bitmapArrayList.clear();
        return temp;
    }

    public void clearStack()
    {
        if(bitmapArrayList.size()>0)
        bitmapArrayList.clear();
    }
}

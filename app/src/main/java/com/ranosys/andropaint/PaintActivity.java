package com.ranosys.andropaint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ranosys.andropaint.constants.Constants;
import com.ranosys.andropaint.utility.Utility;
import com.ranosys.andropaint.view.DrawingView;

import java.util.ArrayList;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = PaintActivity.class.getSimpleName();
    private static DrawingView drawView;
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, floodFill;
    public FrameLayout mFrameLayout;
    public ImageView mFrameImageView;
    public static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private AlertDialog.Builder builder;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private RelativeLayout relativeLayout;
    public String stringFromPreferences;
    private ImageView showColors, showMenus, showShapes;
    private LinearLayout menuDrawerLinearLayout, shapeDrawerLinearLayout, colorDrawerLinearLayout;
    private ImageButton line, rectangle, circle, oval, frame, canvasColor;
    private static boolean isBrushColor = true;
    private   ImageView eraserImageView;
    private ProgressDialog progressDialog;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        initVariables();
        applyListeners();
        //getImageFromPreference();
        mContext = this;
        drawView.setContext(mContext, drawView);

    }

    private void initVariables()
    {

        rectangle = (ImageButton) findViewById(R.id.rectangle);
        line = (ImageButton) findViewById(R.id.line);
        circle = (ImageButton) findViewById(R.id.circle);
        oval = (ImageButton) findViewById(R.id.oval);
        frame = (ImageButton) findViewById(R.id.frame);
        canvasColor = (ImageButton) findViewById(R.id.change_canvas_button);
        showColors = (ImageView) findViewById(R.id.open_color_button);
        showShapes = (ImageView) findViewById(R.id.open_shape_button);
        showMenus = (ImageView) findViewById(R.id.open_menu_button);
        menuDrawerLinearLayout = (LinearLayout) findViewById(R.id.menu_drawer);
        shapeDrawerLinearLayout = (LinearLayout) findViewById(R.id.shape_drawer);
        colorDrawerLinearLayout = (LinearLayout) findViewById(R.id.paint_color_drawer);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        floodFill = (ImageButton) findViewById(R.id.fill_boundary);
        relativeLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        eraserImageView = (ImageView) findViewById(R.id.eraserImage);
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mFrameImageView = (ImageView) findViewById(R.id.borderImageView);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        drawView = (DrawingView)findViewById(R.id.drawing);
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            drawView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawView.setBrushSize(mediumBrush);
    }

    private void applyListeners()
    {
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.draw_btn) {
                    final Dialog brushDialog = new Dialog(PaintActivity.this);
                    brushDialog.setTitle("Brush size:");
                    brushDialog.setContentView(R.layout.brush_chooser);
                    ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                    smallBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setBrushSize(smallBrush);
                            drawView.setLastBrushSize(smallBrush);
                            drawView.setErase(false);
                            brushDialog.dismiss();
                        }
                    });

                    ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                    mediumBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setBrushSize(mediumBrush);
                            drawView.setLastBrushSize(mediumBrush);
                            drawView.setErase(false);
                            brushDialog.dismiss();
                        }
                    });

                    ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
                    largeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setBrushSize(largeBrush);
                            drawView.setLastBrushSize(largeBrush);
                            drawView.setErase(false);
                            brushDialog.dismiss();
                        }
                    });
                    brushDialog.show();
                }
            }
        });

        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.erase_btn){
                    final Dialog brushDialog = new Dialog(PaintActivity.this);
                    brushDialog.setTitle("Eraser size:");
                    brushDialog.setContentView(R.layout.brush_chooser);

                    ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                    smallBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setErase(true);
                            drawView.setBrushSize(smallBrush);

                            brushDialog.dismiss();
                        }
                    });
                    ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                    mediumBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setErase(true);
                            drawView.setBrushSize(mediumBrush);
                            brushDialog.dismiss();
                        }
                    });
                    ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                    largeBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.setErase(true);
                            drawView.setBrushSize(largeBrush);
                            brushDialog.dismiss();
                        }
                    });
                    brushDialog.show();
                }
            }
        });

        newBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.new_btn){
                    AlertDialog.Builder newDialog = new AlertDialog.Builder(PaintActivity.this);
                    newDialog.setTitle("New drawing");
                    newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                    newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            drawView.startNew();
                            dialog.dismiss();
                        }
                    });
                    newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    newDialog.show();

                }
            }
        });

        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.save_btn){
                    AlertDialog.Builder saveDialog = new AlertDialog.Builder(PaintActivity.this);
                    saveDialog.setTitle("Save drawing");
                    saveDialog.setMessage("Save drawing to device Gallery?");
                    saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(DialogInterface dialog, int which){
                            mFrameLayout.setDrawingCacheEnabled(true);

                            int permissionCheck = ContextCompat.checkSelfPermission(PaintActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            if(permissionCheck == PackageManager.PERMISSION_DENIED)
                            {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                            }
                            else{
                                saveImage();
                            }
                            mFrameLayout.destroyDrawingCache();
                        }
                    });
                    saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel();
                        }
                    });
                    saveDialog.show();
                }
            }
        });

        showColors.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorDrawerLinearLayout.getVisibility() == View.GONE) {
                    colorDrawerLinearLayout.setVisibility(View.VISIBLE);
                }
                else{
                    colorDrawerLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        showMenus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuDrawerLinearLayout.getVisibility() == View.GONE) {
                    menuDrawerLinearLayout.setVisibility(View.VISIBLE);
                }
                else{
                    menuDrawerLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        showShapes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shapeDrawerLinearLayout.getVisibility() == View.GONE) {
                    shapeDrawerLinearLayout.setVisibility(View.VISIBLE);
                }
                else{
                    shapeDrawerLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        rectangle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.flag = Constants.drawRectangle;
                Utility.showToast(mContext,"Drag to draw rectangle");

            }
        });

        circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.flag = Constants.drawCircle;
                Utility.showToast(mContext,"Drag to draw circle");

            }
        });

        line.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.flag = Constants.drawLine;
                Utility.showToast(mContext,"Drag to draw line");

            }
        });

        oval.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.flag = Constants.drawOval;
                Utility.showToast(mContext,"Drag to draw oval");

            }
        });

        frame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(false);
                drawView.addFrame(mFrameLayout, mFrameImageView, drawView);
            }
        });

        canvasColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isBrushColor = false;
                colorDrawerLinearLayout.setVisibility(View.VISIBLE);
                Utility.showToast(mContext, "Select color to change canvas color");

            }
        });

        floodFill.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.flag = Constants.drawFloodFill;
                colorDrawerLinearLayout.setVisibility(View.VISIBLE);
                Utility.showToast(mContext, "Select color & Tap on the area to fill color");

            }
        });

    }

    private void getImageFromPreference()
    {
         /*sharedPreferences = getSharedPreferences("andropaintresumestate", MODE_PRIVATE);

        try
        {
            stringFromPreferences = sharedPreferences.getString("resumedbitmap", null);
            Log.d("String"," "+stringFromPreferences);
        }
        catch (NullPointerException npe)
        {
            Log.d(TAG," SHARED PREFERENCE RETURNING NULL");
        }

        if(!TextUtils.isEmpty(stringFromPreferences) && stringFromPreferences!=null)
        {
            drawView.setContext(this, drawView, stringFromPreferences);
        }
        else
        {
            drawView.setContext(this, drawView);
        }*/
    }

    public RelativeLayout getRelativeLayoutRef()
    {
        return relativeLayout;
    }

    public ImageView getImageViewRef()
    {
        return eraserImageView;
    }

    public void paintClicked(View view){
        if(view!=currPaint){
            if(isBrushColor)
            {
                drawView.setErase(false);
                drawView.setBrushSize(drawView.getLastBrushSize());
                ImageButton imgView = (ImageButton)view;
                String color = view.getTag().toString();
                drawView.setColor(color);
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint=(ImageButton)view;
            }
            else {
                String color = view.getTag().toString();
                drawView.changeCanvasColor(Color.parseColor(color));
                isBrushColor = true;
            }
        }
        else
        {
            if (!isBrushColor)
            {
                String color = view.getTag().toString();
                drawView.changeCanvasColor(Color.parseColor(color));
                isBrushColor = true;
            }
        }
    }

    public void saveImage()
    {
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), mFrameLayout.getDrawingCache(),
                UUID.randomUUID().toString()+".png", "drawing");

        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }
    }

    public void saveImage(Bitmap bitmap)
    {

        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), bitmap,
                UUID.randomUUID().toString()+".png", "drawing");

        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d("MAINACTIVITY ", "Permission Granted");

        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {}
                return;
            }
        }
    }

    public static Bitmap getDrawingCacheBitmap()
    {
        drawView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(drawView.getDrawingCache());
        bitmapArrayList.add(bitmap);
        drawView.destroyDrawingCache();
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.app_closing_dialog, null, false);
        builder.setView(view);
        builder.show();

        final AlertDialog alertDialog = builder.create();
        Button saveThisWorkButton = (Button) view.findViewById(R.id.saveThisWorkButton);
        Button dontSaveThisWorkButton = (Button) view.findViewById(R.id.dontSaveThisWorkButton);

        saveThisWorkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                editor.putString("resumedbitmap", "");
                editor.putString("resumedbitmap", drawView.getOverallScreen());
                editor.commit();
                alertDialog.dismiss();
                finish();
            }
        });

        dontSaveThisWorkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clearStack();
                editor = sharedPreferences.edit();
                editor.putString("resumedbitmap", "");
                editor.commit();
                alertDialog.dismiss();
                finish();
            }
        });*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_rectangle) {
            drawView.flag = Constants.drawRectangle;
            return true;
        }


        if (id == R.id.action_circle) {
            drawView.flag = Constants.drawCircle;
            return true;
        }

        if (id == R.id.action_oval) {
            drawView.flag = Constants.drawOval;
            return true;
        }

        if (id == R.id.action_line) {
            drawView.flag = Constants.drawLine;
            return true;
        }

        if (id == R.id.action_text) {
            drawView.flag = Constants.drawText;
            return true;
        }

        if (id == R.id.action_fill) {
            drawView.flag = Constants.drawFloodFill;
            return true;
        }

        if(id == R.id.action_add_frame){
            drawView.setErase(false);
            drawView.addFrame(mFrameLayout, mFrameImageView, drawView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}

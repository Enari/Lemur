package com.g10.lemur.Accelerometer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.g10.lemur.Altimeter.Altimeter;
import com.g10.lemur.Decibel.Decibel;
import com.g10.lemur.Help.Help;
import com.g10.lemur.MainActivity;
import com.g10.lemur.R;
import com.g10.lemur.Settings.Settings;
import com.g10.lemur.Speedometer.Speedometer;
import com.g10.lemur.Vision.Vision;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;

import static com.g10.lemur.R.id.YGraph;
import static com.g10.lemur.R.id.ZGraph;

public class Accelerometer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener
{
    NavigationView navigationView;

    //To be able to Swap
    private ImageView xDirectionIcon, yDirectionIcon, zDirectionIcon;
    private CardView xGraphCard, yGraphCard, zGraphCard;
    private ImageView xSwapper, ySwapper, zSwapper;
    private TextView titleText;
    private Drawable iconX, iconY,iconZ, iconXpressed, iconYpressed, iconZpressed;

    //Sensor related declarations
    private TextView XaxisText, YaxisText, ZaxisText;
    private Sensor accSensor;
    private SensorManager SM;
    private double lessFloatX;
    private double lessFloatY;
    private double lessFloatZ;

    //Graph related declarations
    private double yValueXaxis;
    private double yValueYaxis;
    private double yValueZaxis;

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    static GraphView graphX;
    static GraphView graphY;
    static GraphView graphZ;
    static LineGraphSeries<DataPoint> seriesX;
    static LineGraphSeries<DataPoint> seriesY;
    static LineGraphSeries<DataPoint> seriesZ;
    long activityCreateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the current activity as marked in the menu
        navigationView.setCheckedItem(R.id.menuAcc);

        //Find so we can swap them
        iconX = ContextCompat.getDrawable(getApplicationContext(),R.drawable.x);
        iconY = ContextCompat.getDrawable(getApplicationContext(),R.drawable.y);
        iconZ = ContextCompat.getDrawable(getApplicationContext(),R.drawable.z);
        iconXpressed = ContextCompat.getDrawable(getApplicationContext(),R.drawable.xpressed);
        iconYpressed = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ypressed);
        iconZpressed = ContextCompat.getDrawable(getApplicationContext(),R.drawable.zpressed);
        titleText = (TextView)findViewById(R.id.mtextViewXTitle);
        xDirectionIcon = (ImageView)findViewById(R.id.XDirectionIcon);
        yDirectionIcon = (ImageView)findViewById(R.id.YDirectionIcon);
        zDirectionIcon = (ImageView)findViewById(R.id.ZDirectionIcon);
        xGraphCard = (CardView)findViewById(R.id.cardviewXGraph);
        yGraphCard = (CardView)findViewById(R.id.cardviewYGraph);
        zGraphCard = (CardView)findViewById(R.id.cardviewZGraph);
        xSwapper = (ImageView)findViewById(R.id.swapperX);
        ySwapper = (ImageView)findViewById(R.id.swapperY);
        zSwapper = (ImageView)findViewById(R.id.swapperZ);

        //Create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        accSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Start Listening to sensor
        SM.registerListener(this,accSensor,SensorManager.SENSOR_DELAY_UI);

        //Assign TextViews for accelerometer data
        XaxisText = (TextView)findViewById(R.id.XDataText);
        YaxisText = (TextView)findViewById(R.id.YDataText);
        ZaxisText = (TextView)findViewById(R.id.ZDataText);

        //Graph X
        graphX = (GraphView)findViewById(R.id.XGraph);
        seriesX = new LineGraphSeries<>();
        graphX.addSeries(seriesX);

        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMinX(0);
        graphX.getViewport().setMaxX(10000);
        //graphX.getViewport().setYAxisBoundsManual(true);
        graphX.getViewport().setMinY(-40);
        graphX.getViewport().setMaxY(40);
        graphX.getGridLabelRenderer().setNumHorizontalLabels(4);

        //Graph Y
        graphY = (GraphView)findViewById(YGraph);
        seriesY = new LineGraphSeries<>();
        graphY.addSeries(seriesY);

        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMinX(0);
        graphY.getViewport().setMaxX(10000);
        //graphY.getViewport().setYAxisBoundsManual(true);
        graphY.getViewport().setMinY(-40);
        graphY.getViewport().setMaxY(40);
        graphY.getGridLabelRenderer().setNumHorizontalLabels(4);

        //Graph z
        graphZ = (GraphView)findViewById(ZGraph);
        seriesZ = new LineGraphSeries<>();
        graphZ.addSeries(seriesZ);

        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMinX(0);
        graphZ.getViewport().setMaxX(10000);
        //graphZ.getViewport().setYAxisBoundsManual(true);
        graphZ.getViewport().setMinY(-40);
        graphZ.getViewport().setMaxY(40);
        graphZ.getGridLabelRenderer().setNumHorizontalLabels(4);


        //X Axis Graph Label format
        graphX.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    return df.format(value/1000)+"s";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        //y Axis Graph Label Format
        graphY.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    return df.format(value/1000)+"s";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        //z Axis Graph Format
        graphZ.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    return df.format(value/1000)+"s";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        xSwapper.setImageDrawable(iconXpressed);
        activityCreateTime = System.currentTimeMillis();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        navigationView.setCheckedItem(R.id.menuAcc);
    }
    @Override
    public void onPause(){
        super.onPause(); //always call superclass method first

            mHandler.removeCallbacks(mTimer);

            SM.unregisterListener(this,accSensor);

    }
    @Override
    public void onResume(){
        super.onResume();

        navigationView.setCheckedItem(R.id.menuAcc);

        SM.registerListener(this,accSensor,SensorManager.SENSOR_DELAY_UI);


        mTimer = new Runnable()
        {
            @Override
            public void run()
            {
                yValueXaxis = lessFloatX;
                yValueYaxis = lessFloatY;
                yValueZaxis = lessFloatZ;
                seriesX.appendData(newDatapoint(yValueXaxis), true, 100);
                seriesY.appendData(newDatapoint(yValueYaxis), true, 100);
                seriesZ.appendData(newDatapoint(yValueZaxis), true, 100);
                graphX.onDataChanged(true, false);
                graphY.onDataChanged(true, false);
                graphZ.onDataChanged(true, false);
                mHandler.postDelayed(this, 100);
            }
        };
        mHandler.postDelayed(mTimer, 100);
    }

    @Override
    public void onBackPressed()
    {
        // Physical back button pressed

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Right sub-menu.
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menuHome)
        {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuVision)
        {
            // Go to Google Vision
            intent = new Intent(this, Vision.class);
            startActivity(intent);
        }
        else if (id == R.id.menuAlti)
        {
            // Go to altimeter
            intent = new Intent(this, Altimeter.class);
            startActivity(intent);
        }
        else if (id == R.id.menuSpeed)
        {
            // Go to speedometer
            intent = new Intent(this, Speedometer.class);
            startActivity(intent);
        }
        else if (id == R.id.menuAcc)
        {
            // Stay here
        }
        else if (id == R.id.menuSound)
        {
            // Go to decibel
            intent = new Intent(this, Decibel.class);
            startActivity(intent);
        }
        else if (id == R.id.menuHelp)
        {
            // Go to help
            intent = new Intent(this, Help.class);
            startActivity(intent);
        }
        else if (id == R.id.menuSettings)
        {
            // Go to Settings
            intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //funktion som rundar av till *int precision* antal decimaler
    //Ex Math.pow(10,1) = 10^1, Math.round(9.83827 * 10) = 98, 98/10 = 9.8
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lessFloatX = round(sensorEvent.values[0],1);
        lessFloatY = round(sensorEvent.values[1],1);
        lessFloatZ = round(sensorEvent.values[2],1);
        XaxisText.setText(String.valueOf(lessFloatX));
        YaxisText.setText(String.valueOf((lessFloatY)));
        ZaxisText.setText(String.valueOf((lessFloatZ)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not in use
    }
    private DataPoint newDatapoint(double y)
    {
        double timeSince = System.currentTimeMillis() - activityCreateTime;
        return new DataPoint(timeSince, y);
    }
    public void SwapViews(View view){
        if (view == xSwapper)
        {
            xSwapper.setImageDrawable(iconXpressed);
            ySwapper.setImageDrawable(iconY);
            zSwapper.setImageDrawable(iconZ);

            titleText.setText(getResources().getString(R.string.squared_symbolX));
            XaxisText.setVisibility(View.VISIBLE);
            xDirectionIcon.setVisibility(View.VISIBLE);
            xGraphCard.setVisibility(View.VISIBLE);

            YaxisText.setVisibility(View.INVISIBLE);
            yDirectionIcon.setVisibility(View.INVISIBLE);
            yGraphCard.setVisibility(View.INVISIBLE);

            ZaxisText.setVisibility(View.INVISIBLE);
            zDirectionIcon.setVisibility(View.INVISIBLE);
            zGraphCard.setVisibility(View.INVISIBLE);
        }
        else if(view == ySwapper)
        {
            xSwapper.setImageDrawable(iconX);
            ySwapper.setImageDrawable(iconYpressed);
            zSwapper.setImageDrawable(iconZ);
            titleText.setText(getResources().getString(R.string.squared_symbolY));
            XaxisText.setVisibility(View.INVISIBLE);
            xDirectionIcon.setVisibility(View.INVISIBLE);
            xGraphCard.setVisibility(View.INVISIBLE);

            YaxisText.setVisibility(View.VISIBLE);
            yDirectionIcon.setVisibility(View.VISIBLE);
            yGraphCard.setVisibility(View.VISIBLE);

            ZaxisText.setVisibility(View.INVISIBLE);
            zDirectionIcon.setVisibility(View.INVISIBLE);
            zGraphCard.setVisibility(View.INVISIBLE);
        }
        else if(view == zSwapper)
        {
            xSwapper.setImageDrawable(iconX);
            ySwapper.setImageDrawable(iconY);
            zSwapper.setImageDrawable(iconZpressed);

            titleText.setText(getResources().getString(R.string.squared_symbolZ));
            XaxisText.setVisibility(View.INVISIBLE);
            xDirectionIcon.setVisibility(View.INVISIBLE);
            xGraphCard.setVisibility(View.INVISIBLE);

            YaxisText.setVisibility(View.INVISIBLE);
            yDirectionIcon.setVisibility(View.INVISIBLE);
            yGraphCard.setVisibility(View.INVISIBLE);

            ZaxisText.setVisibility(View.VISIBLE);
            zDirectionIcon.setVisibility(View.VISIBLE);
            zGraphCard.setVisibility(View.VISIBLE);
        }
    }
}

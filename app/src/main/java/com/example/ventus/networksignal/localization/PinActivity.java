package com.example.ventus.networksignal.localization;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.ventus.networksignal.R;
import com.zechassault.zonemap.listener.ItemClickListener;
import com.zechassault.zonemap.util.BitmapUtils;
import com.zechassault.zonemap.view.ImageMapView;

import java.util.ArrayList;
import java.util.List;

public class PinActivity extends Activity implements ItemClickListener<PinItem> {

    /* The index of the item we want the user to tap */
    int askedItemIndex = 0;

    /* Data to populate the adapter */
    public static final int NB_ELEMENTS = 11;

    String[] names = new String[]{
            "Ankle",
            "Apple",
            "Arm",
            "Bread",
            "Butterfly",
            "Chicken",
            "Funny",
            "Heart",
            "Horse",
            "Rib",
            "Head"
    };
    int[] resID = new int[]{
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small,
            R.drawable.pin_gray_small
    };
    int[] resIDEmpty = new int[]{
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty,
            R.drawable.apple_empty
    };
    private PointF[] pointFs = new PointF[]{
            new PointF(0.55413014f, 0.8852321f),
            new PointF(0.47312737f, 0.2281924f),
            new PointF(0.7258657f, 0.46690002f),
            new PointF(0.4931545f, 0.573822f),
            new PointF(0.51643884f, 0.45194212f),
            new PointF(0.4382141f, 0.29778966f),
            new PointF(0.29861376f, 0.36735255f),
            new PointF(0.5429275f, 0.30569845f),
            new PointF(0.3812865f, 0.6557617f),
            new PointF(0.4198726f, 0.3800899f),
            new PointF(0.55254054f, 0.0431957f)
    };
    /* Item Map view*/
    private ImageMapView imageMapView;

    /*List of item to show on map view*/
    private List<PinItem> items;

    private TextView textViewSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);

        // constructing adapter item list to display on image map view
        items = new ArrayList<>();
        for (int i = 0; i < NB_ELEMENTS; i++) {
            items.add(new PinItem(names[i], pointFs[i]
                    , BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), resID[i]))
                    , BitmapUtils.getEmptyBitmap()));
        }
        // retrieving view from xml
        imageMapView = (ImageMapView) findViewById(R.id.imageMapViewFront);
        // main image is set in xml android:src="@drawable/background"
        // here we tell the view to no scale image with background
        imageMapView.setScaleToBackground(false);
        // here we tell the view not to considerate transparent pixel as part of the item
        imageMapView.setAllowTransparent(false);

        // setting image map adapter
        imageMapView.setAdapter(new PinAdapter(items));

        // setting adapter on item tap listener
        imageMapView.getAdapter().setItemClickListener(this);

        //imageMapView.setOnTouchListener(handleTouch);

        //soundPoolPlayer = new SoundPoolPlayer(this);

        textViewSelection = (TextView) findViewById(R.id.textViewSelection);
        updateText();
    }

    /*
    setup next item to pick
     */
    private void updateText() {
        textViewSelection.setText(String.format(getResources().getText(R.string.pick_the_bone).toString(), items.get(askedItemIndex).getText()));
    }

    /* Define what happen when an item is clicked */

    @Override
    public void onMapItemClick(PinItem item) {
        // on this activity we check if the clicked item correspond to the asked one.

        if (askedItemIndex < NB_ELEMENTS && items.get(askedItemIndex).equals(item)) {
            //add item to the list of picked Pin
            ((PinAdapter) imageMapView.getAdapter()).pickItem(item);
            //set next Pin to retrieve
            askedItemIndex++;
            if (askedItemIndex < NB_ELEMENTS) {
                updateText();

                //soundPoolPlayer.playShortResource(R.raw.win);//don't mind this, for win sound ...

            }
        } else {
            //soundPoolPlayer.playShortResource(R.raw.lose);//don't mind this, for win sound ...
        }
    }

    //don't mind this, for win sound ...
    //private SoundPoolPlayer soundPoolPlayer;
    //ponizej jest moj test z kad gosc wzial te PointF
    /*private View.OnTouchListener handleTouch = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event) {

            float winX = imageMapView.getWidth();
            float winY = imageMapView.getHeight();
            float x = (float)event.getX();
            float y = (float)event.getY();
            float fX = x/winX;
            float fY = y/winY;
            Log.i("aaa", Float.toString(fX));
            Log.i("bbb", Float.toString(fY));
            Log.i("ccc", Float.toString(winX));
            Log.i("ccc", Float.toString(winY));

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
            }

            return false;
        }
    };*/
}

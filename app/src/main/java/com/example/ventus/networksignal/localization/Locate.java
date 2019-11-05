package com.example.ventus.networksignal.localization;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ventus.networksignal.R;
import com.zechassault.zonemap.listener.ItemClickListener;
import com.zechassault.zonemap.util.BitmapUtils;
import com.zechassault.zonemap.view.ImageMapView;

import java.util.ArrayList;
import java.util.List;

public class Locate extends Activity implements ItemClickListener<PinItem> {

	int pinID = R.drawable.pin_gray_small;
	int pinIDGold = R.drawable.pin_gold_small;
	String[] names = new String[]{
			"C102",
			"C106",
			"C105",
			"Schody 1",
			"C100*",
			"Schody 2",
			"Ubikacja niepełnosprawnych",
			"Ubikacja damska",
			"Winda",
			"Ubikacja męska",
			"C109",
			"C112"
	};
	private PointF[] pointFs = new PointF[]{
			new PointF(0.33f, 0.40f),  //C102
			new PointF(0.53f, 0.40f),  //C106
			new PointF(0.47f, 0.60f),  //C105
			new PointF(0.55f, 0.60f),  //Schody 1
			new PointF(0.16f, 0.55f),  //C100
			new PointF(0.10f, 0.45f),  //Schody 2
			new PointF(0.60f, 0.35f),  //Ubikacja niepełnosprawnych
			new PointF(0.65f, 0.35f),  //Ubikacja damska
			new PointF(0.695f, 0.35f), //Winda
			new PointF(0.74f, 0.35f),  //Ubikacja męska
			new PointF(0.65f, 0.60f),  //C109
			new PointF(0.80f, 0.60f)   //C112
	};

	/* Item Map view*/
	private ImageMapView imageMapView;

	/*List of item to show on map view*/
	private List<PinItem> items;
	public static final int NB_ELEMENTS = 12;

	ArrayList<String> buildings;
	DatabaseHelper db;
	ArrayAdapter<String> arrayAdapter;
	ArrayList<PositionData> positionsData;
	String building;
	TextView result;
	TextView text;
	Button locate;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.locate);
		db = new DatabaseHelper(this);
		buildings = db.getBuildings();
		locate = (Button) findViewById(R.id.locate);

		result = (TextView) findViewById(R.id.result);
		text = (TextView) findViewById(R.id.text);
        arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, buildings);

		locate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				items = new ArrayList<>();
				for (int i = 0; i < NB_ELEMENTS; i++) {
					items.add(new PinItem(names[i], pointFs[i]
							, BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinID))
							, BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinIDGold))));
				}
				Intent intent = new Intent(getApplicationContext(), Scan.class);
				intent.putExtra("isLearning", false);
				startActivityForResult(intent,0);
				
			}
		});
	
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, buildings);
		// Set The Adapter
        if (buildings.size()==0) {
            Toast.makeText(this, "No building data available.", Toast.LENGTH_LONG).show();
            locate.setEnabled(false);
        }
        else{


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
		builder.setTitle("Choose building");
		builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				building = buildings.get(which);

							
				
			}
		});
		builder.show();
        }

		items = new ArrayList<>();
		for (int i = 0; i < NB_ELEMENTS; i++) {
			items.add(new PinItem(names[i], pointFs[i]
					, BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinID))
					, BitmapUtils.resAsBitmap(ContextCompat.getDrawable(getApplicationContext(), pinIDGold))));
		}
		imageMapView = (ImageMapView) findViewById(R.id.imageMapViewFront);
		imageMapView.setScaleToBackground(false);
		imageMapView.setAllowTransparent(false);
		imageMapView.setAdapter(new PinAdapter(items));
		imageMapView.getAdapter().setItemClickListener(this);

	}



    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
        if(resultCode==RESULT_OK){


		PositionData positionData = (PositionData) intent
				.getSerializableExtra("PositionData");
		positionsData=db.getReadings(building);

		String closestPosition = null;
		ArrayList<Router> wifis = db.getFriendlyWifis(building);

		int min_distance = positionData.uDistance(positionsData.get(0), wifis);
        int j=0;
		closestPosition = positionsData.get(0).getName();
		String res = "";
		res += closestPosition + "\n" + min_distance;
		res += "\n" + positionsData.get(0).toString();
		for (int i = 1; i < positionsData.size(); i++) {
			int distance = positionData.uDistance(positionsData.get(i), wifis);
			res += "\n" + positionsData.get(i).getName() + "\n" + distance;
			res += "\n" + positionsData.get(i).toString();
			if (distance < min_distance) {
				min_distance = distance;
                j=i;
				closestPosition = positionsData.get(i).getName();

			}

		}

           if (min_distance == PositionData.MAX_DISTANCE){
                closestPosition="";
			   	result.setText(closestPosition); //Out fo range
                text.setText("Out of range");
                Toast.makeText(this,"You are out of range of the selected building",Toast.LENGTH_LONG).show();

            }
            else {
			   text.setText("");
               result.setText(closestPosition); //Nearest point
               ((PinAdapter) imageMapView.getAdapter()).pickItem(((PinAdapter) imageMapView.getAdapter()).getItemWithName(closestPosition));
           }





            res += "\nCurrent:\n" + positionData.toString();
		Log.v("Result",res);



		
		super.onActivityResult(requestCode, resultCode, intent);
        }
	}


	public class CustomOnItemSelectedListener implements
			AdapterView.OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			building = parent.getItemAtPosition(pos).toString();
			locate.setEnabled(true);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			locate.setEnabled(false);
		}

	}
	@Override
	public void onMapItemClick(PinItem item) {
		//do nothing
	}


}

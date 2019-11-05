package com.example.ventus.networksignal.localization;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.zechassault.zonemap.adapter.MapAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Very simple implementation of MapAdapter
 */
public class PinAdapter extends MapAdapter<PinItem> {
    // the list of ous item to display
    private final List<PinItem> items;

    // list of picked item
    private Set<PinItem> pickedItem = new HashSet<>();


    public PinAdapter(List<PinItem> items) {
        this.items = items;
    }

    /*
     Tell adapter how to get item coordinate
     */
    @Override
    public PointF getItemCoordinates(PinItem item) {
        return new PointF(item.x, item.y);
    }

    /*
    Tell adapter how to retrieve an item based on its position
    */
    @Override
    public PinItem getItemAtPosition(int position) {
        return items.get(position);
    }

    /*
    Tell adapter how many item in total we have
    */
    @Override
    public int getCount() {
        return items.size();
    }


    public PinItem getItemWithName(String name) {
        int i=0;
        for (;i<items.size();i++){
            if (items.get(i).text.equals(name)){
                break;
            }
        }
        return items.get(i);
    }


    @Override
    public Bitmap getItemBitmap(PinItem item) {
        if (pickedItem.contains(item)) {
            // if the item is already picked it will item appear as hole without Pin
            return item.bitmapEmpty;
        }
        // The bitmap of the item with the Pin
        return item.bitmap;
    }

    /**
     * Pick an item
     * @param item PinItem to pick
     */
    public void pickItem(PinItem item) {
        //update the list of picked items
        pickedItem.add(item);
        //Refresh the view
        notifyDataSetHasChanged();
    }
}

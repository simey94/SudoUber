package com.example.cs3301.cs3301_practical;

import android.content.Context;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;


public class PopUpMenuEventHandler implements PopupMenu.OnMenuItemClickListener {

    Context context;

    public PopUpMenuEventHandler(Context context){
        this.context = context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.id_item1){
            Toast.makeText(context, "Item 1 selected", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (item.getItemId() == R.id.id_item2){
            Toast.makeText(context, "Item 2 selected", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
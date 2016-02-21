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
        if (item.getItemId() == R.id.id_name) {
            Toast.makeText(context, "Name Selected", Toast.LENGTH_LONG).show();
            return true;
        } else if (item.getItemId() == R.id.id_username) {
            Toast.makeText(context, "Username selected", Toast.LENGTH_LONG).show();
            return true;
        } else if (item.getItemId() == R.id.id_age) {
            Toast.makeText(context, "Age selected", Toast.LENGTH_LONG).show();
            return true;
        } else if (item.getItemId() == R.id.id_logout) {
            //clientLocalStore.clearClientData();
            //clientLocalStore.setClientLoggedIn(false);
            //context.startActivity(new Intent(this.context, LoginActivity.class));
            return true;
        }

        return false;
    }
}
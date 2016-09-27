package com.js.pocket.pws;

//import android.annotation.TargetApi;
//import android.app.ActionBar;
//import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
//import android.view.MenuItem;

//@TargetApi(11)
public class PrefsActivity extends PreferenceActivity{
@Override
protected void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   addPreferencesFromResource(R.xml.prefs);
   //ActionBar actionBar = getActionBar();
   //actionBar.setDisplayHomeAsUpEnabled(true);
}
/*FOR FUTURE UPDATE*/
 /**   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    **/
}
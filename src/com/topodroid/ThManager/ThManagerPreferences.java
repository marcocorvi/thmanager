/* @file ThManagerPreferences.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief ThManager options dialog
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 * CHANGES
 */
package com.topodroid.ThManager;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
// import android.preference.EditTextPreference;
// import android.preference.ListPreference;
// import android.view.Menu;
// import android.view.MenuItem;

import android.util.Log;

/**
 */
public class ThManagerPreferences extends PreferenceActivity 
{

  Preference mCwdPreference;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate( savedInstanceState );

    addPreferencesFromResource(R.xml.preferences);

    final Intent cwd_intent = new Intent( this, CWDActivity.class );
    mCwdPreference = (Preference) findPreference( ThManagerApp.THMANAGER_CWD );
    mCwdPreference.setOnPreferenceClickListener( 
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick( Preference pref ) 
          {
            startActivityForResult( cwd_intent, ThManagerApp.REQUEST_CWD );
            return true;
          }
        } );

    setTitle( R.string.title_settings );
  }

  public void onActivityResult( int request, int result, Intent intent ) 
  {
    Bundle extras = (intent != null)? intent.getExtras() : null;
    switch ( request ) {
      case ThManagerApp.REQUEST_CWD:
        if ( extras != null ) {
          String cwd = extras.getString( ThManagerApp.THMANAGER_CWD );
          mCwdPreference.setSummary( cwd );
        }
        break;
    }
  }

}

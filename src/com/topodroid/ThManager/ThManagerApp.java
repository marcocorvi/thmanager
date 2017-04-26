/** @file ThManagerApp.java
 *
 */
package com.topodroid.ThManager;

import java.io.File;

import java.util.ArrayList;

import android.app.Application;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.view.ViewGroup.LayoutParams;

import android.util.Log;

public class ThManagerApp extends Application
                          implements OnSharedPreferenceChangeListener
{
  static final String TAG = "ThManager";

  static final String THMANAGER_SURVEY = "ThManagerSurvey";
  static final String THMANAGER_PATH = "ThManagerPath";
  static final String THCONFIG_PATH = "ThManagerConfig";
  static String VERSION = "";

  final static int REQUEST_THCONFIG = 0;
  final static int REQUEST_CWD = 1;

  final static int RESULT_THCONFIG_OK     = 0;
  final static int RESULT_THCONFIG_DELETE = 1;
  final static int RESULT_THCONFIG_NONE   = 2;

  final static String THMANAGER_CWD = "THMANAGER_CWD";

  // static final int MODE_SOURCE = 0;
  // static final int NODE_SURVEY = 1;

  static String mCWD;
  SharedPreferences mPrefs;
  ArrayList< ThSurvey > mViewSurveys = null;
  ThConfig mConfig = null;                    // current config file
  ThManagerActivity mActivity;

  static double mDist = 40;
  static int mTextSize = 24;

  @Override
  public void onCreate()
  {
    super.onCreate();
    try {
      VERSION = getPackageManager().getPackageInfo( getPackageName(), 0 ).versionName;
    } catch ( NameNotFoundException e ) {
      e.printStackTrace(); // FIXME
    }

    mPrefs = PreferenceManager.getDefaultSharedPreferences( this );
    // this.prefs.registerOnSharedPreferenceChangeListener( this );

    mCWD = mPrefs.getString( THMANAGER_CWD, "TopoDroid" );
    try {
      mDist = Double.parseDouble( mPrefs.getString( "THMANAGER_DIST", "40" ) );
    } catch ( NumberFormatException e ) { }
    try {
      mTextSize = Integer.parseInt( mPrefs.getString( "THMANAGER_TEXTSIZE", "24" ) );
    } catch ( NumberFormatException e ) { }

    ThManagerPath.setPaths( mCWD );
    ThManagerPath.setFilters(); 
    mViewSurveys = null;
  }

  // static boolean deleteThConfigFile( String filepath )
  // {
  //   // Log.v("ThManager", "Th App delete " + filepath );
  //   boolean ret = (new File( filepath )).delete();
  //   if ( ! ret ) {
  //     Log.v("ThManager", "Th App delete FAILED");
  //   }
  //   return ret;
  // }

  void setCWDPreference( String cwd )
  {
    if ( mCWD.equals( cwd ) ) return;
    // Log.v("DistoX", "setCWDPreference " + cwd );
    if ( mPrefs != null ) {
      Editor editor = mPrefs.edit();
      editor.putString( THMANAGER_CWD, cwd ); 
      editor.commit();
    }
    mCWD = cwd;
    ThManagerPath.setPaths( cwd ); 
    if ( mActivity != null ) mActivity.updateThConfigList();
  }

  public void onSharedPreferenceChanged( SharedPreferences sp, String k ) 
  {
    if ( k.equals( "THMANAGER_DIST" ) ) {
      try {
        mDist = Double.parseDouble( mPrefs.getString( "THMANAGER_DIST", "40" ) );
      } catch ( NumberFormatException e ) { }
    } else if ( k.equals( "THMANAGER_TEXTSIZE" ) ) {
      try {
        mTextSize = Integer.parseInt( mPrefs.getString( "THMANAGER_TEXTSIZE", "24" ) );
      } catch ( NumberFormatException e ) { }
    }
  }


  static float getDisplayDensity( Context context )
  {
    return context.getResources().getSystem().getDisplayMetrics().density;
  }

  int setListViewHeight( HorizontalListView listView )
  {
    return ThManagerApp.setListViewHeight( this, listView );
  }

  static int setListViewHeight( Context context, HorizontalListView listView )
  {
    int size = getScaledSize( context );
    if ( listView != null ) {
      LayoutParams params = listView.getLayoutParams();
      params.height = size + 10;
      listView.setLayoutParams( params );
    }
    return size;
  }

  // default button size
  static int getScaledSize( Context context )
  {
    return (int)( 42 * context.getResources().getSystem().getDisplayMetrics().density );
  }

  static int getDefaultSize( Context context )
  {
    return (int)( 42 * context.getResources().getSystem().getDisplayMetrics().density );
  }

}

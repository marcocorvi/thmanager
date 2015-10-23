/** @file ThViewEquateDialog.java
 *
 */
package com.topodroid.ThManager;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import android.app.Dialog;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;

import android.widget.ListView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.widget.Toast;

class ThViewEquateDialog extends Dialog
                         implements OnClickListener
{
  Context mContext;
  ThManagerApp mApp;
  ThViewActivity mParent;
  ArrayList< ThViewCommand > mCommands;
  ArrayList< ThViewStationAdapter > mAdapters;
  LinearLayout mLayout;
  Button mBTok;

  ThViewEquateDialog( Context context, ThViewActivity parent, ThManagerApp app )
  {
    super( context );
    mContext = context;
    mParent = parent;
    mApp = app;
    // Log.v("ThManager", "ThViewEquateDialog equates " + mEquates.size() );
    mAdapters = new ArrayList< ThViewStationAdapter >();
  }

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    mCommands = mParent.getCommands();
    if ( mCommands.size() < 2 ) {
      dismiss();
      return;
    } 

    setContentView(R.layout.thviewequate_dialog);
    getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
    setTitle("THERION EQUATES");

    mLayout = (LinearLayout) findViewById( R.id.layout );

    mBTok = (Button) findViewById( R.id.ok );
    mBTok.setOnClickListener( this );

    populateLayout();
  }

  void populateLayout()
  {
    for ( ThViewCommand command : mCommands ) {
      FrameLayout fl = new FrameLayout( mContext );
      // RelativeLayout rl = new RelativeLayout( mContext );
      LinearLayout rl = new LinearLayout( mContext );
      rl.setOrientation( LinearLayout.VERTICAL );
      ListView lv = new ListView( mContext );
      TextView tv = new TextView( mContext );

      tv.setText( command.name() );
      ThViewStationAdapter adapter = 
          new ThViewStationAdapter( mContext, R.layout.thviewstation_adapter, command.mStationsArray, tv, command );
      lv.setAdapter( adapter );
      mAdapters.add( adapter );
      
      rl.addView( tv, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ) );
      rl.addView( lv, new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
      fl.addView( rl, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
      mLayout.addView( fl, new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );

    }
  }

  @Override
  public void onClick( View v )
  {
    
    Button b = (Button)v;
    if ( b == mBTok ) { // SAVE equate
      ThEquate equate = new ThEquate();
      for ( ThViewStationAdapter adapter : mAdapters ) {
        ThStation ts = adapter.getCheckedStation();
        String st = adapter.getStationName();
        int len = st.length();
        while ( len > 0 && st.charAt( len - 1 ) == '.' ) -- len;
        if ( len < st.length() ) st = st.substring(0,len);
        equate.addStation( st );
      }
      // Log.v("ThManager", "EQUATE " + equate.stationsString() );
      mApp.mConfig.addEquate( equate );
    }
    dismiss();
  }

}

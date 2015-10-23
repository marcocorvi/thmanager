/** @file ThSourcesDialog.java
 *
 */
package com.topodroid.ThManager;

import java.util.ArrayList;
import java.io.File;

import android.app.Dialog;
import android.widget.ListView;

import android.view.ViewGroup.LayoutParams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.widget.Toast;

class ThSourcesDialog extends Dialog
{
  Context mContext;
  ThSourceAdapter mThSourceAdapter;
  ThConfigActivity mParent;
  ListView mList;
  ArrayList< ThSource > mSources;

  ThSourcesDialog( Context context, ThConfigActivity parent )
  {
    super( context );
    mContext = context;
    mParent = parent;
    mSources = new ArrayList< ThSource >();
  }

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView(R.layout.thsources_dialog);
    getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

    mList = (ListView) findViewById(R.id.list);
    mList.setDividerHeight( 2 );

    setTitle("THERION SURVEY FILES");

    updateList();
  }

  void updateList()
  {
    mThSourceAdapter = new ThSourceAdapter( mContext, R.layout.thsource_adapter, mSources );
    File[] files = ThManagerPath.scanThDir();
    // Log.v("ThManager", "source nr " + files.length );
    for ( File file : files ) {
      String name = file.getName();
      if ( ! mParent.hasSource( name ) ) {
        String path = ThManagerPath.getThPath( name );
        // Log.v("ThManager", "source name " + name + " path " + path );
        mThSourceAdapter.addThSource( new ThSource( name, path ) );
      }
    }
    if ( mThSourceAdapter.size() > 0 ) {
      mList.setAdapter( mThSourceAdapter );
      // mList.invalidate();
    } else {
      hide();
      Toast.makeText( mContext, R.string.no_th_file, Toast.LENGTH_LONG ).show();
      dismiss();
    }
  }

  @Override
  public void onBackPressed()
  {
    hide();
    ArrayList<String> sources = mThSourceAdapter.getCheckedSources();
    // Log.v("ThManager", "checked sources " + sources.size() );
    mParent.addSources( sources );
    dismiss();
  }

}

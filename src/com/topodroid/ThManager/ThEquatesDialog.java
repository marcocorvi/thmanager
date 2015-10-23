/** @file ThEquatesDialog.java
 *
 */
package com.topodroid.ThManager;

import java.util.ArrayList;
import java.io.File;

import android.app.Dialog;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.widget.Toast;

class ThEquatesDialog extends Dialog
                      implements OnItemClickListener
{
  Context mContext;
  ThEquateAdapter mThEquateAdapter;
  ThConfig mConfig;
  ListView mList;
  ArrayList< ThEquate > mEquates;

  ThEquatesDialog( Context context, ThConfig config )
  {
    super( context );
    mContext = context;
    mConfig  = config;
    if ( mConfig != null && mConfig.mEquates != null ) {
      mEquates = mConfig.mEquates;
    } else {
      mEquates = new ArrayList< ThEquate >();
    }
    Log.v("ThManager", "ThEquatesDialog equates " + mEquates.size() );
  }

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView(R.layout.thequates_dialog);
    getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

    mList = (ListView) findViewById(R.id.list);
    mList.setDividerHeight( 2 );
    mList.setOnItemClickListener( this );

    setTitle("THERION EQUATES");

    updateList();
  }

  void updateList()
  {
    if ( mEquates != null && mEquates.size() > 0 ) {
      mThEquateAdapter = new ThEquateAdapter( mContext, R.layout.thequate_adapter, mEquates );
      mList.setAdapter( mThEquateAdapter );
      // mList.invalidate();
    } else {
      hide();
      Toast.makeText( mContext, R.string.no_equate, Toast.LENGTH_LONG ).show();
      dismiss();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
  {
    ThEquateViewHolder vh = (ThEquateViewHolder) view.getTag();
    if ( vh != null ) {
      mEquates.remove( vh.equate );
      updateList();
    }
  }
      
}

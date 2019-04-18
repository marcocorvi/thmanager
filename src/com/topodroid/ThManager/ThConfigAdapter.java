/** @file ThConfigAdapter.java
 */
package com.topodroid.ThManager;

import java.util.ArrayList;
import java.io.File;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.util.Log;

class ThConfigAdapter extends ArrayAdapter< ThConfig >
{
  private ArrayList< ThConfig > mItems;
  private Context mContext;
  private OnClickListener mOnClick;

  public ThConfigAdapter( Context ctx, int id, ArrayList< ThConfig > items, OnClickListener onClick )
  {
    super( ctx, id, items );
    mContext = ctx;
    mItems = items;
    mOnClick = onClick;
    // Log.v( ThManagerApp.TAG, "ThConfigAdapter nr. items " + items.size() );
  }

  public ThConfig get( int pos ) { return mItems.get(pos); }

  public ThConfig get( String survey ) 
  {
    // Log.v("ThManager", "ThConfig get survey >" + survey + "< size " + mItems.size() );
    if ( survey == null || survey.length() == 0 ) return null;
    for ( ThConfig thconfig : mItems ) {
      // Log.v("ThManager", "ThConfig item >" + thconfig.mName + "<" );
      if ( thconfig.mName.equals( survey ) ) return thconfig;
    }
    return null;
  }

  boolean deleteThConfig( String filepath )
  {
    File file = new File( filepath );
    boolean ret = file.delete();
    for ( ThConfig thconfig : mItems ) {
      if ( thconfig.mFilepath.equals( filepath ) ) {
        mItems.remove( thconfig );
        break;
      }
    }
    return ret;
  }

  @Override
  public View getView( int pos, View convertView, ViewGroup parent )
  {
    View v = convertView;
    if ( v == null ) {
      LayoutInflater li = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
      v = li.inflate( R.layout.row, null );
    }

    ThConfig b = mItems.get( pos );
    if ( b != null ) {
      TextView tw = (TextView) v.findViewById( R.id.row_text );
      tw.setText( b.toString() );
      // tw.setTextColor( b.color() );
    }
    v.setOnClickListener( mOnClick );
    return v;
  }

  public int size()
  {
    return mItems.size();
  }

}

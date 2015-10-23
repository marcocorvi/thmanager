/** @file ThConfigAdapter.java
 */
package com.topodroid.ThManager;

import java.util.ArrayList;
import java.io.File;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

// import android.util.Log;

class ThConfigAdapter extends ArrayAdapter< ThConfig >
{
  private ArrayList< ThConfig > mItems;
  private Context mContext;

  public ThConfigAdapter( Context ctx, int id, ArrayList< ThConfig > items )
  {
    super( ctx, id, items );
    mContext = ctx;
    mItems = items;
    // Log.v( ThManagerApp.TAG, "ThConfigAdapter nr. items " + items.size() );
  }

  public ThConfig get( int pos ) { return mItems.get(pos); }

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
    return v;
  }

  public int size()
  {
    return mItems.size();
  }

}

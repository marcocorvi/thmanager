/** @file ThSourceAdapter.java
 */
package com.topodroid.ThManager;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.util.Log;

class ThSourceAdapter extends ArrayAdapter< ThSource >
{
  private ArrayList< ThSource > mItems;
  private Context mContext;
  private LayoutInflater mLayoutInflater;

  public ThSourceAdapter( Context ctx, int id, ArrayList< ThSource > items )
  {
    super( ctx, id, items );
    mContext = ctx;
    mLayoutInflater = (LayoutInflater)ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    if ( items != null ) {
      mItems = items;
    } else {
      mItems = new ArrayList< ThSource >();
    }
  }

  public ThSource get( int pos ) { return mItems.get(pos); }

  public ThSource get( String name ) 
  {
    for ( ThSource source : mItems ) {
      if ( source.mName.equals( name ) ) return source;
    }
    return null;
  }

  void addThSource( ThSource source ) { mItems.add( source ); }

  public int size() { return mItems.size(); }

  private class ViewHolder
  { 
    CheckBox checkBox;
    TextView textView;
  }

  /**
   * @return list of filename of checked sources
   */
  ArrayList< String > getCheckedSources()
  {
    ArrayList< String > ret = new ArrayList< String >();
    for ( ThSource source : mItems ) {
      if ( source.isChecked() ) {
        ret.add( source.mFilename );
      }
    }
    return ret;
  }


  @Override
  public View getView( int pos, View convertView, ViewGroup parent )
  {
    ThSource b = mItems.get( pos );
    if ( b == null ) return convertView;

    ViewHolder holder = null; 
    if ( convertView == null ) {
      convertView = mLayoutInflater.inflate( R.layout.thsource_adapter, null );
      holder = new ViewHolder();
      holder.checkBox = (CheckBox) convertView.findViewById( R.id.thsource_checked );
      holder.textView = (TextView) convertView.findViewById( R.id.thsource_name );
      holder.checkBox.setOnClickListener( b );
      convertView.setTag( holder );
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.checkBox.setChecked( b.isChecked() );
    holder.textView.setText( b.toString() );
    return convertView;
  }

}


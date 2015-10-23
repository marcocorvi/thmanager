/** @file ThInputAdapter.java
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

// import android.util.Log;

class ThInputAdapter extends ArrayAdapter< ThInput >
{
  private ArrayList< ThInput > mItems;
  private Context mContext;
  private LayoutInflater mLayoutInflater;

  public ThInputAdapter( Context ctx, int id, ArrayList< ThInput > items )
  {
    super( ctx, id, items );
    mContext = ctx;
    mLayoutInflater = (LayoutInflater)ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    if ( items != null ) {
      mItems = items;
    } else {
      mItems = new ArrayList< ThInput >();
    }
  }

  public ThInput get( int pos ) { return mItems.get(pos); }

  public ThInput get( String name ) 
  {
    for ( ThInput input : mItems ) {
      if ( input.mName.equals( name ) ) return input;
    }
    return null;
  }

  public void add( ThInput input ) { mItems.add( input ); }

  public void drop( ThInput input ) { mItems.remove( input ); }

  public void dropChecked( ) 
  {
    final Iterator it = mItems.iterator();
    while ( it.hasNext() ) {
      ThInput input = (ThInput) it.next();
      if ( input.isChecked() ) {
        mItems.remove( input );
      }
    }
  }

  public int size() { return mItems.size(); }


  private class ViewHolder
  { 
    CheckBox checkBox;
    TextView textView;
  }

  @Override
  public View getView( int pos, View convertView, ViewGroup parent )
  {
    ThInput b = mItems.get( pos );
    if ( b == null ) return convertView;

    ViewHolder holder = null; 
    if ( convertView == null ) {
      convertView = mLayoutInflater.inflate( R.layout.thinput_adapter, null );
      holder = new ViewHolder();
      holder.checkBox = (CheckBox) convertView.findViewById( R.id.thinput_checked );
      holder.textView = (TextView) convertView.findViewById( R.id.thinput_name );
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


/** @file ThEquateAdapter.java
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.util.Log;

class ThEquateAdapter extends ArrayAdapter< ThEquate >
{
  private ArrayList< ThEquate > mItems;
  private Context mContext;
  private LayoutInflater mLayoutInflater;

  public ThEquateAdapter( Context ctx, int id, ArrayList< ThEquate > items )
  {
    super( ctx, id, items );
    mContext = ctx;
    mLayoutInflater = (LayoutInflater)ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    if ( items != null ) {
      mItems = items;
    } else {
      mItems = new ArrayList< ThEquate >();
    }
  }

  public ThEquate get( int pos ) { return mItems.get(pos); }

  // void addThEquate( ThEquate equate ) { mItems.add( equate ); }

  public int size() { return mItems.size(); }


  @Override
  public View getView( int pos, View view, ViewGroup parent )
  {
    ThEquate b = mItems.get( pos );
    if ( b == null ) return view;

    ThEquateViewHolder holder = null; 
    if ( view == null ) {
      view = mLayoutInflater.inflate( R.layout.thequate_adapter, null );
      holder = new ThEquateViewHolder();
      holder.textView = (TextView) view.findViewById( R.id.thequate );
      view.setTag( holder );
    } else {
      holder = (ThEquateViewHolder) view.getTag();
    }
    holder.equate = b;
    holder.textView.setText( b.stationsString() );
    return view;
  }

  

}


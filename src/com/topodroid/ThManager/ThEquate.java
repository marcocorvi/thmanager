/** @File ThEquate.java
 *
 */
package com.topodroid.ThManager;

import java.util.ArrayList;

class ThEquate
{
  ArrayList< String > mStations; // full station names

  ThEquate()
  {
    mStations = new ArrayList< String >();
  }

  boolean contains( String name )
  {
    for ( String st : mStations ) {
      if ( st.equals( name ) ) return true;
    }
    return false;
  }

  void addStation( String station ) { mStations.add( station ); }

  int size() { return mStations.size(); }

  String stationsString()
  {
    StringBuilder sb = new StringBuilder();
    for ( String name : mStations ) {
      sb.append( name + " " );
    }
    sb.deleteCharAt( sb.length() - 1 );
    return sb.toString();
  }
}

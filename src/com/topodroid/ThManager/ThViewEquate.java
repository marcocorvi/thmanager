/** @file ThViewEquate.java
 */
package com.topodroid.ThManager;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Matrix;

import android.widget.CheckBox;

import android.util.Log;

class ThViewEquate
{
  ThEquate mEquate;
  ArrayList< ThViewStation > mStations;
   
  Path mPath;

  ThViewEquate( ThEquate equate )
  {
    mEquate = equate;
    mStations = new ArrayList< ThViewStation >();
    mPath = null;
  }

  void addViewStation( ThViewStation st )
  {
    mStations.add( st );
    makePath();
  }

  void shift( float dx, float dy, ThViewCommand command )
  {
    for ( ThViewStation st : mStations ) {
      if ( command == st.mCommand ) {
        // st.xoff += dx;
        // st.yoff += dy;
        makePath();
        break;
      }
    }
  }

  void makePath()
  {
    if ( mStations.size() > 1 ) {
      mPath = null;
      for ( ThViewStation vst : mStations ) {
        if ( mPath == null ) {
          mPath = new Path();
          mPath.moveTo( vst.fullX(), vst.fullY() );
        } else {
          mPath.lineTo( vst.fullX(), vst.fullY() );
        }
      }
    }
  }

  // void dump()
  // {
  //   Log.v("ThManager", "equate (size " + mStations.size() + ")" );
  //   for ( ThViewStation vst : mStations )
  //     Log.v("ThManager", "  station: " + vst.mStation.mName + " " + vst.mCommand.name() );
  // }

  void draw( Canvas canvas, Matrix matrix, Paint paint )
  {
    if ( mPath != null ) {
      Path path = new Path( mPath );
      path.transform( matrix );
      canvas.drawPath( path, paint );
    }
  }
  
}

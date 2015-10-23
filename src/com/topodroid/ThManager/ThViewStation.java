/** @file ThViewStation.java
 */
package com.topodroid.ThManager;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Matrix;

import android.widget.CheckBox;

class ThViewStation
{
  ThStation mStation;
  float x;  // canvas coords
  float y;
  Path mPath;
  double d;  // distance on selection
  boolean mChecked;
  CheckBox mCB;

  ThViewStation( ThStation st, float x0, float y0 )
  {
    mStation = st;
    x = x0;
    y = y0;
    d = 0;
    mChecked = false;
    mCB = null;
    mPath = new Path();
    mPath.moveTo( x, y );
    mPath.lineTo( x + 10 * st.mName.length(), y );
  }

  String name() { return mStation.mName; }

  void setChecked( boolean checked ) 
  { 
    mChecked = checked;
    if ( mCB != null ) {
      // mCB.setChecked( false );
      mCB.setChecked( checked );
      mCB.invalidate();
    }
  }

  boolean resetChecked()
  {
    boolean ret = mChecked;
    mCB = null;
    mChecked = false;
    return ret;
  }

  void setCheckBox( CheckBox cb ) 
  { 
    mCB = cb;
    if ( mCB != null ) mCB.setChecked( mChecked );
  }

  void shiftBy( float dx, float dy )
  {
    x += dx;
    y += dy;
  }

  void draw( Canvas canvas, Matrix matrix, Paint paint )
  {
    Path path = new Path( mPath );
    path.transform( matrix );
    canvas.drawTextOnPath( mStation.mName, path, 0,0, paint );
  }
  
}

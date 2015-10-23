/** @file ThViewPath.java
 */
package com.topodroid.ThManager;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Matrix;

class ThViewPath
{
  ThViewStation mSt1;
  ThViewStation mSt2;
  Path mPath;

  ThViewPath( ThViewStation st1, ThViewStation st2 )
  {
    mSt1 = st1;
    mSt2 = st2;
    mPath = new Path();
    mPath.moveTo( st1.x, st1.y );
    mPath.lineTo( st2.x, st2.y );
  }

  void draw( Canvas canvas, Matrix matrix, Paint paint )
  {
    Path path = new Path( mPath );
    path.transform( matrix );
    canvas.drawPath( path, paint );
  }
}

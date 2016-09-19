/* @file ThViewCommand.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief TopoDroid drawing: commands manager
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.ThManager;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Path;
// import android.graphics.Path.Direction;
import android.os.Handler;

import java.util.Iterator;
import java.util.List;
// import java.util.Locale;
import java.util.Collections;
import java.util.ArrayList;

import android.util.Log;

/**
 */
public class ThViewCommand 
{
  ThSurvey  mSurvey;
  ThViewStation mSelected;
  ThViewStation mEquateStation;
  List<ThViewPath>    mFixedStack;
  ArrayList<ThViewStation> mStationsArray;
  List<ThViewStation> mStations;
  Matrix mMatrix;
  Paint mPaint;
  Paint mFillPaint;
  float mXoff, mYoff;
  float mScale;

  ThViewStation getViewStation( String name )
  {
    for ( ThViewStation st : mStations ) {
      if ( st.mStation.mName.equals( name ) ) return st;
    }
    return null;
  }

  String name() { return mSurvey.mName; }

  void shift( float dx, float dy )
  {
    mXoff += dx;
    mYoff += dy;
    setTransform();
  }

  void rescale( float rs )
  { 
    mScale *= rs;
    setTransform();
  }

  void transform( float dx, float dy, float rs )
  {
    mXoff += dx;
    mYoff += dy;
    mScale *= rs;
    setTransform();
  }

  public ThViewCommand( ThSurvey survey, int color, float xoff, float yoff )
  {
    mSurvey = survey;
    mSelected = null;
    mFixedStack   = Collections.synchronizedList(new ArrayList< ThViewPath >());
    mStationsArray  = new ArrayList< ThViewStation >();
    mStations     = Collections.synchronizedList( mStationsArray );
    mMatrix = new Matrix(); // identity
    mPaint = makePaint( color, Paint.Style.STROKE );
    mFillPaint = makePaint( color & 0x99cccccc, Paint.Style.FILL );
    mXoff  = xoff;
    mYoff  = yoff;
    mScale = 1.0f;
    // FIXME
  }

  private void setTransform( )
  {
    mMatrix = new Matrix();
    mMatrix.postTranslate( mXoff, mYoff );
    mMatrix.postScale( mScale, mScale );
  }

  // oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo

  /** add a fixed path (called by DrawingSurface::addFixedPath)
   * @param path       path
   * @param selectable whether the path is selectable
   */
  public void addShot( ThShot sh )
  {
    ThViewStation st1 = getViewStation( sh.mFrom );
    ThViewStation st2 = getViewStation( sh.mTo );
    if ( st1 != null && st2 != null ) {
      mFixedStack.add( new ThViewPath( st1, st2 ) );
    }
  }  
  
  public void addStation( ThStation st, boolean equated )
  {
    mStations.add( new ThViewStation( st, this, st.e, st.s, equated ) );
  }

  public void executeAll( Canvas canvas, Handler preview_handler )
  {
    synchronized( mFixedStack ) {
      for ( ThViewPath path : mFixedStack ) path.draw( canvas, mMatrix, mPaint );
    }
    synchronized( mStations ) {
      float zoom = mScale / 50;
      for ( ThViewStation st : mStations ) {
        st.draw( canvas, mMatrix, mPaint, mFillPaint, zoom );
      }
      if ( mSelected != null ) {
        mSelected.drawCircle( canvas, mMatrix, mPaint, zoom );
      }
    }
  }

  // x,y canvas point
  public double getStationAt( float x, float y )
  {
    // Log.v("ThManager", "scale " + mScale );

    x = (x - mXoff); // /mScale;
    y = (y - mYoff); // /mScale;
    double d0 = 40.0 / mScale;
    mSelected = null;
    double dmin = 100000; // FIXME a very large number

    // Log.v("ThManager", name() + " get station at " + x + " " + y );
    synchronized ( mStations ) {
      for ( ThViewStation st : mStations ) {
        // Log.v("ThManager", name() + " station " + st.mStation.mName + " " + st.x + " " + st.y );
        double d = Math.abs( st.x - x ) + Math.abs( st.y - y );
        if ( d < d0 ) {
          if ( mSelected == null || d < dmin ) {
            mSelected = st;
            dmin = d;
          } 
        }
      }
    }
    if ( mSelected != null ) {
      mSelected.d = dmin * mScale;
      return mSelected.d;
    }
    return 2 * 40.0;
  }
    
  private Paint makePaint( int color, Style style )
  {
    Paint ret = new Paint();
    ret.setDither(true);
    ret.setColor( color );
    ret.setStyle( style );
    ret.setStrokeJoin(Paint.Join.ROUND);
    ret.setStrokeCap(Paint.Cap.ROUND);
    ret.setStrokeWidth( 2 );
    ret.setTextSize(24);
    return ret;
  }

}

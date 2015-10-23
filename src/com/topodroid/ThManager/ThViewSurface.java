/* @file ThViewSurface.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief ThManager drawing: drawing surface (canvas)
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.ThManager;

import android.content.Context;
import android.graphics.*; // Bitmap
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/**
 */
public class ThViewSurface extends SurfaceView
                           implements SurfaceHolder.Callback
{
    private Boolean _run;
    protected DrawThread thread;
    public boolean isDrawing = true;
    private SurfaceHolder mHolder; // canvas holder
    private Context mContext;
    private ThViewActivity mActivity;
    private AttributeSet mAttrs;
    int mWidth;            // canvas width
    int mHeight;           // canvas height
    private PointF mDisplayCenter;

    ArrayList< ThViewCommand > mCommandManager; // FIXME not private only to export DXF
    ThViewCommand mCommand = null;

    float mXoffset;
    float mYoffset;
    float mZoom;

    float canvasToSceneX( float x_canvas ) { return (x_canvas + mXoffset)/mZoom; }
    float canvasToSceneY( float y_canvas ) { return (y_canvas + mYoffset)/mZoom; }
    float sceneToCanvasX( float x_scene ) { return x_scene*mZoom - mXoffset; }
    float sceneToCanvasY( float y_scene ) { return y_scene*mZoom - mYoffset; }

    public int width()  { return mWidth; }
    public int height() { return mHeight; }

    void setActivity( ThViewActivity act ) { mActivity = act; }

    public ThViewSurface(Context context, AttributeSet attrs) 
    {
      super(context, attrs);
      mWidth = 0;
      mHeight = 0;

      mXoffset = 0;
      mYoffset = 0;
      mZoom = 1;

      thread = null;
      mContext = context;
      mAttrs   = attrs;
      mHolder = getHolder();
      mHolder.addCallback(this);
      mCommandManager = new ArrayList< ThViewCommand >();
    }

    void resetStation()
    {
      for ( ThViewCommand command : mCommandManager ) {
        command.mSelected = null;
      }
      mCommand = null;
    }

    void setDisplayCenter( float x, float y )
    {
      mDisplayCenter = new PointF( x, y );
    }

    void addSurvey( ThSurvey survey, int color, float xoff, float yoff )
    {
      ThViewCommand command = new ThViewCommand( survey, color, xoff, yoff );
      for ( ThStation st : survey.mStations ) {
        command.addStation( st );
      }
      for ( ThShot sh : survey.mShots ) {
        command.addShot( sh );
      }
      mCommandManager.add( command );
    }

    // dx   delta X
    // dy   delta Y
    // rs   rescale factor
    public void transform( float dx, float dy, float rs )
    {
      mXoffset += dx;
      mYoffset += dy;
      mZoom    *= rs;
      for ( ThViewCommand command : mCommandManager ) command.transform( dx, dy, rs );
    }

    void changeZoom( float f )
    {
      // float zoom0 = mZoom;
      // float zoom1 = zoom0 * f;
      // float dx = mXoffset - mDisplayCenter.x*(1/zoom1-1/zoom0);
      // float dy = mYoffset - mDisplayCenter.y*(1/zoom1-1/zoom0);
      // transform( dx, dy, f );
      // FIXME TODO translate towards (0,0) so that the offset does not change
      transform( 0, 0, f );
    }

    boolean getSurveyAt( float x, float y )
    {
      x = x / mZoom; // canvasToSceneX( x );
      y = y / mZoom; // canvasToSceneY( y );
      Log.v("ThManager", "at " + x + " " + y );
      mCommand = null;
      double dmin = 100000; // FIXME a large number
      for ( ThViewCommand command : mCommandManager ) {
        double d = command.getStationAt( x, y );
        if ( d < 40 && d < dmin ) {
          dmin = d;
          mCommand = command;
        }
      }
      return (mCommand != null);
    }

    ThViewStation selectedStation()
    {
      return ( mCommand == null )? null : mCommand.mSelected;
    }

    ThViewCommand selectedCommand() { return mCommand; }

    String selectedStationName()
    { 
      return ( mCommand == null )? null : mCommand.mSelected.name();
    }

    String selectedCommandName()
    { 
      return ( mCommand == null )? null : mCommand.name();
    }
       

    void shift( float dx, float dy ) 
    { 
      if ( mCommand != null ) {
        mCommand.shift( dx, dy );
      } else {
        transform( dx, dy, 1 );
      }
    }


    // ------------------------------------------------------------------------

    void refresh()
    {
      Canvas canvas = null;
      try {
        canvas = mHolder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        mWidth  = canvas.getWidth();
        mHeight = canvas.getHeight();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        for ( ThViewCommand command : mCommandManager ) command.executeAll( canvas, previewDoneHandler );
      } finally {
        if ( canvas != null ) {
          mHolder.unlockCanvasAndPost( canvas );
        }
      }
    }

    private Handler previewDoneHandler = new Handler()
    {
      @Override
      public void handleMessage(Message msg) {
        isDrawing = false;
      }
    };

    class DrawThread extends  Thread
    {
      private SurfaceHolder mSurfaceHolder;

      public DrawThread(SurfaceHolder surfaceHolder)
      {
        mSurfaceHolder = surfaceHolder;
      }

      public void setRunning(boolean run)
      {
        _run = run;
      }

      @Override
      public void run() 
      {
        while ( _run ) {
          if ( isDrawing == true ) {
            refresh();
          } else {
            try {
              // Log.v( TopoDroidApp.TAG, "drawing thread sleeps ..." );
              sleep(100);
            } catch ( InterruptedException e ) { }
          }
        }
      }
    }

    // ---------------------------------------------------------------------
    // SELECT - EDIT


    public void surfaceChanged(SurfaceHolder mHolder, int format, int width,  int height) 
    {
      // TopoDroidLog.Log( TopoDroidLog.LOG_PLOT, "surfaceChanged " );
      // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder mHolder) 
    {
      Log.v( "ThManager", "surface created " );
      if (thread == null ) {
        thread = new DrawThread(mHolder);
      }
      thread.setRunning(true);
      thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder mHolder) 
    {
      Log.v( "ThManager", "surface destroyed " );
      boolean retry = true;
      thread.setRunning(false);
      while (retry) {
        try {
          thread.join();
          retry = false;
        } catch (InterruptedException e) {
          // we will try it again and again...
        }
      }
      thread = null;
    }

}

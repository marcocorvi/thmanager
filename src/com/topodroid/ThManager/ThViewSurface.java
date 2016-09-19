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
import android.graphics.*; // Bitmap, Matrix, Paint
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
    ArrayList< ThViewEquate > mEquates;

    float mXoffset;
    float mYoffset;
    float mZoom;
    private Matrix mMatrix;
    private Paint  mPaint;  // equate paint

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
      mMatrix = new Matrix();
      mPaint  = new Paint();
      mPaint.setDither(true);
      mPaint.setColor( 0xffff3333 ); // dark red
      mPaint.setStyle( Paint.Style.STROKE );
      mPaint.setPathEffect( new DashPathEffect( new float[]{ 10, 20 }, 0 ) );
      mPaint.setStrokeJoin(Paint.Join.ROUND);
      mPaint.setStrokeCap(Paint.Cap.ROUND);
      mPaint.setStrokeWidth( 2 );

      thread = null;
      mContext = context;
      mAttrs   = attrs;
      mHolder = getHolder();
      mHolder.addCallback(this);
      mCommandManager = new ArrayList< ThViewCommand >();
      mEquates = new ArrayList< ThViewEquate >();
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

    void addEquates( ArrayList< ThEquate > equates )
    {
      // Log.v("ThManager", "add equates: size " + equates.size() );
      mEquates.clear();
      for ( ThEquate equate : equates ) {
        ArrayList< ThViewStation > vst = new ArrayList< ThViewStation >();
        for ( ThViewCommand command : mCommandManager ) {
          String survey_name = command.mSurvey.mName;
          int len = survey_name.length();
          while ( len > 0 && survey_name.charAt( len-1 ) == '.' ) --len;
          survey_name = survey_name.substring( 0, len );
          String st = equate.getSurveyStation( survey_name );
          if ( st != null ) {
            vst.add( command.getViewStation( st ) );
          // } else {
          //   Log.v("ThManager", "survey " + survey_name + " has no equate");
          }
        }
        if ( vst.size() > 1 ) {
          ThViewEquate veq = new ThViewEquate( equate );
          for ( ThViewStation vs : vst ) veq.addViewStation( vs );
          mEquates.add( veq );
        }
      }
      // for ( ThViewEquate veq : mEquates ) veq.dump();
    }

    void addSurvey( ThSurvey survey, int color, float xoff, float yoff, ArrayList< ThEquate > equates )
    {
      ThViewCommand command = new ThViewCommand( survey, color, xoff, yoff );
      ArrayList< String > equate_stations = new ArrayList<String>();

      String survey_name = survey.getName();
      int len = survey_name.length();
      while ( len > 0 && survey_name.charAt( len-1 ) == '.' ) --len;
      survey_name = survey_name.substring( 0, len );
      for ( ThEquate equate : equates ) {
        String station = equate.getSurveyStation( survey_name );
        if ( station != null ) {
          // Log.v("ThManager", "equate station " + station + " survey <" + survey_name  + ">" );
          equate_stations.add( station );
        }
      }
      // Log.v("ThManager", "Survey " + survey.mName + " equated stations " + equate_stations.size() );
      // for ( String st : equate_stations ) Log.v("ThManager", "station " + st);

      for ( ThStation st : survey.mStations ) {
        boolean equated = false;
        for ( String name : equate_stations ) {
          if ( name.equals( st.mName ) ) { equated = true; break; }
        }
        command.addStation( st, equated );
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
      // scale matrix
      mMatrix = new Matrix();
      mMatrix.postTranslate( mXoffset, mYoffset );
      mMatrix.postScale( mZoom, mZoom );
    }

    void changeZoom( float f )
    {
      float zoom0 = mZoom;
      float zoom1 = zoom0 * f;
      float dx = mWidth*(1/zoom1-1/zoom0)/2;
      float dy = mHeight*(1/zoom1-1/zoom0)/2;
      transform( dx, dy, f );
      // FIXME TODO translate towards (0,0) so that the offset does not change
      // transform( 0, 0, f );
    }

    boolean getSurveyAt( float x, float y, ThViewCommand cmd )
    {
      if ( cmd == null ) {
        x = x / mZoom; // canvasToSceneX( x );
        y = y / mZoom; // canvasToSceneY( y );
      } // else 
        // x,y are scene coords
      // Log.v("ThManager", "get survey at " + x + " " + y );
      mCommand = null;
      double dmin = 100000; // FIXME a large number
      for ( ThViewCommand command : mCommandManager ) {
        if ( command != cmd ) {
          double d = command.getStationAt( x, y );
          if ( d < 40 && d < dmin ) {
            dmin = d;
            mCommand = command;
          }
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
        // update equates
        synchronized( mEquates ) {
          for ( ThViewEquate equate : mEquates ) {
            equate.shift( dx, dy, mCommand );
          }
        }
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
        // the view-stations in the view-equate have different transformation matrix
        // the two matrices have the same scale, but different translations
        synchronized( mEquates ) {
          for ( ThViewEquate equate : mEquates ) equate.draw( canvas, mMatrix, mPaint );
        }
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
      // Log.v( "ThManager", "surface created " );
      if (thread == null ) {
        thread = new DrawThread(mHolder);
      }
      thread.setRunning(true);
      thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder mHolder) 
    {
      // Log.v( "ThManager", "surface destroyed " );
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

/* @file ThViewActivity.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief ThManager main drawing activity
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.ThManager;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Path;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.view.Display;

import android.widget.ZoomControls;
import android.widget.ZoomButton;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;
import android.widget.Toast;

import android.util.FloatMath;
// import android.util.DisplayMetrics;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;

/**
 */
public class ThViewActivity extends Activity
                           implements View.OnTouchListener
                                      , OnZoomListener
{
    private ThManagerApp mApp;

    private ThViewSurface mDrawingSurface;
    private boolean mIsNotMultitouch;

    private boolean mEditMove;    // whether moving the selected point
    private int mTouchMode = MODE_MOVE;

    ZoomButtonsController mZoomBtnsCtrl;
    View mZoomView;
    ZoomControls mZoomCtrl;
    // ZoomButton mZoomOut;
    // ZoomButton mZoomIn;
    private float oldDist;  // zoom pointer-sapcing

    private static final float ZOOM_INC = 1.4f;
    private static final float ZOOM_DEC = 1.0f/ZOOM_INC;

    public static final int MODE_MOVE  = 1;
    public static final int MODE_SHIFT = 2; // change point symbol position
    public static final int MODE_ZOOM  = 3;

    public int mMode   = MODE_SHIFT;
    private float mSaveX;
    private float mSaveY;
    private float mSave0X;  // first pointer saved coords
    private float mSave0Y;
    private float mSave1X;  // second pointer saved coords
    private float mSave1Y;
    private PointF mOffset  = new PointF( 0f, 0f );
    // private PointF mOffset0 = new PointF( 0f, 0f );
    private boolean doMove = false;

    @Override
    public void onVisibilityChanged(boolean visible)
    {
      mZoomBtnsCtrl.setVisible( visible );
    }

    @Override
    public void onZoom( boolean zoomin )
    {
      if ( zoomin ) changeZoom( ZOOM_INC );
      else changeZoom( ZOOM_DEC );
    }

    private void changeZoom( float f ) 
    {
      mDrawingSurface.changeZoom( f );
    }

    public void zoomIn()  { changeZoom( ZOOM_INC ); }
    public void zoomOut() { changeZoom( ZOOM_DEC ); }

    static final float SCALE_FIX = 20.0f; 

    static float worldToSceneX( float x ) { return x * SCALE_FIX; }
    static float worldToSceneY( float y ) { return y * SCALE_FIX; }

    static float sceneToWorldX( float x ) { return x/SCALE_FIX; }
    static float sceneToWorldY( float y ) { return y/SCALE_FIX; }

    
    // --------------------------------------------------------------------------------------

    protected void setTheTitle()
    {
    }

    ArrayList< ThViewCommand > getCommands() { return mDrawingSurface.mCommandManager; }

    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
      super.onCreate(savedInstanceState);

      // Display display = getWindowManager().getDefaultDisplay();
      // DisplayMetrics dm = new DisplayMetrics();
      // display.getMetrics( dm );
      // int width = dm widthPixels;
      int width  = getResources().getDisplayMetrics().widthPixels;
      int height = getResources().getDisplayMetrics().heightPixels;

      mIsNotMultitouch = ! getPackageManager().hasSystemFeature( PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH );

      setContentView(R.layout.thview_activity);
      // getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

      mApp = (ThManagerApp)getApplication();

      mDrawingSurface = (ThViewSurface) findViewById(R.id.drawingSurface);
      mDrawingSurface.setActivity( this );
      mDrawingSurface.setOnTouchListener(this);
      // mDrawingSurface.setBuiltInZoomControls(true);
      mDrawingSurface.setDisplayCenter( width/2, height/2 );

      if ( mIsNotMultitouch ) {
        mZoomView = (View) findViewById(R.id.zoomView );
        mZoomBtnsCtrl = new ZoomButtonsController( mZoomView );
        mZoomBtnsCtrl.setOnZoomListener( this );
        mZoomBtnsCtrl.setVisible( true );
        mZoomBtnsCtrl.setZoomInEnabled( true );
        mZoomBtnsCtrl.setZoomOutEnabled( true );
        mZoomCtrl = (ZoomControls) mZoomBtnsCtrl.getZoomControls();
        // ViewGroup vg = mZoomBtnsCtrl.getContainer();
      }

      setTheTitle();

      Bundle extras = getIntent().getExtras();

      doStart();
      mDrawingSurface.transform( width/2, height/2, 1 );
    }

    @Override
    protected synchronized void onResume()
    {
      super.onResume();
      doResume();
    }

    @Override
    protected synchronized void onPause() 
    { 
      super.onPause();
      doPause();
    }

    @Override
    protected synchronized void onStart()
    {
      super.onStart();
    }

    @Override
    protected synchronized void onStop()
    {
      super.onStop();
      doStop();
    }

    private void doResume()
    {
      mDrawingSurface.isDrawing = true;
    }

    private void doPause()
    {
      if ( mIsNotMultitouch ) mZoomBtnsCtrl.setVisible(false);
      mDrawingSurface.isDrawing = false;
    }

    private void doStop()
    {
    }

// ----------------------------------------------------------------------------


    private void doStart()
    {
      ArrayList< ThSurvey > surveys = mApp.mViewSurveys;
      Log.v( "ThManager", "ThView nr. surveys " + surveys.size() );
      int color[] = new int[6];
      color[0] = 0xffffffff;
      color[1] = 0xffff00ff;
      color[2] = 0xffffff00;
      color[3] = 0xff00ffff;
      color[4] = 0xffff0000;
      color[5] = 0xff00ff00;
      int k = 0;
      for ( ThSurvey survey : surveys ) {
        mDrawingSurface.addSurvey( survey, color[k%6], 0, 0 );
        ++k;
      }
    }

    private void doSelectAt( float x_scene, float y_scene )
    {
    }

    private void dumpEvent( WrapMotionEvent ev )
    {
      String name[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "PTR_DOWN", "PTR_UP", "7?", "8?", "9?" };
      StringBuilder sb = new StringBuilder();
      int action = ev.getAction();
      int actionCode = action & MotionEvent.ACTION_MASK;
      sb.append( "Event action_").append( name[actionCode] );
      if ( actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP ) {
        sb.append( "(pid " ).append( action>>MotionEvent.ACTION_POINTER_ID_SHIFT ).append( ")" );
      }
      sb.append( " [" );
      for (int i=0; i<ev.getPointerCount(); ++i ) {
        sb.append( "#" ).append( i );
        sb.append( "(pid " ).append( ev.getPointerId(i) ).append( ")=" ).append( (int)(ev.getX(i)) ).append( "." ).append( (int)(ev.getY(i)) );
        if ( i+1 < ev.getPointerCount() ) sb.append( ":" );
      }
      sb.append( "]" );
      Log.v("ThManager", sb.toString() );
    }
    

    float spacing( WrapMotionEvent ev )
    {
      int np = ev.getPointerCount();
      if ( np < 2 ) return 0.0f;
      float x = ev.getX(1) - ev.getX(0);
      float y = ev.getY(1) - ev.getY(0);
      return FloatMath.sqrt(x*x + y*y);
    }

    void saveEventPoint( WrapMotionEvent ev )
    {
      int np = ev.getPointerCount();
      if ( np >= 1 ) {
        mSave0X = ev.getX(0);
        mSave0Y = ev.getY(0);
        if ( np >= 2 ) {
          mSave1X = ev.getX(1);
          mSave1Y = ev.getY(1);
        } else {
          mSave1X = mSave0X;
          mSave1Y = mSave0Y;
        } 
      }
    }

    
    void shiftByEvent( WrapMotionEvent ev )
    {
      float x0 = 0.0f;
      float y0 = 0.0f;
      float x1 = 0.0f;
      float y1 = 0.0f;
      int np = ev.getPointerCount();
      if ( np >= 1 ) {
        x0 = ev.getX(0);
        y0 = ev.getY(0);
        if ( np >= 2 ) {
          x1 = ev.getX(1);
          y1 = ev.getY(1);
        } else {
          x1 = x0;
          y1 = y0;
        } 
      }
      float x_shift = ( x0 - mSave0X + x1 - mSave1X ) / 2;
      float y_shift = ( y0 - mSave0Y + y1 - mSave1Y ) / 2;
      mSave0X = x0;
      mSave0Y = y0;
      mSave1X = x1;
      mSave1Y = y1;
    
      float zoom = mDrawingSurface.mZoom;
      if ( Math.abs( x_shift ) < 60 && Math.abs( y_shift ) < 60 ) {
        x_shift /= zoom;               // add shift to offset
        y_shift /= zoom; 
        mDrawingSurface.transform( x_shift, y_shift, 1 );
      }
    }


    int mWithStation = 0;

    public boolean onTouch( View view, MotionEvent rawEvent )
    {
      WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
      // dumpEvent( event );

      float x_canvas = event.getX();
      float y_canvas = event.getY();

      if ( mIsNotMultitouch && y_canvas > mDrawingSurface.mHeight-20 ) {
        mZoomBtnsCtrl.setVisible( true );
        // mZoomCtrl.show( );
      }
      // Log.v("ThManager", "touch canvas " + x_canvas + " " + y_canvas ); 
      // float x_scene = mDrawingSurface.canvasToSceneX( x_canvas );
      // float y_scene = mDrawingSurface.canvasToSceneY( y_canvas );
      // Log.v("ThManager", "touch scene " + x_scene + " " + y_scene );

      int action = event.getAction() & MotionEvent.ACTION_MASK;

      if (action == MotionEvent.ACTION_POINTER_DOWN) {
        mTouchMode = MODE_ZOOM;
        oldDist = spacing( event );
        saveEventPoint( event );
        // Log.v("ThManager", "POINTER DOWN old dist " + oldDist );
        doMove = false;

      } else if ( action == MotionEvent.ACTION_POINTER_UP) {
        mTouchMode = MODE_MOVE;
        /* nothing */
        doMove = false;
        mSaveX = x_canvas;
        mSaveY = y_canvas;
        // Log.v("ThManager", "POINTER UP " + mSaveX + " " + mSaveY );

      // ---------------------------------------- DOWN
      } else if (action == MotionEvent.ACTION_DOWN) {
        // check if selected a station
        mSaveX = x_canvas;
        mSaveY = y_canvas;
        doMove = true;
        if ( mWithStation == 0 ) {
          boolean ret = mDrawingSurface.getSurveyAt( mSaveX, mSaveY );
          // Log.v("ThManager", "DOWN at " + mSaveX + " " + mSaveY + " at " + ret );
          if ( ret ) {
            mWithStation = 1;
            setTitle( "ThManager " + mDrawingSurface.selectedCommandName() + " " + mDrawingSurface.selectedStationName() );
          } else {
            setTitle( "ThManager" );
          }
        } else if ( mWithStation == 1 ) {
          mWithStation = 2;
        }

      // ---------------------------------------- MOVE
      } else if ( action == MotionEvent.ACTION_MOVE ) {
        if ( mTouchMode == MODE_MOVE) {
          // Log.v("ThManager", "MOVE (move) to " + x_canvas + " " + y_canvas );
          float x_shift = x_canvas - mSaveX; // compute shift
          float y_shift = y_canvas - mSaveY;
          if ( doMove ) {
            if ( Math.abs( x_shift ) < 60 && Math.abs( y_shift ) < 60 ) {
              float zoom = mDrawingSurface.mZoom;
              x_shift /= zoom;                // add shift to offset
              y_shift /= zoom; 
              mDrawingSurface.shift( x_shift, y_shift );
              // mDrawingSurface.refresh();
              mSaveX = x_canvas; 
              mSaveY = y_canvas;
            }
          }
          doMove = true;
        } else { // mTouchMode == MODE_ZOOM
          float newDist = spacing( event );
          // Log.v("ThManager", "MOVE (zoom) dist " + newDist );
          if ( newDist > 16.0f && oldDist > 16.0f ) {
            float factor = newDist/oldDist;
            if ( factor > 0.05f && factor < 4.0f ) {
              changeZoom( factor );
              oldDist = newDist;
            }
          }
          shiftByEvent( event );
        }

      // ---------------------------------------- UP
      } else if (action == MotionEvent.ACTION_UP) {
        // Log.v("ThManager", "UP");
        if ( mWithStation == 2 ) {
          mDrawingSurface.resetStation();
          mWithStation = 0;
        }
        if ( mTouchMode == MODE_ZOOM ) {
          mTouchMode = MODE_MOVE;
        }
        // mSaveX = x_canvas; 
        // mSaveY = y_canvas;
        doMove = false;
      }
      return true;
    }



  // ---------------------------------------------------------

  private MenuItem mMIequate;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    super.onCreateOptionsMenu( menu );
    mMIequate  = menu.add( R.string.menu_equate );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    if ( item == mMIequate ) {
      // TODO 
      (new ThViewEquateDialog( this, this, mApp ) ).show();
    }
    return true;
  }

}
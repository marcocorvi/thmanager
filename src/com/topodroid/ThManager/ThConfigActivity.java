/** @file ThConfigActivity.java
 *
 * @brief interface activity for a thconfig filre
 *
 */
package com.topodroid.ThManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Dialog;
// import android.widget.Button;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
// import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
// import android.net.Uri;

import android.view.Menu;
// import android.view.SubMenu;
import android.view.MenuItem;
// import android.view.MenuInflater;

import android.util.Log;

public class ThConfigActivity extends Activity
{
  ThInputAdapter mThInputAdapter;
  ThManagerApp mApp;

  private FilenameFilter filterTh;

  private ListView mList;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );

    mApp = (ThManagerApp) getApplication();

    mApp.mConfig = null;
    Bundle extras = getIntent().getExtras();
    if ( extras != null ) {
      String path = extras.getString( ThManagerApp.THCONFIG_PATH );
      if ( path != null ) {
        File file = new File(path);
        Log.v( ThManagerApp.TAG, "ThConfigActivity path <" + path + ">" );
        mApp.mConfig = new ThConfig( path );
        mApp.mConfig.readThConfig();
        if ( file.exists() ) {
          setTitle( "PROJECT " + mApp.mConfig.toString() );
        } else {
          mApp.mConfig = null;
          Toast.makeText( this, R.string.no_file, Toast.LENGTH_LONG ).show();
        }
      } else {
        // Log.v("ThManager", "ThConfig activity missing ThConfig path");
        Toast.makeText( this, R.string.no_path, Toast.LENGTH_LONG ).show();
      }
    }
    if ( mApp.mConfig == null ) {
      doFinish( ThManagerApp.RESULT_THCONFIG_NONE );
    } else {
      setContentView(R.layout.thconfig_activity);
      // getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

      mList = (ListView) findViewById(R.id.list);
      // mList.setOnItemClickListener( this );
      // mList.setLongClickable( true );
      // mList.setOnItemLongClickListener( this );
      mList.setDividerHeight( 2 );

      updateList();
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    // Log.v("ThManager", "ThConfig activity on pause");
    if ( mApp.mConfig != null ) mApp.mConfig.writeThConfig( false );
  }

  boolean hasSource( String name ) 
  {
    return mApp.mConfig.hasInput( name );
  }

  /** update surveys list
   */
  void updateList()
  {
    if ( mApp.mConfig != null ) {
      // Log.v("ThManager", "ThConfig input nr. " + mApp.mConfig.mInputs.size() );
      mThInputAdapter = new ThInputAdapter( this, R.layout.row, mApp.mConfig.mInputs );
      mList.setAdapter( mThInputAdapter );
    } else {
      Toast.makeText( this, R.string.no_thconfig, Toast.LENGTH_LONG ).show();
    }
  }


  // ---------------------------------------------------------------
  // OPTIONS MENU

  private MenuItem mMIadd;      // add survey
  private MenuItem mMIdrop;     // drop survey(s)
  private MenuItem mMIview;     // open 2D view
  private MenuItem mMIequates;
  private MenuItem mMIdelete;   // delete
  // private MenuItem mMIoptions;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    super.onCreateOptionsMenu( menu );

    mMIadd     = menu.add( R.string.menu_add );
    mMIdrop    = menu.add( R.string.menu_drop );
    mMIview    = menu.add( R.string.menu_view );
    mMIequates = menu.add( R.string.menu_equates );
    mMIdelete  = menu.add( R.string.menu_delete);
    // mMIoptions = menu.add( R.string.menu_options );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    // if ( item == mMIoptions ) { // OPTIONS DIALOG
    //   // Intent optionsIntent = new Intent( this, TopoDroidPreferences.class );
    //   // optionsIntent.putExtra( TopoDroidPreferences.PREF_CATEGORY, TopoDroidPreferences.PREF_CATEGORY_ALL );
    //   // startActivity( optionsIntent );
    // } else 
    if ( item == mMIdelete ) { 
      askDelete();
    } else if ( item == mMIadd ) { 
      (new ThSourcesDialog(this, this)).show();
    } else if ( item == mMIdrop ) { 
      dropSurveys();
    } else if ( item == mMIview ) { 
      startThSurveysActivity();
    } else if  ( item == mMIequates ) { 
      (new ThEquatesDialog( this, mApp.mConfig, null )).show();
    }
    return true;
  }

  // ------------------------ DISPLAY -----------------------------
  private void startThSurveysActivity()
  {
    ThSurvey mySurvey = new ThSurvey( "." );
    ThUnits  myUnits  = new ThUnits();

    for ( ThInput input : mApp.mConfig.mInputs ) {
      if ( input.isChecked() ) {
        // Log.v("ThManager", "parse file " + input.mFilepath );
        ThParser parser = new ThParser( input.mFilepath, mySurvey, myUnits );
      }
    }
    if ( mySurvey.mSurveys.size() == 0 ) {
      Toast.makeText( this, "no surveys", Toast.LENGTH_SHORT ).show();
      return;
    }
    mApp.mViewSurveys = new ArrayList< ThSurvey >(); // list of displayed surveys
    for ( ThSurvey survey : mySurvey.mSurveys ) {
      survey.reduce();
      mApp.mViewSurveys.add( survey );
    }
    // TODO start drawing activity with reduced surveys
    Intent intent = new Intent( this, ThViewActivity.class );
    startActivity( intent );
  }

  // ------------------------ ADD ------------------------------
  // called by ThSourcesDialog with a list of sources filenames
  //
  void addSources( ArrayList<String> filenames )
  {
    for ( String filename : filenames ) {
      // Log.v("ThManager", "add  source " + filename );
      String filepath = filename;
      if ( ! filename.startsWith("/") ) {
        // filepath = mApp.mConfig.mParentDir + "../th/" + filename;     
        filepath = ThManagerPath.getThPath( filename );     
      }
      filepath = new File(filepath).getAbsolutePath();
      mThInputAdapter.add( new ThInput( filename, filepath ) ) ;
    }
    updateList();
  }

  // ------------------------ DELETE ------------------------------
  private void askDelete()
  {
    new ThAlertDialog( this, getResources(), 
                             getResources().getString( R.string.ask_delete_thconfig ),
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick( DialogInterface dialog, int btn ) {
          doDelete();
        }
      }
    );
  }

  void doDelete()
  {
    // if ( ! ThManagerApp.deleteThConfigFile( mApp.mConfig.mFilepath ) ) { 
    //   Toast.makeText( this, "delete FAILED", Toast.LENGTH_LONG ).show();
    // } else {
      doFinish( ThManagerApp.RESULT_THCONFIG_DELETE );
    // }
  }

  void doFinish( int result )
  {
    Intent intent = new Intent();
    if ( mApp.mConfig != null ) {
      intent.putExtra( ThManagerApp.THCONFIG_PATH, mApp.mConfig.mFilepath );
    } else {
      intent.putExtra( ThManagerApp.THCONFIG_PATH, "no_path" );
    }
    setResult( result, intent );
    finish();
  }
  // ---------------------- DROP SURVEYS ----------------------------
  void dropSurveys()
  {
    ArrayList< ThInput > inputs = new ArrayList< ThInput >();
    final Iterator it = mApp.mConfig.mInputs.iterator();
    while ( it.hasNext() ) {
      ThInput input = (ThInput) it.next();
      if ( ! input.isChecked() ) {
        inputs.add( input );
      }
    }
    mApp.mConfig.mInputs = inputs;
    updateList();
  }

  // ---------------------- SAVE -------------------------------------

  @Override
  public void onBackPressed()
  {
    // Log.v("ThManager", "ThConfig activity back pressed");
    // if ( mApp.mConfig != null ) mApp.mConfig.writeThConfig( false );
    doFinish( ThManagerApp.RESULT_THCONFIG_OK );
  }

}

/** @file ThManagerActivity.java
 * 
 * Displays the list of thconfig files
 * - long-pressing on a file opens it in the editor
 * - clicking on a file starts the ThConfigActivity on it
 */
package com.topodroid.ThManager;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.ArrayList;

import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Dialog;
// import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.app.Activity;
import android.net.Uri;

import android.view.Menu;
// import android.view.SubMenu;
import android.view.MenuItem;
// import android.view.MenuInflater;

import android.util.Log;

public class ThManagerActivity extends Activity
                       implements OnItemClickListener
                       // , OnItemLongClickListener

{
  ThConfigAdapter mThConfigAdapter;

  private ListView mList;


  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView(R.layout.thmanager_activity);
    // getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

    setTitle("TH PROJECT MANAGER");

    ThManagerApp app = (ThManagerApp) getApplication();
    app.mActivity = this;

    mList = (ListView) findViewById(R.id.list);
    mList.setOnItemClickListener( this );
    // mList.setLongClickable( true );
    // mList.setOnItemLongClickListener( this );
    mList.setDividerHeight( 2 );
    
    updateThConfigList();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    // Log.v("ThManager", "ThManager on resume");
    updateThConfigList();
  }
    
  void updateThConfigList()
  {
    mThConfigAdapter = new ThConfigAdapter( this, R.layout.row, new ArrayList<ThConfig>() );
    File[] thconfigs = ThManagerPath.scanThConfigDir();
    for ( File file : thconfigs ) {
      // Log.v("ThManager", "ThManager activity update " + file.getAbsolutePath() );
      mThConfigAdapter.add( new ThConfig( file.getAbsolutePath() ) );
    }
    mList.setAdapter( mThConfigAdapter );
  }


  @Override
  public void onItemClick( AdapterView<?> parent, View view, int pos, long id )
  {
    ThConfig thconfig = mThConfigAdapter.getItem( pos );
    // TODO start ThConfigActivity or Dialog
    Intent intent = new Intent( this, ThConfigActivity.class );
    // Log.v("ThManager", "start ThConfig " + thconfig.mFilepath );
    intent.putExtra( ThManagerApp.THCONFIG_PATH, thconfig.mFilepath );
    try {
      startActivityForResult( intent, ThManagerApp.REQUEST_THCONFIG );
    } catch ( ActivityNotFoundException e ) {
      Toast.makeText( this, R.string.no_editor, Toast.LENGTH_LONG ).show();
    }
  }

  /** add a new thconfig file
   * @param name    thconfig name
   */
  void addThConfig( String name )
  {
    String filename = name;
    if ( ! filename.endsWith(".thconfig") ) filename = filename + ".thconfig";
    String path = ThManagerPath.getThConfigPath( filename );
    ThConfig thconfig = new ThConfig( path );
    // updateThConfigList();
    mThConfigAdapter.add( thconfig );
  }

  /** deletes a thconfig file
   * @param filename thconfig filename
   */
  // void deleteThConfig( String filepath )
  // {
  //   File file = new File( filepath );
  //   file.delete();
  //   updateThConfigList();
  // }
    
  public void onActivityResult( int request, int result, Intent intent ) 
  {
    Bundle extras = (intent != null )? intent.getExtras() : null;
    switch ( request ) {
      case ThManagerApp.REQUEST_THCONFIG:
        if ( result == ThManagerApp.RESULT_THCONFIG_OK ) {
          // nothing 
        } else if ( result == ThManagerApp.RESULT_THCONFIG_DELETE ) {
          // get ThConfig name and delete it
          String path = extras.getString( ThManagerApp.THCONFIG_PATH );
          mThConfigAdapter.deleteThConfig( path );
          mList.invalidate();
          // updateThConfigList();
        } else if ( result == ThManagerApp.RESULT_THCONFIG_NONE ) {
          // nothing
        }
    }
  }



  // ---------------------------------------------------------------
  // OPTIONS MENU

  private MenuItem mMInew;
  private MenuItem mMIhelp;
  private MenuItem mMIoptions;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    super.onCreateOptionsMenu( menu );

    mMInew     = menu.add( R.string.menu_new );
    mMIoptions = menu.add( R.string.menu_options );
    mMIhelp    = menu.add( R.string.menu_help );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    if ( item == mMIoptions ) { // OPTIONS DIALOG
      Intent intent = new Intent( this, ThManagerPreferences.class );
      startActivity( intent );
    } else if ( item == mMIhelp ) { 
      new ThManagerHelpDialog( this ).show();
      // TODO
    } else if ( item == mMInew ) { 
      (new ThConfigDialog( this, this )).show();
    } else {
      return false;
    }
    return true;
  }

}

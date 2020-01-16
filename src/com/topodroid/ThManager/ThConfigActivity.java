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
import android.widget.Button;
import android.widget.ArrayAdapter;

import android.view.View;
// import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;

import android.content.res.Resources;

import android.view.Menu;
// import android.view.SubMenu;
import android.view.MenuItem;
// import android.view.MenuInflater;

import android.net.Uri;

import android.util.Log;

public class ThConfigActivity extends Activity
                              implements OnClickListener
                              // , OnItemClickListener
{
  ThInputAdapter mThInputAdapter;
  ThManagerApp mApp;

  private FilenameFilter filterTh;

  private static String[] mExportTypes = { "Therion", "Survex" };

  // HorizontalListView mListView;
  // HorizontalButtonView mButtonView1;

  ListView mList;
  // Button   mImage;
  // ListView mMenu;
  // ArrayAdapter<String> mMenuAdapter;
  Button[] mButton1;

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

      mList = (ListView) findViewById(R.id.th_list);
      // mList.setOnItemClickListener( this );
      mList.setDividerHeight( 2 );

      // mImage = (Button) findViewById( R.id.handle );
      // mImage.setOnClickListener( this );
      // mMenu = (ListView) findViewById( R.id.menu );
      // mMenuAdapter = null;
      // setMenuAdapter( getResources() );
      // closeMenu();
      // mMenu.setOnItemClickListener( this );

      // mListView = (HorizontalListView) findViewById(R.id.listview);
      resetButtonBar();

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
      mList.invalidate();
    } else {
      Toast.makeText( this, R.string.no_thconfig, Toast.LENGTH_LONG ).show();
    }
  }

  
  // -------------------------------------------------
  // boolean onMenu;
  int mNrButton1 = 8;
  // int mNrMenus   = 5;
  private static int izons[] = { 
    R.drawable.iz_add,
    R.drawable.iz_drop,
    R.drawable.iz_view,
    R.drawable.iz_equates,
    R.drawable.iz_3d,
    R.drawable.iz_export,
    // R.drawable.iz_note,
    R.drawable.iz_delete,
    R.drawable.iz_exit,
  };
  // private static int menus[] = { 
  //   R.string.menu_add,
  //   R.string.menu_drop,
  //   R.string.menu_view,
  //   R.string.menu_equates,
  //   R.string.menu_delete
  // };

  private void resetButtonBar()
  {
    // mImage.setBackgroundDrawable( MyButton.getButtonBackground( mApp, getResources(), R.drawable.iz_menu ) );

    if ( mNrButton1 > 0 ) {
      // int size = mApp.setListViewHeight( mListView );
      // MyButton.resetCache( size );
      int size = ThManagerApp.getScaledSize( this );
      LinearLayout layout = (LinearLayout) findViewById( R.id.list_layout );
      layout.setMinimumHeight( size + 40 );
      LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
      lp.setMargins( 10, 10, 10, 10 );
      lp.width  = size;
      lp.height = size;

      // FIXME THMANAGER
      mButton1 = new Button[mNrButton1];

      for (int k=0; k<mNrButton1; ++k ) {
        mButton1[k] = MyButton.getButton( this, this, size, izons[k] );
        layout.addView( mButton1[k], lp );
      }

      // mButtonView1 = new HorizontalButtonView( mButton1 );
      // mListView.setAdapter( mButtonView1.mAdapter );
    }
  }

  // private void setMenuAdapter( Resources res )
  // {
  //   mMenuAdapter = new ArrayAdapter<String>( this, R.layout.menu );
  //   for ( int k=0; k<mNrMenus; ++k ) {
  //     mMenuAdapter.add( res.getString( menus[k] ) );  
  //   }
  //   mMenu.setAdapter( mMenuAdapter );
  //   mMenu.invalidate();
  // }

  // private void closeMenu()
  // {
  //   mMenu.setVisibility( View.GONE );
  //   onMenu = false;
  // }

  // private void handleMenu( int pos ) 
  // {
  //   closeMenu();
  //   int p = 0;
  //   if ( p++ == pos ) {        // ADD
  //     (new ThSourcesDialog(this, this)).show();
  //   } else if ( p++ == pos ) { // DROP
  //     dropSurveys();
  //   } else if ( p++ == pos ) { // VIEW
  //     startThSurveysActivity();
  //   } else if ( p++ == pos ) { // EQUATES
  //     (new ThEquatesDialog( this, mApp.mConfig, null )).show();
  //   } else if ( p++ == pos ) { // DELETE
  //     askDelete();
  //   }
  // }

  // ----------------------------------------------

  // ---------------------------------------------------------------
  // OPTIONS MENU

  // private MenuItem mMIadd;      // add survey
  // private MenuItem mMIdrop;     // drop survey(s)
  // private MenuItem mMIview;     // open 2D view
  // private MenuItem mMIequates;
  // private MenuItem mMIdelete;   // delete
  // // private MenuItem mMIoptions;

  // @Override
  // public boolean onCreateOptionsMenu(Menu menu) 
  // {
  //   super.onCreateOptionsMenu( menu );

  //   mMIadd     = menu.add( R.string.menu_add );
  //   mMIdrop    = menu.add( R.string.menu_drop );
  //   mMIview    = menu.add( R.string.menu_view );
  //   mMIequates = menu.add( R.string.menu_equates );
  //   mMIdelete  = menu.add( R.string.menu_delete);
  //   // mMIoptions = menu.add( R.string.menu_options );
  //   return true;
  // }

  // @Override
  // public boolean onOptionsItemSelected(MenuItem item) 
  // {
  //   // if ( item == mMIoptions ) { // OPTIONS DIALOG
  //   //   // Intent optionsIntent = new Intent( this, TopoDroidPreferences.class );
  //   //   // optionsIntent.putExtra( TopoDroidPreferences.PREF_CATEGORY, TopoDroidPreferences.PREF_CATEGORY_ALL );
  //   //   // startActivity( optionsIntent );
  //   // } else 
  //   if ( item == mMIdelete ) { 
  //     askDelete();
  //   } else if ( item == mMIadd ) { 
  //     (new ThSourcesDialog(this, this)).show();
  //   } else if ( item == mMIdrop ) { 
  //     dropSurveys();
  //   } else if ( item == mMIview ) { 
  //     startThSurveysActivity();
  //   } else if  ( item == mMIequates ) { 
  //     (new ThEquatesDialog( this, mApp.mConfig, null )).show();
  //   }
  //   return true;
  // }

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
    new ThAlertDialog( this, getResources(), getResources().getString( R.string.title_drop ), 
      new DialogInterface.OnClickListener() {
	@Override
	public void onClick( DialogInterface dialog, int btn ) {
          ArrayList< ThInput > inputs = new ArrayList< ThInput >();
          final Iterator it = mApp.mConfig.mInputs.iterator();
          while ( it.hasNext() ) {
            ThInput input = (ThInput) it.next();
            if ( ! input.isChecked() ) {
              inputs.add( input );
            } else {
              String survey = input.getSurveyName();
              // Log.v("ThManager", "drop survey >" + survey + "<" );
              mApp.mConfig.dropEquates( survey );
            }
          }
          mApp.mConfig.mInputs = inputs;
          updateList();
	} 
    } );
  }

  // ---------------------- SAVE -------------------------------------

  @Override
  public void onBackPressed()
  {
    // Log.v("ThManager", "ThConfig activity back pressed");
    // if ( mApp.mConfig != null ) mApp.mConfig.writeThConfig( false );
    doFinish( ThManagerApp.RESULT_THCONFIG_OK );
  }

  @Override
  public void onClick(View view)
  { 
    // if ( onMenu ) {
    //   closeMenu();
    //   return;
    // }
    Button b0 = (Button)view;

    // if ( b0 == mImage ) {
    //   if ( mMenu.getVisibility() == View.VISIBLE ) {
    //     mMenu.setVisibility( View.GONE );
    //     onMenu = false;
    //   } else {
    //     mMenu.setVisibility( View.VISIBLE );
    //     onMenu = true;
    //   }
    //   return;
    // }
    int k1 = 0;
    if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // ADD
      (new ThSourcesDialog(this, this)).show();
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // DROP
      dropSurveys();
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // VIEW
      startThSurveysActivity();
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // EQUATES
      (new ThEquatesDialog( this, mApp.mConfig, null )).show();
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // 3D
      try {
        Intent intent = new Intent( "Cave3D.intent.action.Launch" );
        intent.putExtra( "INPUT_FILE", mApp.mConfig.mFilepath );
        startActivity( intent );
      } catch ( ActivityNotFoundException e ) {
        Toast.makeText( this, "Missing Cave3D", Toast.LENGTH_SHORT ).show();
      }
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // EXPORT
      if ( mApp.mConfig != null ) {
        new ExportDialog( this, this, mExportTypes, R.string.title_export ).show();
      }
    // } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // EXTERNAL EDIT
    //   if ( mApp.mConfig != null ) {
    //     try {
    //       Intent intent = new Intent( Intent.ACTION_EDIT );
    //       Uri uri = Uri.fromFile( new File( mApp.mConfig.mFilepath ) );
    //       intent.setDataAndType( uri, "text/plain" );
    //       startActivity( intent );
    //     } catch ( ActivityNotFoundException e ) {
    //       Toast.makeText( this, "Missing edit apk", Toast.LENGTH_SHORT ).show();
    //     }
    //   }
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // DELETE
      askDelete();
    } else if ( k1 < mNrButton1 && b0 == mButton1[k1++] ) {  // EXIT
      onBackPressed();
    }
  }

  void doExport( String type, boolean overwrite )
  {
    String filepath = null;
    if ( type.equals("Therion") ) {
       filepath = mApp.mConfig.exportTherion( overwrite );
    } else if ( type.equals("Survex") ) {
       filepath = mApp.mConfig.exportSurvex( overwrite );
    }
    if ( filepath != null ) {
      Toast.makeText( this, String.format( getResources().getString(R.string.exported), filepath ),
            Toast.LENGTH_SHORT ).show();
    } else {
      Toast.makeText( this, R.string.export_failed, Toast.LENGTH_SHORT ).show();
    }
  }


  // @Override
  // public void onItemClick( AdapterView<?> parent, View view, int pos, long id )
  // {
  //   CharSequence item = ((TextView) view).getText();
  //   if ( mMenu == (ListView)parent ) {
  //     handleMenu( pos );
  //     return;
  //   }
  //   if ( onMenu ) {
  //     closeMenu();
  //     return;
  //   }
  // }

}

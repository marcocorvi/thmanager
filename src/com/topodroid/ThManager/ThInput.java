/** @file ThInput.h
 *
 */
package com.topodroid.ThManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import android.util.Log;

class ThInput extends ThFile
              implements View.OnClickListener
{
  String  mFilename; //!< filename as in the thconfig file
  boolean mChecked;

  ThInput( String filename, String filepath )
  {
    super( filepath );

    // Log.v("ThManager", "new ThInput name " + filename + " path " + filepath );

    mFilename = filename;
    mChecked  = false;
  }

  String getSurveyName()
  {
    String ret = super.toString(); // FIXME use filename without extension
    int pos = ret.lastIndexOf(".");
    return ( pos >= 0 )? ret.substring(0, pos) : ret;
  }

  // void toggleChecekd() { mChecked = ! mChecked; }

  boolean isChecked() { return mChecked; }

  @Override
  public void onClick( View v ) 
  {
    mChecked = ! mChecked;
    ((CheckBox)v).setChecked( mChecked );
  }

}


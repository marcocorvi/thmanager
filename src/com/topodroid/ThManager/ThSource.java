/** @file ThSource.java
 */
package com.topodroid.ThManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import android.util.Log;

class ThSource extends ThFile
               implements View.OnClickListener
{
  String  mFilename;
  boolean mChecked;

  public ThSource( String filename, String filepath )
  {
    super( filepath );
    mFilename = filename;
    mChecked = false;
  }

  // void toggleChecekd() { mChecked = ! mChecked; }

  boolean isChecked() { return mChecked; }

  public String toString() { return mName; }

  @Override
  public void onClick( View v ) 
  {
    mChecked = ! mChecked;
    ((CheckBox)v).setChecked( mChecked );
  }
}

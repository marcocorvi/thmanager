/** @file ThFile.java
 */
package com.topodroid.ThManager;

import android.util.Log;

class ThFile 
{
  String mName;                // name (only for display purposes)
  String mFilepath;            // thconfig file (fullpath)

  public String toString() { return mName; }

  public ThFile( String filepath )
  {
    mFilepath = filepath;
    int pos = mFilepath.lastIndexOf('/');
    mName = ( pos >= 0 )? mFilepath.substring( pos+1 ) : mFilepath;
    mName = mName.replace(".thconfig", "");
  }

}

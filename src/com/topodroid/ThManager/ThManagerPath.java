/** @file TopoDroidPath.java
 *
 * @author marco corvi
 * @date jan 2015 
 *
 * @brief ThManager application paths
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 * CHANGES
 */
package com.topodroid.ThManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

// import java.io.IOException;
// import java.io.FileNotFoundException;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

// import android.util.Log;

public class ThManagerPath
{
  static private String mThConfigDir; // pathname of dir with thconfig projects
  static private String mThDir;       // pathname of dir with th survey files

  static private FilenameFilter mFilterThConfig; // filename filter for ".thconfig"
  static private FilenameFilter mFilterTh;       // filename filter for ".th"

  // ------------------------------------------------------------
  // PATHS

  static String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); // app base path
  static String APP_DEFAULT_PATH = EXTERNAL_STORAGE_PATH + "/TopoDroid/";
  static String APP_BASE_PATH = APP_DEFAULT_PATH;

  // FIXME BASEPATH 
  // remove comments when ready to swicth to new Android app path system
  //
  static void setPaths( String path )
  {
    File dir = null;
    if ( path != null ) {
      String cwd = EXTERNAL_STORAGE_PATH + "/" + path;
      dir = new File( cwd );
      if ( ! dir.exists() ) {
        dir.mkdirs();
      }
      if ( dir.isDirectory() && dir.canWrite() ) {
        APP_BASE_PATH = cwd + "/";
      }
    }
    dir = new File( APP_BASE_PATH );
    if ( ! dir.exists() ) {
      if ( ! dir.mkdir() ) {
        APP_BASE_PATH = APP_DEFAULT_PATH;
      }
    }
    // Log.v(TAG, "Base Path \"" + APP_BASE_PATH + "\"" );

    mThConfigDir = APP_BASE_PATH + "thconfig/";
    mThDir       = APP_BASE_PATH + "th/";

    File f1 = new File( mThConfigDir );
    if ( ! f1.exists() ) f1.mkdirs( );

    File f2 = new File( mThDir );
    if ( ! f2.exists() ) f2.mkdirs( );
  }

  static void setFilters()
  {
    mFilterThConfig = new FilenameFilter() {
       public boolean accept(File dir, String name) {
         return name.endsWith( "thconfig" );
       }
    };

    mFilterTh  = new FilenameFilter() {
       public boolean accept(File dir, String name) {
         return name.endsWith( "th" );
       }
    };
  }


  // static String noSpaces( String s )
  // {
  //   return ( s == null )? null 
  //     : s.trim().replaceAll("\\s+", "_").replaceAll("/", "-").replaceAll("\\*", "+").replaceAll("\\\\", "");
  // }

  // static void checkPath( String filename )
  // {
  //   if ( filename == null ) return;
  //   File fp = new File( filename );
  //   checkPath( new File( filename ) );
  // }

  // static void checkPath( File fp ) 
  // {
  //   if ( fp == null || fp.exists() ) return;
  //   File fpp = fp.getParentFile();
  //   if ( fpp.exists() ) return;
  //   fpp.mkdirs(); // return boolean : must check ?
  // }


  static File[] scanThConfigDir()
  {
    File dir = new File( mThConfigDir );
    return dir.listFiles( mFilterThConfig );
  }

  static File[] scanThDir()
  {
    File dir = new File( mThDir );
    return dir.listFiles( mFilterTh );
  }

  static String getThPath( String th_name ) 
  {
    return mThDir + th_name; 
  }

  static String getThConfigPath( String thconfig_name ) 
  {
    return mThConfigDir + thconfig_name; 
  }

  // ------------------------------------------------------------------
  // FILE NAMES

  static File[] getTopoDroidFiles( )
  {
    File dir = new File( EXTERNAL_STORAGE_PATH );
    return dir.listFiles( new FileFilter() {
      public boolean accept( File pathname ) { 
        if ( ! pathname.isDirectory() ) return false;
        if ( pathname.getName().toLowerCase().startsWith( "topodroid" ) ) return true;
        return false;
      }
    } );
  }

}

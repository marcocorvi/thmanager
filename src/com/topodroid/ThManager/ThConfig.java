/** @file ThConfig.java
 */
package com.topodroid.ThManager;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

class ThConfig extends ThFile
{
  String mParentDir;           // parent directory
  String mSurveyName;
  ThSurvey mSurvey;            // inline survey in the thconfig file
  ArrayList< ThInput > mInputs; // surveys: th files on input
  ArrayList< ThEquate > mEquates;
  private boolean mRead;       // whether the ThConfig has read the file

  public ThConfig( String filepath )
  {
    super( filepath );

    // Log.v("ThManager", "ThConfig cstr filepath " + filepath );
    mParentDir = (new File( filepath )).getParentFile().getName() + "/";
    mSurvey  = null;
    mInputs = new ArrayList< ThInput >();
    mEquates = new ArrayList< ThEquate >();
    mRead = false;
  }

  void addEquate( ThEquate equate ) { mEquates.add( equate ); }

  void removeEquate( ThEquate equate ) { mEquates.remove( equate ); }

  void readThConfig()
  {
    if ( mRead ) return;
    // Log.v( ThManagerApp.TAG, "readThConfig() for file " + mName );
    readFile();
    // Log.v( ThManagerApp.TAG, "ThConfig() inputs " + mInputs.size() + " equates " + mEquates.size() );
    mRead = true;
  }
    
  boolean hasInput( String name )
  {
    // Log.v("ThManager", "ThConfig check input name " + name );
    for ( ThInput input : mInputs ) {
      // Log.v("ThManager", "ThConfig check input " + input.mName );
      if ( input.mName.equals(name) ) return true;
    }
    return false;
  }

  void addInput( String filename, String filepath )
  {
    // Log.v("ThManager", "add input name " + filename + " path " + filepath );
    mInputs.add( new ThInput( filename, filepath ) );
  }

  void dropInput( String name )
  {
    for ( ThInput input : mInputs ) {
      if ( name.equals( input.mName ) ) {
        mInputs.remove( input );
        return;
      }
    }
  }


// ---------------------------------------------------------------
// READ and WRITE
  static String currentDate()
  {
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd", Locale.US );
    return sdf.format( new Date() );
  }


  void writeThConfig( boolean force )
  {
    if ( mRead || force ) {
      writeTherion( mFilepath );
    }
  }

  String exportTherion( boolean overwrite )
  {
    String filepath = mFilepath.replace(".thconfig", ".th").replace("/thconfig/", "/th/");
    File file = new File( filepath );
    if ( file.exists() ) {
      if ( ! overwrite ) return null;
    } else {
      File dir = file.getParentFile();
      if ( dir != null ) dir.mkdirs();
    }
    writeTherion( filepath );
    return filepath;
  }

  void writeTherion( String filepath )
  {
    try {
      FileWriter fw = new FileWriter( filepath );
      PrintWriter pw = new PrintWriter( fw );
      pw.format("# created by ThManager %s - %s\n", ThManagerApp.VERSION, currentDate() );
      pw.format("source\n");
      pw.format("  survey %s\n", mSurveyName );
      for ( ThInput s : mInputs ) {
        // FIXME path
        String path = "../th/" + s.mFilename;
        // Log.v("ThManager", "config write add survey " + path );
        pw.format("    input %s\n", path );
      }
      for ( ThEquate equate : mEquates ) {
        pw.format("    equate");
        for ( String st : equate.mStations ) pw.format(" %s", st );
        pw.format("\n");
      }
      pw.format("  endsurvey\n");
      pw.format("endsource\n");
      fw.flush();
      fw.close();
    } catch ( IOException e ) { 
      Log.v("ThManager", "write file " + mFilepath + " I/O error " + e );
    }
  }

  String exportSurvex( boolean overwrite )
  {
    String filepath = mFilepath.replace(".thconfig", ".svx").replace("/thconfig/", "/svx/");
    File file = new File( filepath );
    if ( file.exists() ) {
      if ( ! overwrite ) return null;
    } else {
      File dir = file.getParentFile();
      if ( dir != null ) dir.mkdirs();
    }
    writeSurvex( filepath );
    return filepath;
  }

  private String toSvxStation( String st )
  {
    int pos = st.indexOf('@');
    return st.substring(pos+1) + "." + st.substring(0,pos);
  }

  void writeSurvex( String filepath )
  {
    try {
      FileWriter fw = new FileWriter( filepath );
      PrintWriter pw = new PrintWriter( fw );
      pw.format("; created by ThManager %s - %s\n", ThManagerApp.VERSION, currentDate() );
      // TODO EXPORT
      for ( ThInput s : mInputs ) {
        String path = "../svx/" + s.mFilename.replace(".th", ".svx");
        pw.format("*include %s\n", path );
      }
      for ( ThEquate equate : mEquates ) {
        pw.format("*equate");
        for ( String st : equate.mStations ) pw.format(" %s", toSvxStation( st ) );
        pw.format("\n");
      }

      fw.flush();
      fw.close();
    } catch ( IOException e ) { 
      Log.v("ThManager", "write file " + mFilepath + " I/O error " + e );
    }
  }

  void loadFile()
  {
    // Log.v("ThManager", "load file path " + mFilepath );
    mSurvey = new ThSurvey( "." );
    new ThParser( mFilepath, mSurvey, new ThUnits() );
  }

  private void readFile( )
  {
    // if the file does not exists creates it and write an empty thconfig file
    // Log.v("ThManager", "read file path " + mFilepath );
    File file = new File( mFilepath );
    if ( ! file.exists() ) {
      // Log.v("ThManager", "file does not exist");
      writeThConfig( true );
      return;
    }

    try {
      FileReader fr = new FileReader( file );
      BufferedReader br = new BufferedReader( fr );
      String line = br.readLine();
      int cnt = 1;
      // Log.v( ThManagerApp.TAG, cnt + ":" + line );
      while ( line != null ) {
        line = line.trim();
        int pos = line.indexOf( '#' );
        if ( pos >= 0 ) line = line.substring( 0, pos );
        if ( line.length() > 0 ) {
          String[] vals = line.split( " " );
          if ( vals.length > 0 ) {
            if ( vals[0].equals( "source" ) ) {
            } else if ( vals[0].equals( "survey" ) ) {
              for (int k=1; k<vals.length; ++k ) {
                if ( vals[k].length() > 0 ) {
                  mSurveyName = vals[k];
                  break;
                }
              }
            } else if ( vals[0].equals( "input" ) ) {
              for (int k=1; k<vals.length; ++k ) {
                if ( vals[k].length() > 0 ) {
                  String filename = vals[k];
                  int idx = filename.lastIndexOf('/');
                  if ( idx >= 0 ) { filename = filename.substring(idx+1); }
                  String filepath = ThManagerPath.getThPath( filename );
                  // String filepath = mParentDir + filename;
                  filepath = (new File(filepath)).getAbsolutePath();
                  // addInput( vals[k], filepath );
                  addInput( filename, filepath );
                  break;
                }
              }    
            } else if ( vals[0].equals( "equate" ) ) {
              ThEquate equate = new ThEquate();
              for (int k=1; k<vals.length; ++k ) {
                if ( vals[k].length() > 0 ) {
                  equate.addStation( vals[k] );
                }
              }
              mEquates.add( equate );
            }
          }
        }
        line = br.readLine();
        ++ cnt;
      }
      fr.close();
    } catch ( IOException e ) {
      // TODO
      Log.e( ThManagerApp.TAG, "exception " + e.toString() );
    }
    // Log.v( "ThManager", "ThConfig read file: nr. sources " + mInputs.size() );
  }

}

/** @file ThParser.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief  therion file parser and model
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 */
package com.topodroid.ThManager;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

/** parse a therion file
 */
public class ThParser 
{
  private static final String TAG = "ThManager";

  static float FEET_PER_METER = 3.2808399f;

  int cnt; // line counter

 
  /** get the next index of a non-empty string in the array
   * @param vals   array of strings
   * @param idx    current index (start from the next one)
   */
  static int nextIndex( String[] vals, int idx )
  {
    ++idx;
    while ( idx < vals.length && vals[idx].length() == 0 ) ++idx;
    return idx;
  }

  String nextLine( BufferedReader br )
  {
    String line = null;
    try {
      while ( (line = br.readLine() ) != null ) {
        ++ cnt;
        line = line.trim();
        int pos = line.indexOf( '#' );
        if ( pos >= 0 ) line = line.substring( 0, pos );
        if ( line.length() > 0 ) return line;
      } 
    } catch ( IOException e ) { 
    }
    return null;
  }

  /** parse the string array for a "fix"
   * @param survey    current survey
   * @param vals      string array
   * @param idx       index in the string array
   */
  private void parseFix( ThSurvey survey, String[] vals, int idx ) 
  {
    if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
      String name = vals[idx];
      try { 
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
          float x = Float.parseFloat( vals[idx] );
          if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
            float y = Float.parseFloat( vals[idx] );
            if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
              float z = Float.parseFloat( vals[idx] );
              survey.addFix( new ThFix( name, x, y, z ) );
            }
          }
        }
      } catch ( NumberFormatException e ) {
        Log.e("Th", "fix station number format exception");
      }
    }
  }
   
  /** parse the string array for "declination"
   * @param units    current units
   * @param vals      string array
   * @param idx       index in the string array
   */
  private void parseDeclination( ThUnits units, String[] vals, int idx )
  {
    if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
      try {
        units.mDecl = Float.parseFloat( vals[idx] );
      } catch ( NumberFormatException e ) { }
    }
  }

  /**
   * @param filename   full pathname of the file to parse
   * @param survey     current survey
   */
  ThParser( String filename, ThSurvey survey, ThUnits units ) 
  {
    cnt = 0;
    // Log.v("ThManager", "parser file " + filename + " survey " + survey.mName  );
    String dirname = (new File( filename )).getParentFile().getName() + "/";
    try {
      FileReader fr = new FileReader( filename );

      BufferedReader br = new BufferedReader( fr );
      parseFile( dirname, br, survey, units );
    } catch ( IOException e ) { }
  }

  private void parseFile( String dirname, BufferedReader br, ThSurvey survey, ThUnits units )
  {
    String line = null;
    while ( ( line = nextLine( br ) ) != null ) {
      String[] vals = line.split(" ");
      int idx = nextIndex( vals, -1 );
      String cmd = vals[idx];
      if ( cmd.equals("source") ) {  // source: if followed by a filename read it
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
          String filename = vals[idx];
          if ( ! filename.startsWith("/") ) filename = dirname + filename;
          new ThParser( filename, survey, units );
        } 
      } else if ( cmd.equals("survey") ) { // swallow up to "endsurvey" 
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
	  ThSurvey survey1 = new ThSurvey( vals[idx] + "." + survey.mName );
          // Log.v( "ThManager", "add survey " + survey1.mName );
	  survey.addSurvey( survey1 );
	  parseSurvey( dirname, br, survey1, units );
	}
      } else {
        // everything else is ignored
      }
    }
  }
          
        

  private void parseSurvey( String dirname, BufferedReader br, ThSurvey survey, ThUnits units )
  {
    ThUnits cur_units = new ThUnits( units );

    String line = null;
    while ( ( line = nextLine( br ) ) != null ) {
      String[] vals = line.split(" ");
      int idx = nextIndex( vals, -1 );
      String cmd = vals[idx];
      if ( cmd.equals("endsurvey") ) { // "endsurvey" finishes the survey-parsing
        return;
      } else if ( cmd.equals("survey") ) {  // start a child-survey
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
	  ThSurvey survey1 = new ThSurvey( vals[idx] );
	  survey.addSurvey( survey1 );
	  parseSurvey( dirname, br, survey1, cur_units );
	}
      } else if ( cmd.equals("declination") ) { 
        parseDeclination( cur_units, vals, idx );
      } else if ( cmd.equals("input") ) {
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
          String filename = vals[idx];
	  if ( ! filename.startsWith("/" ) ) filename = dirname + filename;
	  new ThParser( filename, survey, cur_units );
	}
      } else if ( cmd.equals("equate") ) {
        ThEquate equate = new ThEquate();
        while ( ( idx = nextIndex( vals, idx ) ) < vals.length ) equate.addStation( vals[idx] );
        if ( equate.size() > 1 ) survey.addEquate( equate );
      } else if ( cmd.equals("centerline") || cmd.equals("centreline") ) { // swallow "centerline"
        readCenterline( br, survey, cur_units );
      } else if ( cmd.equals("map") ) { // FIXME map must end in the same file
        while ( ( line = nextLine( br ) ) != null && ! line.startsWith("endmap") ) /* nothing*/ ;
      } else if ( cmd.equals("surface") ) { 
        while ( ( line = nextLine( br ) ) != null && ! line.startsWith("endsurface") ) /* nothing*/ ;
      }
    }
  }

  private void readCenterline( BufferedReader br, ThSurvey survey, ThUnits units )
  {
    int extend = 1;
    ThUnits cur_units = new ThUnits( units );
    String line;
    while ( ( line = nextLine(br) ) != null ) {
      String[] vals = line.split(" ");
      int idx = -1;
      idx = nextIndex( vals, idx );
      String cmd = vals[idx];
      if ( cmd.equals("endcenterline") || cmd.equals("endcentreline") ) {
        return;
      } else if ( cmd.equals("extend") ) {
        if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
          if ( vals[idx].equals("right") || vals[idx].equals("normal") ) {  // right normal
            extend = 1;
          } else if ( vals[idx].equals("left") || vals[idx].equals("reverse") ) { // left reverse
            extend = -1;
          } else if ( vals[idx].equals("ignore") ) { // ignore
            extend = 2;
          } else if ( vals[idx].startsWith("vert") ) { // vertical
            extend = 0;
          } else {  // other values "break" "start"
            // ignore
          }
        }
      } else if ( cmd.equals("declination") ) { 
        parseDeclination( cur_units, vals, idx );
      } else if ( cmd.equals("flags")  // commands that are ignored
               || cmd.equals("date") 
               || cmd.equals("team") ) {
        // skip
       } else if ( cmd.equals("data") ) {
         // data normal from to length compass clino ...
         // TODO
       } else if ( cmd.equals("units") ) {
         float value = 1;
         boolean b_length  = false;
         boolean b_bearing = false;
         boolean b_clino   = false;
         if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
           if ( vals[idx].equals("length") ) {
             b_length = true;
           } else if ( vals[idx].equals("azimuth") ) {
             b_bearing = true;
           } else if ( vals[idx].equals("clino") ) {
             b_clino = true;
           } else if ( vals[idx].equals("cm") ) {
             value *= 100;
           } else if ( vals[idx].equals("ft") || vals[idx].equals("feet") ) {
             value *= 1/FEET_PER_METER;
           } else if ( vals[idx].startsWith("deg") ) {
           } else if ( vals[idx].startsWith("grad") ) {
             value *= 0.9f; // 360/400
           } else if ( vals[idx].equals("left") || vals[idx].equals("right") 
                    || vals[idx].equals("up") || vals[idx].equals("down") ) {
             // skip
           } else {
             try {
               value = Float.parseFloat( vals[idx] );
             } catch ( NumberFormatException e ) { }
           }
         } else {
           if ( b_length )  cur_units.mLength  = value;
           if ( b_bearing ) cur_units.mBearing = value;
           if ( b_clino )   cur_units.mClino   = value;
         }  
       } else if ( cmd.equals("fix") ) { 
         parseFix( survey, vals, idx );
       } else if ( vals.length >= 5 ) { // data line
         String from = vals[idx];
         if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
           String to = vals[idx]; 
           if ( ! to.equals("-") && ! to.equals(".") ) { // skip splays
             try {
               if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
                 float len  = Float.parseFloat( vals[idx] ) * cur_units.mLength;
                 if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
                   float ber  = Float.parseFloat( vals[idx] ) * cur_units.mBearing + cur_units.mDecl;
                   if ( ( idx = nextIndex( vals, idx ) ) < vals.length ) {
                     float cln  = Float.parseFloat( vals[idx] ) * cur_units.mClino;
		     if ( extend >= -1 && extend <= 1 ) {
                       survey.addShot( new ThShot( from, to, len, ber, cln, extend, survey ) );
		     }
                   }
                 }
               }
             } catch ( NumberFormatException e ) {
               Log.e("Th", "shot data number format exception");
             }
           }
         }
       }
     } 
   }

}

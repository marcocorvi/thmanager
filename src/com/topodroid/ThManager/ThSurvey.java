/** @file ThSurvey.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief Th survey
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 */
package com.topodroid.ThManager;

import java.util.ArrayList;

import java.io.BufferedReader;

import android.util.FloatMath;
import android.util.Log;

public class ThSurvey
{
  private static final String TAG = "ThManager";

  String mName; // survey name
  ThSurvey mParent;
  ThStation mStartStation;

  ArrayList< ThShot >    mShots;
  ArrayList< ThStation > mStations;
  ArrayList< ThSurvey >  mSurveys;  // child surveys
  ArrayList< ThEquate >  mEquates;
  ArrayList< ThFix >     mFixes;

  ThSurvey( String name )
  {
    mName   = name;
    mParent = null;
    mShots    = new ArrayList< ThShot >();
    mStations = null;
    mSurveys  = new ArrayList< ThSurvey >();
    mEquates  = new ArrayList< ThEquate >();
    mFixes    = new ArrayList< ThFix >();
  }

  ThSurvey( String name, ThSurvey parent )
  {
    mName   = name;
    mParent = parent;
    mShots    = new ArrayList< ThShot >();
    mStations = null;
    mSurveys  = new ArrayList< ThSurvey >();
    mEquates  = new ArrayList< ThEquate >();
    mFixes    = new ArrayList< ThFix >();
  }

  ThSurvey getSurvey( String[] ns, int pos )
  {
    String name = ns[pos];
    for ( ThSurvey s : mSurveys ) {
      if ( name.equals( s.mName ) ) {
        if ( pos == 0 ) return s;
        return s.getSurvey( ns, pos-1 );
      }
    }
    return null;
  }

  void addFix( ThFix fix ) { mFixes.add( fix ); }

  void addEquate( ThEquate equate ) { mEquates.add( equate ); }

  void addSurvey( ThSurvey survey )
  {
    mSurveys.add( survey );
    survey.mParent = this;
  }

  String getName() { return mName; }

  String getFullName() 
  { 
    if ( mParent != null ) {
      return mName + "." + mParent.getFullName();
    }
    return mName;
  }

  /** data reduction
   * data reduction consumes the equates that are resolved inside the survey stations
   * without considering the child surveys
   */
  void reduce()
  {
    computeStations();
    for ( ThSurvey s : mSurveys ) s.reduce();
  }

  void addShot( ThShot shot ) { mShots.add( shot ); }

  void addShot( String from, String to, float d, float b, float c, int e )
  {
    mShots.add( new ThShot( from, to, d, b, c, e, this ) );
  }

  /** get a station by the the name
   * @param name    station name
   */
  ThStation getStation( String name )
  {
    for ( ThStation st : mStations ) {
      if ( st.mName.equals( name ) ) return st;
    }
    return null;
  }

  // ---------------------------------------------------------------

  private void computeStations()
  {
    mStations = new ArrayList< ThStation >();
    mStartStation = null;
    if ( mShots.size() == 0 ) return;

    // reset shots stations
    for ( ThShot sh : mShots ) sh.setStations( null, null );

    ThStation fs=null, ts=null;
    boolean repeat = true;
    while ( repeat ) {
      repeat = false;
      for ( ThShot sh : mShots ) {
        if ( sh.mFromStation != null ) continue; // shot already got stations
        if ( mStartStation == null ) {
          fs = new ThStation( sh.mFrom, 0, 0, 0, 0, this );
	  mStartStation = fs;
          mStations.add( mStartStation );
          // angles are already in radians
          float h = (float)Math.cos( sh.mClino ) * sh.mLength;
          float v = (float)Math.sin( sh.mClino ) * sh.mLength;
          float e =   h * (float)Math.sin( sh.mBearing );
          float s = - h * (float)Math.cos( sh.mBearing );
          ts = new ThStation( sh.mTo, e, s, h*sh.mExtend, v, this );
          mStations.add( ts );
          sh.setStations( fs, ts );
          repeat = true;
        } else {
          fs = getStation( sh.mFrom );
          ts = getStation( sh.mTo );
          if ( fs != null ) {
            if ( ts == null ) {  // FROM --> TO 
              float h = (float)Math.cos( sh.mClino ) * sh.mLength;
              float v = (float)Math.sin( sh.mClino ) * sh.mLength;
              float e =   h * (float)Math.sin( sh.mBearing );
              float s = - h * (float)Math.cos( sh.mBearing );
              ts = new ThStation( sh.mTo, fs.e+e, fs.s+s, fs.h+h*sh.mExtend, fs.v+v, this );
              mStations.add( ts );
              repeat = true;
            } else {
	      // skip: both shot stations exist
	    }
            sh.setStations( fs, ts );
          } else if ( ts != null ) {
            float h = (float)Math.cos( sh.mClino ) * sh.mLength;
            float v = (float)Math.sin( sh.mClino ) * sh.mLength;
            float e =   h * (float)Math.sin( sh.mBearing );
            float s = - h * (float)Math.cos( sh.mBearing );
            fs = new ThStation( sh.mFrom, ts.e-e, ts.s-s, ts.h-h*sh.mExtend, ts.v-v, this );
            mStations.add( fs );
            sh.setStations( fs, ts );
            repeat = true;
          } else { 
	    // the two shot stations do not exist: check equates
	    boolean skip_equate = false;
	    for ( ThEquate eq : mEquates ) {
	      if ( skip_equate ) break;
	      if ( eq.contains( sh.mFrom ) ) {
		for ( String st : eq.mStations ) if ( ! st.equals( sh.mFrom  ) ) {
		  if ( ( fs = getStation( st ) ) != null ) {
                    float h = (float)Math.cos( sh.mClino ) * sh.mLength;
                    float v = (float)Math.sin( sh.mClino ) * sh.mLength;
                    float e =   h * (float)Math.sin( sh.mBearing );
                    float s = - h * (float)Math.cos( sh.mBearing );
                    ts = new ThStation( sh.mTo, fs.e+e, fs.s+s, fs.h+h*sh.mExtend, fs.v+v, this );
                    mStations.add( ts );
                    sh.setStations( fs, ts );
		    skip_equate = true;
		    break;
		  }
		}
              } else if ( eq.contains( sh.mTo ) ) {
	        for ( String st : eq.mStations ) if ( ! st.equals( sh.mTo ) ) {
		  if ( ( ts = getStation( st ) ) != null ) {
                    float h = (float)Math.cos( sh.mClino ) * sh.mLength;
                    float v = (float)Math.sin( sh.mClino ) * sh.mLength;
                    float e =   h * (float)Math.sin( sh.mBearing );
                    float s = - h * (float)Math.cos( sh.mBearing );
                    fs = new ThStation( sh.mFrom, ts.e-e, ts.s-s, ts.h-h*sh.mExtend, ts.v-v, this );
                    mStations.add( fs );
                    sh.setStations( fs, ts );
		    skip_equate = true;
		    break;
                  }
                }
              }
            }
	    if ( skip_equate ) repeat = true;
	  }
        }
      }
    }
  }

}



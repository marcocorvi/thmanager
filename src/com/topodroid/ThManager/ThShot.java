/** @file ThShot.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief Th shot
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 */
package com.topodroid.ThManager;

import android.util.Log;

public class ThShot
{
  private static final float DEG2RAD = (float)(Math.PI/180);

  String mFrom;
  String mTo;
  ThStation mFromStation;
  ThStation mToStation; 
  float mLength, mBearing, mClino;  // radians
  int mExtend;
  ThSurvey mSurvey;  // survey this shot belongs to

  public ThShot( String f, String t, float l, float b, float c, int e, ThSurvey survey )
  {
    mFrom = f;
    mTo   = t;
    mLength  = l;
    mBearing = b * DEG2RAD;
    mClino   = c * DEG2RAD;
    mExtend  = e;
    mFromStation = null;
    mToStation   = null;
    mSurvey = survey;
  }

  public ThShot( float l, float b, float c, int e, ThSurvey survey )
  {
    mFrom = null;
    mTo   = null;
    mLength  = l;
    mBearing = b * DEG2RAD;
    mClino   = c * DEG2RAD;
    mExtend  = e;
    mFromStation = null;
    mToStation   = null;
    mSurvey = survey;
  }

  void setStations( ThStation fs, ThStation ts )
  {
    mFromStation = fs;
    mToStation   = ts;
  }

}


package com.topodroid.ThManager;

class ThUnits
{
  float mDecl;    // declination
  float mLength;
  float mBearing;
  float mClino;

  ThUnits()
  {
    mDecl    = 0;
    mLength  = 1;
    mBearing = 1;
    mClino   = 1;
  }

  ThUnits( float l, float b, float c )
  {
    mDecl    = 0;
    mLength  = l;
    mBearing = b;
    mClino   = c;
  }

  ThUnits( float d, float l, float b, float c )
  {
    mDecl    = d;
    mLength  = l;
    mBearing = b;
    mClino   = c;
  }

  ThUnits( ThUnits units )
  {
    mDecl    = units.mDecl;
    mLength  = units.mLength;
    mBearing = units.mBearing;
    mClino   = units.mClino;
  }

}

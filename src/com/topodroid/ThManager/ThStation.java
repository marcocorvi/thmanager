package com.topodroid.ThManager;

class ThStation
{
  String mName;
  float e, s, h, v;
  ThSurvey mSurvey; // survey this station belongs to

  ThStation( String name, float e0, float s0, float h0, float v0, ThSurvey survey )
  {
    mName = name;
    e = e0;
    s = s0;
    h = h0;
    v = v0;
    mSurvey = survey;
  }

  String getFullName() 
  {
    if ( mName.indexOf('@') > 0 ) {
      return mName + '.' + mSurvey.getFullName();
    }
    return mName + '@' + mSurvey.getFullName();
  }

  String getName()
  {
    return mName; 
  }

}

/** @file ThFix.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief Th fixed station
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 */
package com.topodroid.ThManager;

import android.util.Log;

public class ThFix
{
  private static final String TAG = "ThManager";

  /** fix station:
   * fix stations are supposed to be referred to the same coord system
   */
  // private CS cs;
  String mName;
  double e, n, z; // north east, vertical (upwards)

  public ThFix( String nm, double e0, double n0, double z0 )
  {
    mName = nm;
    e = e0;
    n = n0;
    z = z0;
  }

}


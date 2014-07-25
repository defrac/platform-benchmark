// The ray tracer code in this file is written by Adam Burmister. It
// is available in its original form from:
//
//   http://labs.flog.co.nz/raytracer/
//
// Ported from the v8 benchmark suite by Google 2012.
//
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark.tracer;

import javax.annotation.Nonnull;

final class IntersectionInfo {
  @Nonnull
  private static final Color COLOR = new Color(0.0, 0.0, 0.0);

  boolean isHit = false;
  int hitCount = 0;
  BaseShape shape;
  Vector position;
  Vector normal;
  Color color;
  double distance;

  IntersectionInfo() {
    color = COLOR;
  }

  @Nonnull
  @Override
  public String toString() {
    return "Intersection ["+position+"]";
  }
}

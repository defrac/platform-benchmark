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

abstract class BaseShape {
  @Nonnull
  final Vector position;

  @Nonnull
  final Materials material;

  BaseShape(@Nonnull final Vector position, @Nonnull final Materials material) {
    this.position = position;
    this.material = material;
  }

  abstract IntersectionInfo intersect(Ray ray);

  @Override
  public String toString() {
    return "BaseShape";
  }
}

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

final class Light {
  @Nonnull
  final Vector position;

  @Nonnull
  final Color color;

  private final double intensity;

  Light(@Nonnull final Vector position, @Nonnull final Color color) {
    this(position, color, 10.0);
  }

  Light(@Nonnull final Vector position, @Nonnull final Color color, final double intensity) {
    this.position = position;
    this.color = color;
    this.intensity = intensity;
  }

}

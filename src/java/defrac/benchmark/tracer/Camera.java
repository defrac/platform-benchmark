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

final class Camera {
  @Nonnull
  final Vector position;
  @Nonnull
  private final Vector lookAt;
  @Nonnull
  private final Vector up;
  private final Vector equator;
  private final Vector screen;

  Camera(@Nonnull final Vector position, @Nonnull final Vector lookAt, @Nonnull final Vector up) {
    this.position = position;
    this.lookAt = lookAt;
    this.up = up;
    equator = lookAt.normalize().cross(up);
    screen = position.add(lookAt);
  }

  @Nonnull
  Ray getRay(double vx, double vy) {
    final Vector pos = screen.sub(equator.multiplyScalar(vx).sub(up.multiplyScalar(vy))).negateY();
    final Vector dir = pos.sub(position);
    return new Ray(pos, dir.normalize());
  }

  @Nonnull
  @Override
  public String toString() {
    return "Camera []";
  }
}

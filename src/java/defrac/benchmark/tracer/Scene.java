// The ray tracer code in this file is written by Adam Burmister. It
// is available in its original form from:
//
//   http://labs.flog.co.nz/raytracer/
//
// Ported from the v8 benchmark suite by Google 2012.
//
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark.tracer;

import defrac.lang.Lists;

import javax.annotation.Nonnull;
import java.util.List;

final class Scene {
  @Nonnull
  private static final Vector POSITION = new Vector(0.0, 0.0, -0.5);
  @Nonnull
  private static final Vector LOOK_AT = new Vector(0.0, 0.0, 1.0);
  @Nonnull
  private static final Vector UP = new Vector(0.0, 1.0, 0.0);
  @Nonnull
  private static final Color COLOR = new Color(0.0, 0.0, 0.5);
  @Nonnull
  private static final Background BACKGROUND = new Background(COLOR, 0.2);

  @Nonnull
  Camera camera;
  @Nonnull
  final List<BaseShape> shapes;
  @Nonnull
  final List<Light> lights;
  @Nonnull
  Background background;
  Scene() {
    camera = new Camera(POSITION, LOOK_AT, UP);
    shapes = Lists.newArrayList();
    lights = Lists.newArrayList();
    background = BACKGROUND;
  }
}

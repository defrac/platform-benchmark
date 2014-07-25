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
import javax.annotation.Nullable;
import java.util.Random;

public final class RenderScene {
  @Nonnull private static final Vector POSITION = new Vector(0.0, 0.0, -15.0);
  @Nonnull private static final Vector LOOK_AT = new Vector(-0.2, 0.0, 5.0);
  @Nonnull private static final Vector UP = new Vector(0.0, 1.0, 0.0);
  @Nonnull private static final Color COLOR = new Color(0.5, 0.5, 0.5);
  @Nonnull private static final Background BACKGROUND = new Background(COLOR, 0.4);
  @Nonnull private static final Vector SPHERE_POS = new Vector(-1.5, 1.5, 2.0);
  @Nonnull private static final Color SPHERE_COLOR = new Color(0.0, 0.5, 0.5);
  @Nonnull private static final Materials.Solid SPHERE_MATERIAL = new Materials.Solid(SPHERE_COLOR, 0.3, 0.0, 0.0, 2.0);
  @Nonnull private static final Sphere SPHERE = new Sphere(SPHERE_POS, 1.5, SPHERE_MATERIAL);
  @Nonnull private static final Color COLOR_WHITE = new Color(1.0, 1.0, 1.0);
  @Nonnull private static final Color COLOR_BLACK = new Color(0.0, 0.0, 0.0);
  @Nonnull private static final Materials.Chessboard PLANE_MATERIAL = new Materials.Chessboard(
      COLOR_WHITE, COLOR_BLACK, 0.2, 0.0, 1.0, 0.7);
  @Nonnull private static final Color LIGHT_COLOR = new Color(0.8, 0.8, 0.8);
  @Nonnull private static final Vector LIGHT_POS = new Vector(5.0, 10.0, -1.0);
  @Nonnull private static final Light LIGHT = new Light(LIGHT_POS, LIGHT_COLOR);
  @Nonnull private static final Vector LIGHT1_POS = new Vector(-3.0, 5.0, -15.0);
  @Nonnull private static final Light LIGHT1 = new Light(LIGHT1_POS, LIGHT_COLOR, 100.0);

  public static void apply(@Nullable final Object event) {
    final Scene scene = new Scene();
    scene.camera = new Camera(POSITION, LOOK_AT, UP);
    scene.background = BACKGROUND;

    Sphere sphere = SPHERE;
    Sphere sphere1 = new Sphere(
        new Vector(1.0, 0.25, 1.0),
        0.5,
        new Materials.Solid(
            new Color(0.9, 0.9, 0.9),
            0.1,
            0.0,
            0.0,
            1.5
        )
    );

    Plane plane = new Plane(
        new Vector(0.1, 0.9, -0.5).normalize(),
        1.2,
        PLANE_MATERIAL
    );

    scene.shapes.add(plane);
    scene.shapes.add(sphere);
    scene.shapes.add(sphere1);

    Light light = LIGHT;
    Light light1 = LIGHT1;

    scene.lights.add(light);
    scene.lights.add(light1);

    int imageWidth, imageHeight, pixelSize;
    boolean renderDiffuse, renderShadows, renderHighlights, renderReflections;
    Canvas canvas;
    if (event == null) {
      imageWidth = 100;
      imageHeight = 100;
      pixelSize = 5;
      renderDiffuse = true;
      renderShadows = true;
      renderHighlights = true;
      renderReflections = true;
      canvas = null;
    } else {
      // not supported
      canvas = new Canvas();
      final Random r = new Random();
      imageWidth = r.nextInt();
      imageHeight = r.nextInt();
      pixelSize = r.nextInt();
      renderDiffuse = r.nextBoolean();
      renderShadows = r.nextBoolean();
      renderHighlights = r.nextBoolean();
      renderReflections = r.nextBoolean();
    }

    int rayDepth = 2;

    Engine raytracer = new Engine(imageWidth, imageHeight,
        pixelSize, pixelSize, renderDiffuse, renderShadows, renderHighlights, renderReflections,
        rayDepth);

    raytracer.renderScene(scene, canvas);
  }
}

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

final class Engine {
  private final int canvasWidth;
  private final int canvasHeight;
  private final int pixelWidth;
  private final int pixelHeight;
  private final boolean renderDiffuse;
  private final boolean renderShadows;
  private final boolean renderHighlights;
  private final boolean renderReflections;
  private final int rayDepth;
  private Canvas canvas;

  private int checkNumber;

  Engine(final int canvasWidth, final int canvasHeight,
         final int pixelWidth, final int pixelHeight,
         final boolean renderDiffuse, final boolean renderShadows,
         final boolean renderHighlights, final boolean renderReflections,
         final int rayDepth) {
    this.canvasWidth = canvasWidth / pixelWidth;
    this.canvasHeight = canvasHeight / pixelHeight;
    this.pixelWidth = pixelWidth;
    this.pixelHeight = pixelHeight;
    this.renderDiffuse = renderDiffuse;
    this.renderShadows = renderShadows;
    this.renderHighlights = renderHighlights;
    this.renderReflections = renderReflections;
    this.rayDepth = rayDepth;
  }

  Engine() {
    this(100, 100, 2, 2, false, false, false, false, 2);
  }

  void setPixel(int x, int y, Color color){
    int pxW, pxH;
    pxW = pixelWidth;
    pxH = pixelHeight;

    if (canvas != null) {
      canvas.fillStyle = color.toString();
      canvas.fillRect(x * pxW, y * pxH, pxW, pxH);
    } else {
      checkNumber += color.brightness();
    }
  }

  // 'canvas' can be null if raytracer runs as benchmark
  void renderScene(Scene scene, Canvas canvas) {
    checkNumber = 0;
    /* Get canvas */
    this.canvas = (canvas == null) ? null : canvas.getContext("2d");

    int canvasHeight = this.canvasHeight;
    int canvasWidth = this.canvasWidth;

    for(int y = 0; y < canvasHeight; y++){
      for(int x = 0; x < canvasWidth; x++){
        double yp = (double)y / (double)canvasHeight * 2.0 - 1.0;
        double xp = (double)x / (double)canvasWidth * 2.0 - 1.0;

        Ray ray = scene.camera.getRay(xp, yp);
        setPixel(x, y, getPixelColor(ray, scene));
      }
    }
    if ((canvas == null) && (checkNumber != 55545)) {
      // Used for benchmarking.
      throw new RuntimeException("Scene rendered incorrectly - expected <55545>, but was <"+checkNumber+">");
    }
  }

  Color getPixelColor(Ray ray, Scene scene){
    IntersectionInfo info = testIntersection(ray, scene, null);
    if(info.isHit){
      return rayTrace(info, ray, scene, 0);
    }
    return scene.background.color;
  }

  IntersectionInfo testIntersection(Ray ray, Scene scene, BaseShape exclude) {
    int hits = 0;
    IntersectionInfo best = new IntersectionInfo();
    best.distance = 2000.0;

    final int n = scene.shapes.size();
    for(int  i=0; i < n; i++){
      BaseShape shape = scene.shapes.get(i);

      if(shape != exclude){
        IntersectionInfo info = shape.intersect(ray);
        if (info.isHit &&
            (info.distance >= 0) &&
            (info.distance < best.distance)){
          best = info;
          hits++;
        }
      }
    }
    best.hitCount = hits;
    return best;
  }

  Ray getReflectionRay(Vector P, Vector N, Vector V){
    double c1 = -N.dot(V);
    Vector R1 = N.multiplyScalar(2*c1).add(V);
    return new Ray(P, R1);
  }

  Color rayTrace(IntersectionInfo info, Ray ray, Scene scene, int depth) {
    // Calc ambient
    Color color = info.color.multiplyScalar(scene.background.ambience);
    Color oldColor = color;
    double shininess = Math.pow(10.0, info.shape.material.gloss + 1.0);

    final int n = scene.lights.size();
    for(int i = 0; i < n; i++) {
      final Light light = scene.lights.get(i);

      // Calc diffuse lighting
      Vector v = (light.position.sub(info.position)).normalize();

      if (renderDiffuse) {
        double L = v.dot(info.normal);
        if (L > 0.0) {
          color = color.add(info.color.multiply(light.color.multiplyScalar(L)));
        }
      }

      // The greater the depth the more accurate the colours, but
      // this is exponentially (!) expensive
      if (depth <= rayDepth) {
        // calculate reflection ray
        if (renderReflections && info.shape.material.reflection > 0.0) {
          Ray reflectionRay = getReflectionRay(info.position,
              info.normal,
              ray.direction);
          IntersectionInfo refl = testIntersection(reflectionRay, scene, info.shape);

          if (refl.isHit && refl.distance > 0.0){
            refl.color = rayTrace(refl, reflectionRay, scene, depth + 1);
          } else {
            refl.color = scene.background.color;
          }

          color = color.blend(refl.color, info.shape.material.reflection);
        }
        // Refraction
        /* TODO */
      }
      /* Render shadows and highlights */

      IntersectionInfo shadowInfo = new IntersectionInfo();

      if (renderShadows) {
        Ray shadowRay = new Ray(info.position, v);

        shadowInfo = testIntersection(shadowRay, scene, info.shape);
        if (shadowInfo.isHit &&
            shadowInfo.shape != info.shape
            /*&& shadowInfo.shape.type != 'PLANE'*/) {
          Color vA = color.multiplyScalar(0.5);
          double dB = (0.5 * Math.pow(shadowInfo.shape.material.transparency, 0.5));
          color = vA.addScalar(dB);
        }
      }
      // Phong specular highlights
      if (renderHighlights &&
          !shadowInfo.isHit &&
          (info.shape.material.gloss > 0.0)) {
        Vector Lv = (info.shape.position.sub(light.position)).normalize();

        Vector E = (scene.camera.position.sub(info.shape.position)).normalize();

        Vector H = (E.sub(Lv)).normalize();

        double glossWeight = Math.pow(Math.max(info.normal.dot(H), 0.0), shininess);
        color = light.color.multiplyScalar(glossWeight).add(color);
      }
    }
    return color.limit();
  }

  @Override
  @Nonnull
  public String toString() {
    return "Engine [canvasWidth: "+canvasWidth+", canvasHeight: "+canvasHeight+"]";
  }
}

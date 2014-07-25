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

public final class Sphere extends BaseShape {
  private final double radius;

  Sphere(@Nonnull final Vector pos, final double radius, @Nonnull final Materials material) {
    super(pos, material);
    this.radius = radius;
  }

  IntersectionInfo intersect(Ray ray){
    IntersectionInfo info = new IntersectionInfo();
    info.shape = this;

    Vector dst = ray.position.sub(position);

    double B = dst.dot(ray.direction);
    double C = dst.dot(dst) - (radius * radius);
    double D = (B * B) - C;

    if (D > 0) { // intersection!
      info.isHit = true;
      info.distance = (-B) - Math.sqrt(D);
      info.position = ray.position.add(ray.direction.multiplyScalar(info.distance));
      info.normal = (info.position.sub(position)).normalize();

      info.color = material.getColor(0,0);
    } else {
      info.isHit = false;
    }
    return info;
  }

  @Nonnull
  @Override
  public String toString() {
    return "Sphere [position="+position+", radius="+radius+"]";
  }
}

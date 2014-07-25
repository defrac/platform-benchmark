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

public final class Plane extends BaseShape {
  private final double d;

  Plane(@Nonnull final Vector pos, final double d, @Nonnull final Materials material) {
    super(pos, material);
    this.d = d;
  }

  IntersectionInfo intersect(Ray ray) {
    IntersectionInfo info = new IntersectionInfo();

    double Vd = position.dot(ray.direction);
    if (Vd == 0) return info; // no intersection

    double t = -(position.dot(ray.position) + d) / Vd;
    if (t <= 0) return info;

    info.shape = this;
    info.isHit = true;
    info.position = ray.position.add(ray.direction.multiplyScalar(t));
    info.normal = position;
    info.distance = t;

    if(material.hasTexture){
      Vector vU = new Vector(position.y, position.z, -position.x);
      Vector vV = vU.cross(position);
      double u = info.position.dot(vU);
      double v = info.position.dot(vV);
      info.color = material.getColor(u,v);
    } else {
      info.color = material.getColor(0,0);
    }

    return info;
  }

  @Override
  @Nonnull
  public String toString() {
    return "Plane ["+position+", d="+d+"]";
  }
}

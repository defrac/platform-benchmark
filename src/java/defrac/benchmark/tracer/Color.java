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

final class Color {
  private final double red;
  private final double green;
  private final double blue;

  public Color(final double red, final double green, final double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  @Nonnull
  Color limit() {
    double r = (red > 0.0) ? ((red > 1.0) ? 1.0 : red) : 0.0;
    double g = (green > 0.0) ? ((green > 1.0) ? 1.0 : green) : 0.0;
    double b = (blue > 0.0) ? ((blue > 1.0) ? 1.0 : blue) : 0.0;
    return new Color(r, g, b);
  }

  @Nonnull
  Color add(@Nonnull final Color c2) {
    return new Color(red + c2.red, green + c2.green, blue + c2.blue);
  }

  @Nonnull
  Color addScalar(double s){
    final Color result = new Color(red + s, green + s, blue + s);
    result.limit();
    return result;
  }

  @Nonnull
  Color multiply(Color c2) {
    return new Color(red * c2.red, green * c2.green, blue * c2.blue);
  }

  @Nonnull
  Color multiplyScalar(double f) {
    return new Color(red * f, green * f, blue * f);
  }

  @Nonnull
  Color blend(Color c2, double w) {
    return multiplyScalar(1.0 - w).add(c2.multiplyScalar(w));
  }

  int brightness() {
    int r = (int)(red * 255);
    int g = (int)(green * 255);
    int b = (int)(blue * 255);
    return (r * 77 + g * 150 + b * 29) >> 8;
  }

  @Override
  @Nonnull
  public String toString() {
    int r = (int)(red * 255);
    int g = (int)(green * 255);
    int b = (int)(blue * 255);

    return "rgb("+r+","+g+","+b+")";
  }
}

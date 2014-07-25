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

abstract class Materials {
  final double gloss;             // [0...infinity] 0 = matt
  final double transparency;      // 0=opaque
  final double reflection;        // [0...infinity] 0 = no reflection
  private final double refraction;
  final boolean hasTexture;

  Materials(final double reflection,
            final double transparency,
            final double gloss,
            final double refraction,
            final boolean hasTexture) {
    this.reflection = reflection;
    this.transparency = transparency;
    this.gloss = gloss;
    this.refraction = refraction;
    this.hasTexture = hasTexture;
  }

  @Nonnull
  public abstract Color getColor(double u, double v);

  double wrapUp(double t) {
    t = t % 2.0;
    if (t < -1) t += 2.0;
    if (t >= 1) t -= 2.0;
    return t;
  }

  static class Chessboard extends Materials {
    final Color colorEven, colorOdd;
    final double density;

    Chessboard(@Nonnull final Color colorEven,
               @Nonnull final Color colorOdd,
               final double reflection,
               final double transparency,
               final double gloss,
               final double density) {
      super(reflection, transparency, gloss, 0.5, true);
      this.colorEven = colorEven;
      this.colorOdd = colorOdd;
      this.density = density;
    }

    @Override
    @Nonnull
    public Color getColor(final double u, final double v) {
      double t = wrapUp(u * density) * wrapUp(v * density);

      if (t < 0.0) {
        return colorEven;
      } else {
        return colorOdd;
      }
    }
  }


  static class Solid extends Materials {
    @Nonnull final Color color;

    Solid(@Nonnull final Color color, final double reflection, final double refraction, final double transparency, final double gloss) {
      super(reflection, transparency, gloss, refraction, false);
      this.color = color;
    }

    @Override
    @Nonnull
    public Color getColor(final double u, final double v) {
      return color;
    }
  }
}

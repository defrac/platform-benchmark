// The ray tracer code in this file is written by Adam Burmister. It
// is available in its original form from:
//
//   http://labs.flog.co.nz/raytracer/
//
// Ported from the v8 benchmark suite by Google 2012.
//
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark;

import defrac.benchmark.tracer.RenderScene;

public final class Tracer extends BenchmarkBase {
  public static void main(String[] args) {
    new Tracer().report();
  }

  public Tracer() {
    super("Tracer");
  }

  @Override
  public void warmup() {
    RenderScene.apply(null);
  }

  @Override
  public void exercise() {
    RenderScene.apply(null);
  }
}

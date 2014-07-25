// Copyright 2011 Google Inc. All Rights Reserved.
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark;

import javax.annotation.Nonnull;

abstract class BenchmarkBase {
  @Nonnull
  protected final String name;

  protected BenchmarkBase(@Nonnull final String name) {
    this.name = name;
  }

  public void run() {

  }

  public void warmup() {
    run();
  }

  public void exercise() {
    for(int i = 0; i < 10; ++i) {
      run();
    }
  }

  public void setup() {

  }

  public void teardown() {

  }

  public static double measureFor(@Nonnull final Runnable r, final int timeMinimum) {
    final Stopwatch watch = new Stopwatch();
    watch.start();

    int iter = 0;
    int elapsed = 0;

    while(elapsed < timeMinimum) {
      r.run();
      elapsed = watch.elapsedMilliseconds();
      iter++;
    }

    return 1000.0 * elapsed / iter;
  }

  public double measure() {
    setup();

    // Warmup for at least 100ms. Discard result.
    measureFor(new Runnable() {
      @Override
      public void run() {
        warmup();
      }
    }, 100);

    // Run the benchmark for at least 2000ms.
    final double result = measureFor(new Runnable() {
      @Override
      public void run() {
        exercise();
      }
    }, 2000);
    teardown();

    return result;
  }

  public void report() {
    final double runtime = measure();
    System.out.println(name+"(RunTime): "+runtime+" us");
  }
}

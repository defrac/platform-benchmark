/**
 * Copyright 2013 the V8 project authors. All rights reserved.
 * Copyright 2009 Oliver Hunt <http://nerget.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// Ported from the v8 benchmark suite by Google 2013.
// Translated from Dart's ton80 benchmark suite to Java
// Uses double[] for data.
package defrac.benchmark;

import defrac.benchmark.fluidMotion.Field;
import defrac.benchmark.fluidMotion.FluidField;
import defrac.lang.Procedure;

import javax.annotation.Nonnull;

public final class FluidMotion extends BenchmarkBase {
  public static void main(String[] args) {
    new FluidMotion().report();
  }

  FluidMotion() { super("FluidMotion"); }

  private static FluidField solver;
  private static int framesTillAddingPoints = 0;
  private static int framesBetweenAddingPoints = 5;

  private static void setupFluidMotion() {
    framesTillAddingPoints = 0;
    framesBetweenAddingPoints = 5;
    solver = FluidField.create(null, 128, 128, 20);
    solver.setDisplayFunction(new Procedure<Field>() {
      @Override
      public void apply(Field field) {
      }
    });
    solver.setUICallback(new Procedure<Field>() {
      @Override
      public void apply(Field field) {
        prepareFrame(field);
      }
    });
  }

  private static void runFluidMotion() {
    setupFluidMotion();
    for (int i = 0; i < 10; i++) {
      solver.update();
    }

    solver.validate(758.9012130174812, -352.56376676179076, -357.3690235879736);
  }

  private static void addPoints(@Nonnull final Field field) {
    int n = 64;
    for (int i = 1; i <= n; i++) {
      final double dn = (double)n;
      field.setVelocity(i, i, dn, dn);
      field.setDensity(i, i, 5.0);
      field.setVelocity(i, n - i, -dn, -dn);
      field.setDensity(i, n - i, 20.0);
      field.setVelocity(128 - i, n + i, -dn, -dn);
      field.setDensity(128 - i, n + i, 30.0);
    }
  }

  private static void prepareFrame(@Nonnull final Field field) {
    if (framesTillAddingPoints == 0) {
      addPoints(field);
      framesTillAddingPoints = framesBetweenAddingPoints;
      framesBetweenAddingPoints++;
    } else {
      framesTillAddingPoints--;
    }
  }

  // Overrides of BenchmarkBase.

  @Override
  public void warmup() {
    runFluidMotion();
  }

  @Override
  public void exercise() {
    runFluidMotion();
  }
}

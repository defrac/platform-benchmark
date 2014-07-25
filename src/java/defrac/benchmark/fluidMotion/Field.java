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
package defrac.benchmark.fluidMotion;

import javax.annotation.Nonnull;

public final class Field {
  @Nonnull
  private final double[] dens;
  @Nonnull
  private final double[] u;
  @Nonnull
  private final double[] v;
  private final int rowSize;

  public Field(@Nonnull final double[] dens,
               @Nonnull final double[] u,
               @Nonnull final double[] v,
               int rowSize) {
    this.dens = dens;
    this.u = u;
    this.v = v;
    this.rowSize = rowSize;
  }

  public void setDensity(final int x, final int y, final double d) {
    dens[(x + 1) + (y + 1) * rowSize] = d;
  }

  public void setVelocity(final int x, final int y, final double xv, final double yv) {
    u[(x + 1) + (y + 1) * rowSize] = xv;
    v[(x + 1) + (y + 1) * rowSize] = yv;
  }
}

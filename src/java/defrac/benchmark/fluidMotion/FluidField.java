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

import defrac.lang.Procedure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FluidField {
  @Nullable
  private final Object canvas;
  private final int iterations;
  private final int size;
  private double[] dens;
  private double[] dens_prev;
  private double[] u;
  private double[] u_prev;
  private double[] v;
  private double[] v_prev;
  private final int width;
  private final int height;
  private final int rowSize;
  private Procedure<Field> displayFunc;

  @Nullable
  private static FluidField _lastCreated;

  private static boolean notApproxEquals(final double a, final double b) {
    return Math.abs(a - b) >= 0.000001;
  }

  public void validate(final double expectedDens, final double expectedU, final double expectedV) {
    double sumDens = 0.0;
    double sumU = 0.0;
    double sumV = 0.0;
    for (int i = 0; i < dens.length; i++) {
      sumDens += dens[i];
      sumU += u[i];
      sumV += v[i];
    }

    if (notApproxEquals(sumDens, expectedDens) ||
        notApproxEquals(sumU, expectedU) ||
        notApproxEquals(sumV, expectedV)) {
      throw new RuntimeException("Incorrect result");
    }
  }

  // Allocates a new FluidField or return previously allocated field if the
  // size is too large.
  public static FluidField create(@Nullable final Object canvas, final int hRes, final int wRes, final int iterations) {
    final int res = wRes * hRes;
    if ((res > 0) && (res < 1000000)) {
      _lastCreated = new FluidField(canvas, hRes, wRes, iterations);
    } else if (_lastCreated == null) {
      _lastCreated = new FluidField(canvas, 64, 64, iterations);
    }
    assert((canvas == _lastCreated.canvas) &&
        (iterations == _lastCreated.iterations));
    return _lastCreated;
  }

  private FluidField(@Nullable Object canvas, final int hRes, final int wRes, final int iterations) {
    this.canvas = canvas;
    this.width = wRes;
    this.height = hRes;
    this.iterations = iterations;
    this.rowSize = wRes + 2;
    this.size = (wRes + 2) * (hRes + 2);
    reset();
  }

  void reset() {
    // All double[] elements are initialized to 0.0.
    dens = new double[size];
    dens_prev = new double[size];
    u = new double[size];
    u_prev = new double[size];
    v = new double[size];
    v_prev = new double[size];
  }

  void addFields(@Nonnull final double[] x, @Nonnull final double[] s, final double dt) {
    for (int i=0; i< size ; i++) x[i] += dt*s[i];
  }

  void set_bnd(final int b, @Nonnull final double[] values) {
    if (b==1) {
      int i = 1;
      for (; i <= width; i++) {
        values[i] =  values[i + rowSize];
        values[i + (height+1) *rowSize] = values[i + height * rowSize];
      }

      for (int j = 1; j <= height; j++) {
        values[j * rowSize] = -values[1 + j * rowSize];
        values[(width + 1) + j * rowSize] = -values[width + j * rowSize];
      }
    } else if (b == 2) {
      for (int i = 1; i <= width; i++) {
        values[i] = -values[i + rowSize];
        values[i + (height + 1) * rowSize] = -values[i + height * rowSize];
      }

      for (int j = 1; j <= height; j++) {
        values[j * rowSize] =  values[1 + j * rowSize];
        values[(width + 1) + j * rowSize] =  values[width + j * rowSize];
      }
    } else {
      for (int i = 1; i <= width; i++) {
        values[i] =  values[i + rowSize];
        values[i + (height + 1) * rowSize] = values[i + height * rowSize];
      }

      for (int j = 1; j <= height; j++) {
        values[j * rowSize] =  values[1 + j * rowSize];
        values[(width + 1) + j * rowSize] =  values[width + j * rowSize];
      }
    }
    int maxEdge = (height + 1) * rowSize;
    values[0]                 = 0.5 * (values[1] + values[rowSize]);
    values[maxEdge]           = 0.5 * (values[1 + maxEdge] + values[height * rowSize]);
    values[(width+1)]         = 0.5 * (values[width] + values[(width + 1) + rowSize]);
    values[(width+1)+maxEdge] = 0.5 * (values[width + maxEdge] + values[(width + 1) +
        height * rowSize]);
  }

  void lin_solve(final int b, @Nonnull final double[] x, @Nonnull final double[] x0, final int a, final int c) {
    if (a == 0 && c == 1) {
      for (int j=1 ; j<=height; j++) {
        int currentRow = j * rowSize;
        ++currentRow;
        for (int i = 0; i < width; i++) {
          x[currentRow] = x0[currentRow];
          ++currentRow;
        }
      }
      set_bnd(b, x);
    } else {
      double invC = 1.0 / (double)c;
      for (int k=0 ; k<iterations; k++) {
        for (int j=1 ; j<=height; j++) {
          int lastRow = (j - 1) * rowSize;
          int currentRow = j * rowSize;
          int nextRow = (j + 1) * rowSize;
          double lastX = x[currentRow];
          ++currentRow;
          for (int i=1; i<=width; i++)
            lastX = x[currentRow] = (x0[currentRow] +
                a*(lastX+x[++currentRow]+x[++lastRow]+x[++nextRow])) * invC;
        }
        set_bnd(b, x);
      }
    }
  }

  void diffuse(final int b, @Nonnull final double[] x, @Nonnull final double[] x0) {
    int a = 0;
    lin_solve(b, x, x0, a, 1 + 4*a);
  }

  void lin_solve2(@Nonnull final double[] x, @Nonnull final double[] x0,
                  @Nonnull final double[] y, @Nonnull final double[] y0, final int a, final int c) {
    if (a == 0 && c == 1) {
      for (int j=1 ; j <= height; j++) {
        int currentRow = j * rowSize;
        ++currentRow;
        for (int i = 0; i < width; i++) {
          x[currentRow] = x0[currentRow];
          y[currentRow] = y0[currentRow];
          ++currentRow;
        }
      }
      set_bnd(1, x);
      set_bnd(2, y);
    } else {
      double invC = 1.0/(double)c;
      for (int k=0 ; k<iterations; k++) {
        for (int j=1 ; j <= height; j++) {
          int lastRow = (j - 1) * rowSize;
          int currentRow = j * rowSize;
          int nextRow = (j + 1) * rowSize;
          double lastX = x[currentRow];
          double lastY = y[currentRow];
          ++currentRow;
          for (int i = 1; i <= width; i++) {
            lastX = x[currentRow] = (x0[currentRow] + a *
                (lastX + x[currentRow] + x[lastRow] + x[nextRow])) * invC;
            lastY = y[currentRow] = (y0[currentRow] + a *
                (lastY + y[++currentRow] + y[++lastRow] + y[++nextRow])) * invC;
          }
        }
        set_bnd(1, x);
        set_bnd(2, y);
      }
    }
  }

  void diffuse2(@Nonnull final double[] x, @Nonnull final double[] x0, @Nonnull final double[] y,
                @Nonnull final double[] y0) {
    int a = 0;
    lin_solve2(x, x0, y, y0, a, 1 + 4 * a);
  }

  void advect(final int b, @Nonnull final double[] d, @Nonnull final double[] d0,
              @Nonnull final double[] u, @Nonnull final double[] v, final double dt) {
    double Wdt0 = dt * width;
    double Hdt0 = dt * height;
    double Wp5 = width + 0.5;
    double Hp5 = height + 0.5;
    for (int j = 1; j<= height; j++) {
      int pos = j * rowSize;
      for (int i = 1; i <= width; i++) {
        double x = i - Wdt0 * u[++pos];
        double y = j - Hdt0 * v[pos];
        if (x < 0.5)
          x = 0.5;
        else if (x > Wp5)
          x = Wp5;
        int i0 = (int)x;
        int i1 = i0 + 1;
        if (y < 0.5)
          y = 0.5;
        else if (y > Hp5)
          y = Hp5;
        int j0 = (int)y;
        int j1 = j0 + 1;
        double s1 = x - i0;
        double s0 = 1 - s1;
        double t1 = y - j0;
        double t0 = 1 - t1;
        int row1 = j0 * rowSize;
        int row2 = j1 * rowSize;
        d[pos] = s0 * (t0 * d0[i0 + row1] + t1 * d0[i0 + row2]) +
            s1 * (t0 * d0[i1 + row1] + t1 * d0[i1 + row2]);
      }
    }
    set_bnd(b, d);
  }

  void project(@Nonnull final double[] u, @Nonnull final double[] v,
               @Nonnull final double[] p, @Nonnull final double[] div) {
    double h = -0.5 / Math.sqrt(width * height);
    for (int j = 1 ; j <= height; j++ ) {
      int row = j * rowSize;
      int previousRow = (j - 1) * rowSize;
      int prevValue = row - 1;
      int currentRow = row;
      int nextValue = row + 1;
      int nextRow = (j + 1) * rowSize;
      for (int i = 1; i <= width; i++ ) {
        div[++currentRow] = h * (u[++nextValue] - u[++prevValue] +
            v[++nextRow] - v[++previousRow]);
        p[currentRow] = 0.0;
      }
    }
    set_bnd(0, div);
    set_bnd(0, p);

    lin_solve(0, p, div, 1, 4 );
    double wScale = 0.5 * width;
    double hScale = 0.5 * height;
    for (int j = 1; j<= height; j++ ) {
      int prevPos = j * rowSize - 1;
      int currentPos = j * rowSize;
      int nextPos = j * rowSize + 1;
      int prevRow = (j - 1) * rowSize;
      int nextRow = (j + 1) * rowSize;

      for (int i = 1; i<= width; i++) {
        u[++currentPos] -= wScale * (p[++nextPos] - p[++prevPos]);
        v[currentPos]   -= hScale * (p[++nextRow] - p[++prevRow]);
      }
    }
    set_bnd(1, u);
    set_bnd(2, v);
  }

  void dens_step(@Nonnull final double[] x, @Nonnull final double[] x0,
                 @Nonnull final double[] u, @Nonnull final double[] v, final double dt) {
    addFields(x, x0, dt);
    diffuse(0, x0, x);
    advect(0, x, x0, u, v, dt );
  }

  void vel_step(@Nonnull double[] u, @Nonnull double[] v,
                @Nonnull double[] u0, @Nonnull double[] v0, final double dt) {
    addFields(u, u0, dt );
    addFields(v, v0, dt );
    double[] temp = u0; u0 = u; u = temp;
    temp = v0; v0 = v; v = temp;
    diffuse2(u,u0,v,v0);
    project(u, v, u0, v0);
    temp = u0; u0 = u; u = temp;
    temp = v0; v0 = v; v = temp;
    advect(1, u, u0, u0, v0, dt);
    advect(2, v, v0, u0, v0, dt);
    project(u, v, u0, v0 );
  }

  private Procedure<Field> uiCallback;


  public void setDisplayFunction(Procedure<Field> func) {
    displayFunc = func;
  }

  public void setUICallback(Procedure<Field> callback) {
    uiCallback = callback;
  }

  void queryUI(@Nonnull final double[] d, @Nonnull final double[] u, @Nonnull final double[] v) {
    for (int i = 0; i < size; i++) {
      u[i] = v[i] = d[i] = 0.0;
    }
    uiCallback.apply(new Field(d, u, v, rowSize));
  }

  public void update() {
    queryUI(dens_prev, u_prev, v_prev);
    vel_step(u, v, u_prev, v_prev, 0.1);
    dens_step(dens, dens_prev, u, v, 0.1);
    displayFunc.apply(new Field(dens, u, v, rowSize));
  }
}

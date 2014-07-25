// Copyright 2011 Google Inc. All Rights Reserved.
// Copyright 1996 John Maloney and Mario Wolczko
//
// This file is part of GNU Smalltalk.
//
// GNU Smalltalk is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 2, or (at your option) any later version.
//
// GNU Smalltalk is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// GNU Smalltalk; see the file COPYING.  If not, write to the Free Software
// Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
//
// Translated first from Smalltalk to JavaScript, and finally to
// Dart by Google 2008-2010.
//
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark;

import defrac.benchmark.deltablue.*;
import defrac.lang.Lists;

import java.util.ArrayList;

public final class DeltaBlue extends BenchmarkBase {
  public static void main(final String[] args) {
    new DeltaBlue().report();
  }

  DeltaBlue() { super("DeltaBlue"); }

  @Override
  public void run() {
    chainTest(100);
    projectionTest(100);
  }

  private void chainTest(final int n) {
    final Planner planner = new Planner();
    Planner.INSTANCE = planner;
    Variable prev = null, first = null, last = null;
    // Build chain of n equality constraints.
    for (int i = 0; i <= n; i++) {
      Variable v = new Variable(0);
      if (prev != null) new EqualityConstraint(prev, v, Strength.REQUIRED);
      if (i == 0) first = v;
      if (i == n) last = v;
      prev = v;
    }
    new StayConstraint(last, Strength.STRONG_DEFAULT);
    EditConstraint edit = new EditConstraint(first, Strength.PREFERRED);
    Plan plan = planner.extractPlanFromConstraints(Lists.newArrayList(edit));
    for (int i = 0; i < 100; i++) {
      first.value = i;
      plan.execute();
      if (last.value != i) {
        System.out.println("Chain test failed:");
        System.out.println("Expected last value to be "+i+" but it was "+last.value+".");
      }
    }
  }

  private void projectionTest(final int n) {
    Planner.INSTANCE = new Planner();
    Variable scale = new Variable(10);
    Variable offset = new Variable(1000);
    Variable src = null, dst = null;

    ArrayList<Variable> dests = Lists.newArrayList(n);
    for (int i = 0; i < n; i++) {
      src = new Variable(i);
      dst = new Variable(i);
      dests.add(dst);
      new StayConstraint(src, Strength.NORMAL);
      new ScaleConstraint(src, scale, offset, dst, Strength.REQUIRED);
    }
    change(src, 17);
    if (dst.value != 1170) System.out.println("Projection 1 failed");
    change(dst, 1050);
    if (src.value != 5) System.out.println("Projection 2 failed");
    change(scale, 5);
    for (int i = 0; i < n - 1; i++) {
      if (dests.get(i).value != i * 5 + 1000) System.out.println("Projection 3 failed");
    }
    change(offset, 2000);
    for (int i = 0; i < n - 1; i++) {
      if (dests.get(i).value != i * 5 + 2000) System.out.println("Projection 4 failed");
    }
  }

  private void change(Variable v, int newValue) {
    EditConstraint edit = new EditConstraint(v, Strength.PREFERRED);
    Plan plan = Planner.INSTANCE.extractPlanFromConstraints(Lists.newArrayList(edit));
    for (int i = 0; i < 10; i++) {
      v.value = newValue;
      plan.execute();
    }
    edit.destroyConstraint();
  }
}

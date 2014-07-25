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
package defrac.benchmark.deltablue;

import defrac.lang.Lists;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;

// dart version of benchmark uses indexed for loop here
// so we do the same
//
// see https://github.com/dart-lang/ton80/blob/c1ac5e2180747b0a9c438748e96d1543831ebabf/lib/src/DeltaBlue/dart/DeltaBlue.dart
@SuppressWarnings("ForLoopReplaceableByForEach")
public final class Planner {
  public static Planner INSTANCE;

  private int currentMark = 0;

  public void incrementalAdd(@Nonnull final Constraint c) {
    final int mark = newMark();
    //noinspection StatementWithEmptyBody
    for(Constraint overridden = c.satisfy(mark);
        overridden != null;
        overridden = overridden.satisfy(mark));
  }

  public void incrementalRemove(@Nonnull final Constraint c) {
    final Variable out = c.output();

    c.markUnsatisfied();
    c.removeFromGraph();

    final ArrayList<Constraint> unsatisfied = removePropagateFrom(out);

    Strength strength = Strength.REQUIRED;

    do {
      final int length = unsatisfied.size();
      for(int i = 0; i < length; i++) {
        Constraint u = unsatisfied.get(i);
        if (u.strength == strength) {
          incrementalAdd(u);
        }
      }
      strength = strength.nextWeaker();
    } while (strength != Strength.WEAKEST);
  }

  int newMark() {
    return ++currentMark;
  }

  @Nonnull
  Plan makePlan(@Nonnull final LinkedList<Constraint> sources) {
    final int mark = newMark();
    final Plan plan = new Plan();

    while(!sources.isEmpty()) {
      Constraint c = sources.removeLast();
      if (c.output().mark != mark && c.inputsKnown(mark)) {
        plan.addConstraint(c);
        c.output().mark = mark;
        addConstraintsConsumingTo(c.output(), sources);
      }
    }
    return plan;
  }

  @Nonnull
  public Plan extractPlanFromConstraints(@Nonnull final ArrayList<? extends Constraint> constraints) {
    final LinkedList<Constraint> sources = Lists.newLinkedList();
    final int length = constraints.size();
    for(int i = 0; i < length; i++) {
      Constraint c = constraints.get(i);
      if (c.isInput() && c.isSatisfied()) {
        sources.add(c);
      }
    }
    return makePlan(sources);
  }

  public boolean addPropagate(@Nonnull final Constraint c, final int mark) {
    final LinkedList<Constraint> todo = Lists.newLinkedList();
    todo.add(c);

    while(!todo.isEmpty()) {
      final Constraint d = todo.removeLast();

      if (d.output().mark == mark) {
        incrementalRemove(c);
        return false;
      }
      d.recalculate();
      addConstraintsConsumingTo(d.output(), todo);
    }

    return true;
  }

  @Nonnull
  ArrayList<Constraint> removePropagateFrom(@Nonnull final Variable out) {
    out.determinedBy = null;
    out.walkStrength = Strength.WEAKEST;
    out.stay = true;
    final ArrayList<Constraint> unsatisfied = Lists.newArrayList();
    final LinkedList<Variable> todo = Lists.newLinkedList();
    todo.add(out);
    while(!todo.isEmpty()) {
      final Variable v = todo.removeLast();
      final ArrayList<Constraint> constraints = v.constraints;
      final int length = constraints.size();
      for (int i = 0; i < length; i++) {
        Constraint c = constraints.get(i);
        if (!c.isSatisfied()) unsatisfied.add(c);
      }

      final Constraint determining = v.determinedBy;

      for (int i = 0; i < length; i++) {
        Constraint next = constraints.get(i);
        if (next != determining && next.isSatisfied()) {
          next.recalculate();
          todo.add(next.output());
        }
      }
    }
    return unsatisfied;
  }

  void addConstraintsConsumingTo(@Nonnull final Variable v, @Nonnull final LinkedList<Constraint> coll) {
    final Constraint determining = v.determinedBy;
    final ArrayList<Constraint> constraints = v.constraints;
    final int length = constraints.size();
    for (int i = 0; i < length; i++) {
      final Constraint c = constraints.get(i);
      if (c != determining && c.isSatisfied()) {
        coll.add(c);
      }
    }
  }
}

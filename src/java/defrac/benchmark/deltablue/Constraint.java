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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Constraint {
  @Nonnull
  public final Strength strength;

  Constraint(@Nonnull final Strength strength) {
    this.strength = strength;
  }

  public abstract boolean isSatisfied();
  public abstract void markUnsatisfied();
  protected abstract void addToGraph();
  public abstract void removeFromGraph();
  protected abstract void chooseMethod(final int mark);
  protected abstract void markInputs(final int mark);
  public abstract boolean inputsKnown(final int mark);
  public abstract Variable output();
  public abstract void execute();
  public abstract void recalculate();

  void addConstraint() {
    addToGraph();
    Planner.INSTANCE.incrementalAdd(this);
  }

  @Nullable
  public Constraint satisfy(final int mark) {
    chooseMethod(mark);
    if(!isSatisfied()) {
      if(strength == Strength.REQUIRED) {
        System.out.println("Could not satisfy a required constraint!");
      }

      return null;
    }

    markInputs(mark);
    Variable out = output();
    Constraint overridden = out.determinedBy;

    if (overridden != null) {
      overridden.markUnsatisfied();
    }

    out.determinedBy = this;

    if (!Planner.INSTANCE.addPropagate(this, mark)) {
      System.out.println("Cycle encountered");
    }

    out.mark = mark;
    return overridden;
  }

  public void destroyConstraint() {
    if(isSatisfied()) {
      Planner.INSTANCE.incrementalRemove(this);
    }

    removeFromGraph();
  }

  public boolean isInput() { return false; }
}

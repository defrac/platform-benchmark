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

public abstract class BinaryConstraint extends Constraint {
  private static final int NONE = 1;
  static final int FORWARD = 2;
  private static final int BACKWARD = 0;

  final Variable v1;
  final Variable v2;

  int direction = NONE;

  BinaryConstraint(@Nonnull final Variable v1, @Nonnull final Variable v2, @Nonnull final Strength strength) {
    super(strength);
    this.v1 = v1;
    this.v2 = v2;
  }

  @Override
  public void chooseMethod(int mark) {
    if (v1.mark == mark) {
      direction = (v2.mark != mark &&
          Strength.stronger(strength, v2.walkStrength))
          ? FORWARD : NONE;
    }
    if (v2.mark == mark) {
      direction = (v1.mark != mark &&
          Strength.stronger(strength, v1.walkStrength))
          ? BACKWARD : NONE;
    }
    if (Strength.weaker(v1.walkStrength, v2.walkStrength)) {
      direction = Strength.stronger(strength, v1.walkStrength)
          ? BACKWARD : NONE;
    } else {
      direction = Strength.stronger(strength, v2.walkStrength)
          ? FORWARD : BACKWARD;
    }
  }

  @Override
  protected void addToGraph() {
    v1.addConstraint(this);
    v2.addConstraint(this);
    direction = NONE;
  }

  @Override
  public boolean isSatisfied() {
    return direction != NONE;
  }

  @Override
  protected void markInputs(int mark) {
    input().mark = mark;
  }

  @Nonnull
  Variable input() {
    return direction == FORWARD ? v1 : v2;
  }

  @Nonnull
  public Variable output() {
    return direction == FORWARD ? v2 : v1;
  }

  @Override
  public void recalculate() {
    final Variable ihn = input();
    final Variable out = output();
    out.walkStrength = Strength.weakest(strength, ihn.walkStrength);
    out.stay = ihn.stay;
    if(out.stay) {
      execute();
    }
  }

  @Override
  public void markUnsatisfied() {
    direction = NONE;
  }

  @Override
  public boolean inputsKnown(int mark) {
    Variable i = input();
    return i.mark == mark || i.stay || i.determinedBy == null;
  }

  @Override
  public void removeFromGraph() {
    v1.removeConstraint(this);
    v2.removeConstraint(this);
    direction = NONE;
  }
}

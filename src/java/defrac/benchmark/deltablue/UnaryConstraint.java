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

public abstract class UnaryConstraint  extends Constraint {
  private final Variable myOutput;

  private boolean satisfied;

  UnaryConstraint(final Variable myOutput, @Nonnull final Strength strength) {
    super(strength);
    this.myOutput = myOutput;
    addConstraint();
  }

  @Override
  public void addToGraph() {
    myOutput.addConstraint(this);
    satisfied = false;
  }

  @Override
  public void chooseMethod(int mark) {
    satisfied = (myOutput.mark != mark) &&
        Strength.stronger(strength, myOutput.walkStrength);
  }

  @Override
  public boolean isSatisfied() {
    return satisfied;
  }

  @Override
  public void markInputs(int mark) {

  }

  @Override
  public Variable output() {
    return myOutput;
  }

  @Override
  public void recalculate() {
    myOutput.walkStrength = strength;
    myOutput.stay = !isInput();
    if(myOutput.stay) {
      execute();
    }
  }

  @Override
  public void markUnsatisfied() {
    satisfied = false;
  }

  @Override
  public boolean inputsKnown(int mark) {
    return true;
  }

  @Override
  public void removeFromGraph() {
    if(myOutput != null) {
      myOutput.removeConstraint(this);
    }

    satisfied = false;
  }
}

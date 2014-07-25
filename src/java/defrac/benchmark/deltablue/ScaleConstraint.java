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

public final class ScaleConstraint extends BinaryConstraint {
  @Nonnull
  private final Variable scale;

  @Nonnull
  private final Variable offset;

  public ScaleConstraint(@Nonnull final Variable src, @Nonnull final Variable scale, @Nonnull final Variable offset,
                         @Nonnull final Variable dest, @Nonnull final Strength strength) {
    super(src, dest, strength);
    this.scale = scale;
    this.offset = offset;
    addConstraint();
  }

  @Override
  public void addToGraph() {
    super.addToGraph();
    scale.addConstraint(this);
    offset.addConstraint(this);
  }

  @Override
  public void removeFromGraph() {
    super.removeFromGraph();
    scale.removeConstraint(this);
    offset.removeConstraint(this);
  }

  @Override
  public void markInputs(int mark) {
    super.markInputs(mark);
    scale.mark = offset.mark = mark;
  }

  @Override
  public void execute() {
    if(direction == FORWARD) {
      v2.value = v1.value * scale.value + offset.value;
    } else {
      v1.value = (v2.value - offset.value) / scale.value;
    }
  }

  @Override
  public void recalculate() {
    final Variable ihn = input();
    final Variable out = output();

    out.walkStrength = Strength.weakest(strength, ihn.walkStrength);
    out.stay = ihn.stay && scale.stay && offset.stay;
    if(out.stay) {
      execute();
    }
  }
}

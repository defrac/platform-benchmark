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

public final class Plan {
  @Nonnull
  private final ArrayList<Constraint> list = Lists.newArrayList();

  public void addConstraint(@Nonnull final Constraint c) {
    list.add(c);
  }

  public void execute() {
    final int length = list.size();
    //noinspection ForLoopReplaceableByForEach
    for(int i = 0; i < length; i++) {
      list.get(i).execute();
    }
  }
}

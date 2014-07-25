// Copyright 2012 Google Inc.
// All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark.havlak;

import defrac.lang.Lists;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static defrac.benchmark.Havlak.mix;

public class LSG {
  private int loopCounter = 1;

  @Nonnull
  private final ArrayList<SimpleLoop> loops = Lists.newArrayList();

  @Nonnull
  private final SimpleLoop root = new SimpleLoop(0);

  public LSG() {
    root.setNestingLevel(0);
    loops.add(root);
  }

  SimpleLoop createNewLoop() {
    return new SimpleLoop(loopCounter++);
  }

  void addLoop(@Nonnull final SimpleLoop loop) { loops.add(loop); }

  public int checksum() {
    int result = loops.size();
    for(SimpleLoop e : loops) {
      result = mix(result, e.checksum());
    }
    return mix(result, root.checksum());
  }

  int getNumLoops() { return loops.size(); }
}

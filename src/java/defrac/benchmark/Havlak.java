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
package defrac.benchmark;

import defrac.benchmark.havlak.BasicBlockEdge;
import defrac.benchmark.havlak.CFG;
import defrac.benchmark.havlak.HavlakLoopFinder;
import defrac.benchmark.havlak.LSG;

import javax.annotation.Nonnull;

public final class Havlak extends BenchmarkBase {
  public static void main(String[] args) {
    new Havlak().report();
  }

  public static int mix(final int existing, final int value) {
    return ((existing & 0x0fffffff) << 1) + value;
  }

  private static int buildDiamond(@Nonnull final CFG cfg, final int start) {
    new BasicBlockEdge(cfg, start, start + 1);
    new BasicBlockEdge(cfg, start, start + 2);
    new BasicBlockEdge(cfg, start + 1, start + 3);
    new BasicBlockEdge(cfg, start + 2, start + 3);
    return start + 3;
  }


  private static void buildConnect(@Nonnull final CFG cfg, final int start, final int end) {
    new BasicBlockEdge(cfg, start, end);
  }

  private static int buildStraight(@Nonnull final CFG cfg, final int start, final int n) {
    for (int i=0; i < n; i++) {
      buildConnect(cfg, start + i, start + i + 1);
    }
    return start + n;
  }

  private static int buildBaseLoop(@Nonnull final CFG cfg, final int from) {
    int header   = buildStraight(cfg, from, 1);
    int diamond1 = buildDiamond(cfg, header);
    int d11      = buildStraight(cfg, diamond1, 1);
    int diamond2 = buildDiamond(cfg, d11);
    int footer   = buildStraight(cfg, diamond2, 1);
    buildConnect(cfg, diamond2, d11);
    buildConnect(cfg, diamond1, header);

    buildConnect(cfg, footer, from);
    footer = buildStraight(cfg, footer, 1);
    return footer;
  }


  @Nonnull
  private final CFG cfg = new CFG();

  Havlak() {
    super("Havlak");

    // Construct simple CFG.
    cfg.createNode(0);  // top
    buildBaseLoop(cfg, 0);
    cfg.createNode(1);  //s bottom
    buildConnect(cfg, 0, 2);

    // Construct complex CFG.
    int n = 2;
    for (int parlooptrees=0; parlooptrees < 10; parlooptrees++) {
      cfg.createNode(n + 1);
      buildConnect(cfg, n, n + 1);
      n = n + 1;
      for (int i=0; i < 2; ++i) {
        int top = n;
        n = buildStraight(cfg, n, 1);
        for (int j=0; j < 25; j++) {
          n = buildBaseLoop(cfg, n);
        }

        int bottom = buildStraight(cfg, n, 1);
        buildConnect(cfg, n, top);
        n = bottom;
      }
    }
  }

  @Override
  public void exercise() {
    LSG lsg = new LSG();
    HavlakLoopFinder finder = new HavlakLoopFinder(cfg, lsg);
    int numLoops = finder.findLoops();
    if (numLoops != 1522) {
      throw new RuntimeException("Wrong result - expected <1522>, but was <"+numLoops+">");
    }
  }

  @Override
  public void warmup() {
    for (int dummyloop = 0; dummyloop < 20; ++dummyloop) {
      LSG lsg = new LSG();
      HavlakLoopFinder finder = new HavlakLoopFinder(cfg, lsg);
      finder.findLoops();
      int checksum = lsg.checksum();

      if (checksum != 435630002) {
        throw new RuntimeException("Wrong checksum - expected <435630002>, but was <"+checksum+">");
      }
    }
  }
}

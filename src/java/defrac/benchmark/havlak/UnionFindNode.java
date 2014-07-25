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
import javax.annotation.Nullable;
import java.util.ArrayList;

public final class UnionFindNode {
  int dfsNumber = 0;

  @Nullable
  private UnionFindNode parent;

  @Nullable
  BasicBlock bb;

  @Nullable
  SimpleLoop loop;

  // Initialize this node.
  //
  public void initNode(BasicBlock bb, int dfsNumber) {
    parent = this;
    this.bb = bb;
    this.dfsNumber = dfsNumber;
  }

  // Union/Find Algorithm - The find routine.
  //
  // Implemented with Path Compression (inner loops are only
  // visited and collapsed once, however, deep nests would still
  // result in significant traversals).
  //
  public UnionFindNode findSet() {
    final ArrayList<UnionFindNode> nodeList = Lists.newArrayList();

    UnionFindNode node = this;
    while (node != node.parent) {
      if (node.parent != node.parent.parent)
        nodeList.add(node);

      node = node.parent;
    }

    // Path Compression, all nodes' parents point to the 1st level parent.
    final int n = nodeList.size();
    //noinspection ForLoopReplaceableByForEach
    for (int iter=0; iter < n; ++iter) {
      nodeList.get(iter).parent = node.parent;
    }

    return node;
  }

  // Union/Find Algorithm - The union routine.
  //
  // Trivial. Assigning parent pointer is enough,
  // we rely on path compression.
  //
  public void union(@Nonnull final UnionFindNode unionFindNode) {
    parent = unionFindNode;
  }
}

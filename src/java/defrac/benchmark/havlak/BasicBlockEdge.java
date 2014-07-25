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

import javax.annotation.Nonnull;

public class BasicBlockEdge {
  public BasicBlockEdge(@Nonnull final CFG cfg, final int fromName, final int toName) {
    final BasicBlock from = cfg.createNode(fromName);
    final BasicBlock to = cfg.createNode(toName);

    from.addOutEdge(to);
    to.addInEdge(from);

    cfg.addEdge(this);
  }
}

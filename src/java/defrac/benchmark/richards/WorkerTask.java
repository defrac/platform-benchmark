// Copyright 2006-2008 the V8 project authors. All rights reserved.
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials provided
//       with the distribution.
//     * Neither the name of Google Inc. nor the names of its
//       contributors may be used to endorse or promote products derived
//       from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

// Ported by the Dart team to Dart.

// This is a Dart implementation of the Richards benchmark from:
//
//    http://www.cl.cam.ac.uk/~mr10/Bench.html
//
// The benchmark was originally implemented in BCPL by
// Martin Richards.
//

// Translated from Dart's ton80 benchmark suite to Java
package defrac.benchmark.richards;

import defrac.benchmark.Richards;

final class WorkerTask extends Task {

  private int v1; // A seed used to specify how work packets are manipulated.
  private int v2; // Another seed used to specify how work packets are manipulated.

  WorkerTask(Scheduler scheduler, int v1, int v2)  {
    super(scheduler);
    this.v1 = v1;
    this.v2 = v2;
  }

  TaskControlBlock run(Packet packet) {
    if (packet == null) {
      return scheduler.suspendCurrent();
    }
    if (v1 == Richards.ID_HANDLER_A) {
      v1 = Richards.ID_HANDLER_B;
    } else {
      v1 = Richards.ID_HANDLER_A;
    }
    packet.id = v1;
    packet.a1 = 0;
    for (int i = 0; i < Richards.DATA_SIZE; i++) {
      v2++;
      if (v2 > 26) v2 = 1;
      packet.a2[i] = v2;
    }
    return scheduler.queue(packet);
  }

  @Override
  public String toString() {
    return "WorkerTask";
  }
}

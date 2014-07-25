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
package defrac.benchmark;

import defrac.benchmark.richards.Packet;
import defrac.benchmark.richards.Scheduler;

public final class Richards extends BenchmarkBase {
  public static void main(String[] args) {
    new Richards().report();
  }

  Richards() {
    super("Richards");
  }

  public static final int DATA_SIZE = 4;
  public static final int COUNT = 1000;

  /**
   * These two constants specify how many times a packet is queued and
   * how many times a task is put on hold in a correct run of richards.
   * They don't have any meaning a such but are characteristic of a
   * correct run so if the actual queue or hold count is different from
   * the expected there must be a bug in the implementation.
   **/
  public static final int EXPECTED_QUEUE_COUNT = 2322;
  public static final int EXPECTED_HOLD_COUNT = 928;

  public static final int ID_IDLE = 0;
  public static final int ID_WORKER = 1;
  public static final int ID_HANDLER_A = 2;
  public static final int ID_HANDLER_B = 3;
  public static final int ID_DEVICE_A = 4;
  public static final int ID_DEVICE_B = 5;
  public static final int NUMBER_OF_IDS = 6;

  public static final int KIND_DEVICE = 0;
  public static final int KIND_WORK = 1;


  @Override
  public void run() {
    Scheduler scheduler = new Scheduler();
    scheduler.addIdleTask(ID_IDLE, 0, null, COUNT);

    Packet queue = new Packet(null, ID_WORKER, KIND_WORK);
    queue = new Packet(queue, ID_WORKER, KIND_WORK);
    scheduler.addWorkerTask(ID_WORKER, 1000, queue);

    queue = new Packet(null, ID_DEVICE_A, KIND_DEVICE);
    queue = new Packet(queue, ID_DEVICE_A, KIND_DEVICE);
    queue = new Packet(queue, ID_DEVICE_A, KIND_DEVICE);
    scheduler.addHandlerTask(ID_HANDLER_A, 2000, queue);

    queue = new Packet(null, ID_DEVICE_B, KIND_DEVICE);
    queue = new Packet(queue, ID_DEVICE_B, KIND_DEVICE);
    queue = new Packet(queue, ID_DEVICE_B, KIND_DEVICE);
    scheduler.addHandlerTask(ID_HANDLER_B, 3000, queue);

    scheduler.addDeviceTask(ID_DEVICE_A, 4000, null);

    scheduler.addDeviceTask(ID_DEVICE_B, 5000, null);

    scheduler.schedule();

    if (scheduler.queueCount != EXPECTED_QUEUE_COUNT ||
        scheduler.holdCount != EXPECTED_HOLD_COUNT) {
      System.out.println("Error during execution: queueCount = "+scheduler.queueCount+
          ", holdCount = "+scheduler.holdCount+".");
    }
    if (EXPECTED_QUEUE_COUNT != scheduler.queueCount) {
      throw new RuntimeException("bad scheduler queue-count");
    }
    if (EXPECTED_HOLD_COUNT != scheduler.holdCount) {
      throw new RuntimeException("bad scheduler hold-count");
    }
  }
}

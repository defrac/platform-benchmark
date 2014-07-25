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

public final class Scheduler {
  public int queueCount = 0;
  public int holdCount = 0;
  private TaskControlBlock currentTcb;
  private int currentId;
  private TaskControlBlock list;
  private final TaskControlBlock[] blocks =
      new TaskControlBlock[Richards.NUMBER_OF_IDS];

  /// Add an idle task to this scheduler.
  public void addIdleTask(int id, int priority, Packet queue, int count) {
    addRunningTask(id, priority, queue, new IdleTask(this, 1, count));
  }

  /// Add a work task to this scheduler.
  public void addWorkerTask(int id, int priority, Packet queue) {
    addTask(id,
        priority,
        queue,
        new WorkerTask(this, Richards.ID_HANDLER_A, 0));
  }

  /// Add a handler task to this scheduler.
  public void addHandlerTask(int id, int priority, Packet queue) {
    addTask(id, priority, queue, new HandlerTask(this));
  }

  /// Add a handler task to this scheduler.
  public void addDeviceTask(int id, int priority, Packet queue) {
    addTask(id, priority, queue, new DeviceTask(this));
  }

  /// Add the specified task and mark it as running.
  void addRunningTask(int id, int priority, Packet queue, Task task) {
    addTask(id, priority, queue, task);
    currentTcb.setRunning();
  }

  /// Add the specified task to this scheduler.
  void addTask(int id, int priority, Packet queue, Task task) {
    currentTcb = new TaskControlBlock(list, id, priority, queue, task);
    list = currentTcb;
    blocks[id] = currentTcb;
  }

  /// Execute the tasks managed by this scheduler.
  public void schedule() {
    currentTcb = list;
    while (currentTcb != null) {
      if (currentTcb.isHeldOrSuspended()) {
        currentTcb = currentTcb.link;
      } else {
        currentId = currentTcb.id;
        currentTcb = currentTcb.run();
      }
    }
  }

  /// Release a task that is currently blocked and return the next block to run.
  TaskControlBlock release(int id) {
    TaskControlBlock tcb = blocks[id];
    if (tcb == null) return null;
    tcb.markAsNotHeld();
    if (tcb.priority > currentTcb.priority) return tcb;
    return currentTcb;
  }

  /**
   * Block the currently executing task and return the next task control block
   * to run.  The blocked task will not be made runnable until it is explicitly
   * released, even if new work is added to it.
   */
  TaskControlBlock holdCurrent() {
    holdCount++;
    currentTcb.markAsHeld();
    return currentTcb.link;
  }

  /**
   * Suspend the currently executing task and return the next task
   * control block to run.
   * If new work is added to the suspended task it will be made runnable.
   */
  TaskControlBlock suspendCurrent() {
    currentTcb.markAsSuspended();
    return currentTcb;
  }

  /**
   * Add the specified packet to the end of the worklist used by the task
   * associated with the packet and make the task runnable if it is currently
   * suspended.
   */
  TaskControlBlock queue(Packet packet) {
    TaskControlBlock t = blocks[packet.id];
    if (t == null) return null;
    queueCount++;
    packet.link = null;
    packet.id = currentId;
    return t.checkPriorityAdd(currentTcb, packet);
  }
}

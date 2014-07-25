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

final class TaskControlBlock {

  final TaskControlBlock link;
  final int id;       // The id of this block.
  final int priority; // The priority of this block.
  private Packet queue; // The queue of packages to be processed by the task.
  private final Task task;
  private int state;

  TaskControlBlock(TaskControlBlock link, int id, int priority, Packet queue, Task task) {
    this.link = link;
    this.id = id;
    this.priority = priority;
    this.queue = queue;
    this.task = task;
    state = queue == null ? STATE_SUSPENDED : STATE_SUSPENDED_RUNNABLE;
  }

  /// The task is running and is currently scheduled.
  private static final int STATE_RUNNING = 0;

  /// The task has packets left to process.
  private static final int STATE_RUNNABLE = 1;

  /**
   * The task is not currently running. The task is not blocked as such and may
   * be started by the scheduler.
   */
  private static final int STATE_SUSPENDED = 2;

  /// The task is blocked and cannot be run until it is explicitly released.
  private static final int STATE_HELD = 4;

  private static final int STATE_SUSPENDED_RUNNABLE = STATE_SUSPENDED | STATE_RUNNABLE;
  private static final int STATE_NOT_HELD = ~STATE_HELD;

  void setRunning() {
    state = STATE_RUNNING;
  }

  void markAsNotHeld() {
    state = state & STATE_NOT_HELD;
  }

  void markAsHeld() {
    state = state | STATE_HELD;
  }

  boolean isHeldOrSuspended() {
    return (state & STATE_HELD) != 0 ||
        (state == STATE_SUSPENDED);
  }

  void markAsSuspended() {
    state = state | STATE_SUSPENDED;
  }

  void markAsRunnable() {
    state = state | STATE_RUNNABLE;
  }

  /// Runs this task, if it is ready to be run, and returns the next task to run.
  TaskControlBlock run() {
    Packet packet;
    if (state == STATE_SUSPENDED_RUNNABLE) {
      packet = queue;
      queue = packet.link;
      state = queue == null ? STATE_RUNNING : STATE_RUNNABLE;
    } else {
      packet = null;
    }
    return task.run(packet);
  }

  /**
   * Adds a packet to the worklist of this block's task, marks this as
   * runnable if necessary, and returns the next runnable object to run
   * (the one with the highest priority).
   */
  TaskControlBlock checkPriorityAdd(TaskControlBlock task, Packet packet) {
    if (queue == null) {
      queue = packet;
      markAsRunnable();
      if (priority > task.priority) return this;
    } else {
      queue = packet.addTo(queue);
    }
    return task;
  }

  @Override
  public String toString() {
    return "tcb { "+task+"@"+state+"}";
  }
}

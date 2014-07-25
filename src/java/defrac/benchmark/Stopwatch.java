package defrac.benchmark;

final class Stopwatch {
  private long startTime;

  public void start() {
    startTime = currentTime();
  }

  private long currentTime() {
    return System.currentTimeMillis();
  }

  public int elapsedMilliseconds() {
    return (int)(currentTime() - startTime);
  }
}

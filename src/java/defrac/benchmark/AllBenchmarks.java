package defrac.benchmark;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public final class AllBenchmarks {
  @Nonnull
  private static final DecimalFormat PERCENT = new DecimalFormat("##0.00");

  @Nonnull
  private static final DecimalFormat ERROR = new DecimalFormat("0.0");

  @Nonnull
  private static final DecimalFormat SCORE = new DecimalFormat("########.00");

  @Nonnull
  private static final double[] TABLE = {
      Double.NaN, Double.NaN, 12.71,
      4.30, 3.18, 2.78, 2.57, 2.45, 2.36, 2.31, 2.26, 2.23, 2.20, 2.18, 2.16,
      2.14, 2.13, 2.12, 2.11, 2.10, 2.09, 2.09, 2.08, 2.07, 2.07, 2.06, 2.06,
      2.06, 2.05, 2.05, 2.05, 2.04, 2.04, 2.04, 2.03, 2.03, 2.03, 2.03, 2.03,
      2.02, 2.02, 2.02, 2.02, 2.02, 2.02, 2.02, 2.01, 2.01, 2.01, 2.01, 2.01,
      2.01, 2.01, 2.01, 2.01, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00,
      2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.99, 1.99, 1.99, 1.99, 1.99,
      1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99,
      1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99 };

  public static void main(String[] args) {
    final BenchmarkBase[] benchmarks = {
        new DeltaBlue(),
        new FluidMotion(),
        new Richards(),
        new Tracer(),
        new Havlak(),
    };

    for(final BenchmarkBase benchmark : benchmarks) {
      measure(benchmark);
    }
  }

  private static void measure(@Nonnull final BenchmarkBase benchmark) {
    System.out.println("Running "+benchmark.name+" ...");
    final double[] scores = extractScores(benchmark);
    System.out.println("  - defrac  : "+format(scores, "runs/sec"));
  }

  private static double[] extractScores(@Nonnull final BenchmarkBase benchmark) {
    return extractScores(benchmark, 10);
  }

  private static double[] extractScores(@Nonnull final BenchmarkBase benchmark, final int iterations) {
    final double[] scores = new double[iterations];

    for(int i = 0; i < iterations; ++i) {
      scores[i] = 1.0e6 / benchmark.measure();
    }

    return scores;
  }

  private static String format(@Nonnull final double[] scores, @Nonnull final String metric) {
    final double mean = computeMean(scores);
    final double best = computeBest(scores);
    final String score = SCORE.format(best);
    if(scores.length == 1) {
      return score+" "+metric;
    } else {
      final int n = scores.length;
      final double standardDeviation = computeStandardDeviation(scores, mean);
      final double standardError = standardDeviation / Math.sqrt(n);
      final double percent = (computeTDistribution(n) * standardError / mean) * 100.0;
      final String error = ERROR.format(percent);
      return score+" "+metric+" ("+PERCENT.format(mean)+"±"+error+"%)";
    }
  }


  private static double computeBest(double[] scores) {
    double best = scores[0];
    for(int i = 1; i < scores.length; i++) {
      best = Math.max(best, scores[i]);
    }
    return best;
  }

  private static double computeMean(double[] scores) {
    double sum = 0.0;
    for(double score : scores) {
      sum += score;
    }
    return sum / scores.length;
  }

  private static double computeStandardDeviation(double[] scores, double mean) {
    double deltaSquaredSum = 0.0;
    for(double score : scores) {
      double delta = score - mean;
      deltaSquaredSum += delta * delta;
    }
    double variance = deltaSquaredSum / (scores.length - 1);
    return Math.sqrt(variance);
  }

  private static double computeTDistribution(int n) {
    if (n >= 474) return 1.96;
    else if (n >= 160) return 1.97;
    else if (n >= TABLE.length) return 1.98;
    else return TABLE[n];
  }
}

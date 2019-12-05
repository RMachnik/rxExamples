import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class Completable {

    static class SomeHeavyComputation {
        public void compute() {
            log.info("Computing... ");
            Uninterruptibles.sleepUninterruptibly(1, SECONDS);
            log.info("Computation completed");
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("Starting");
        Executor executor = CompletableFuture.delayedExecutor(1, SECONDS);
        executor.execute(() -> {
            log.info("Submitting now");
            new SomeHeavyComputation().compute();
        });


        CompletableFuture<String> elo = CompletableFuture.supplyAsync(() -> "elo elo elo");
        CompletableFuture.supplyAsync(() -> "alo alo alo")
                .thenCombine(elo, (x, y) -> x + y)
                .thenAcceptBoth(elo, (x, y) -> log.info(y));

        Uninterruptibles.sleepUninterruptibly(5, SECONDS);
    }
}

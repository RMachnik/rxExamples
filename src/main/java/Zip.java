import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Zip {


    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> range = Observable.range(1, 10).delay(10, TimeUnit.MILLISECONDS);
        Observable<Integer> range2 = Observable.range(1, 5);

        Observable.zip(range, range2, (a, b) -> a + b)
                .map(a ->
                {
                    log.info("mapping {}", a);
                    return a.toString();
                })
                .subscribe(a -> log.info("finishing {}", a));

        TimeUnit.SECONDS.sleep(5);
    }
}

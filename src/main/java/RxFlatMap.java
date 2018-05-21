import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RxFlatMap {

    static Random random = new Random();


    public static void main(String[] args) throws InterruptedException {

        Observable.range(0, 100)
                .map(User::new)
                .flatMap(user -> heavyOperation(user)
                        .onErrorReturn(error -> error.getMessage())
                        .subscribeOn(Schedulers.io()), 2)
                .map((s) -> {
                    log.info("mapped {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(loaded -> log.info("finished {}", loaded));

        TimeUnit.SECONDS.sleep(5);
    }

    private static Observable<String> heavyOperation(User user) {
        log.info("heavy operation {}", user.id);
        return Observable.fromCallable(() -> user.loadProfile());
    }

    @Value
    static class User {
        int id;

        public String loadProfile() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(id * 100);
            log.info("Loaded profile {}", id);
            if (id % 4 == 0) throw new RuntimeException("EXCEPTION in " + id);
            return "loaded: " + id;
        }
    }
}

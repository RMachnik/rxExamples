import com.google.common.util.concurrent.Uninterruptibles;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class RxExample {

    static Random random = new Random();

    static Person heavyLoad(UUID uuid) {
        Uninterruptibles.sleepUninterruptibly(1, SECONDS);
        if (random.nextInt(3) % 3 == 0)
            throw new RuntimeException("SomeException");
        return new Person(uuid);
    }

    static Flowable<Person> asyncLoad(UUID uuid) {
        return Flowable.fromCallable(() -> heavyLoad(uuid));
    }

    public static void main(String[] args) {

        Flowable<UUID> source = Flowable.fromCallable(UUID::randomUUID)
                .repeat()
                .take(10);

        Flowable<Person> personFlowable = source
                .flatMap(uuid -> asyncLoad(uuid)
                        .onErrorReturn(throwable -> new Person(null))
                        .subscribeOn(Schedulers.io())
                );

        personFlowable
                .map(p -> p.id)
                .onErrorReturnItem("some error")
                .subscribe(p -> log.info(Thread.currentThread().getName() + "  " + p));

        Uninterruptibles.sleepUninterruptibly(5, SECONDS);


    }

    static class Person {
        String id;

        public Person(UUID uuid) {
            log.info(Thread.currentThread().getName() + " created " + uuid);
        }
    }
}

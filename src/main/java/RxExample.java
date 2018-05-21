import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RxExample {

    static Random random = new Random();

    static Person heavyLoad(UUID uuid) {
        sleepUninteruptably(1);
        if (random.nextInt(3) % 3 == 0)
            throw new RuntimeException("SomeException");
        return new Person(uuid);
    }

    static void sleepUninteruptably(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " ohhh... sleeping error");
        }
    }

    static Flowable<Person> asyncLoad(UUID uuid) {
        return Flowable.fromCallable(() -> heavyLoad(uuid));
    }

    public static void main(String[] args) {

        Flowable<UUID> source = Flowable.fromCallable(UUID::randomUUID)
                .repeat()
                .take(10);

        Flowable<Person> personFlowable = source
                .flatMap(uuid -> asyncLoad(uuid).onErrorReturn(throwable -> new Person(null)).subscribeOn(Schedulers.io()));

        personFlowable
                .map(p -> p.id)
                .onErrorReturnItem("some error")
                .subscribe(p -> System.out.println(Thread.currentThread().getName() + "  " + p));

        sleepUninteruptably(5);

    }

    static class Person {
        String id;

        public Person(UUID uuid) {
            System.out.println(Thread.currentThread().getName() + " created " + uuid);
        }
    }
}

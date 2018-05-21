import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class RxCache {

    public static void main(String[] args) {
        Observable
                .concat(loadFromCache10(1), loadFromCache(1), fromDb(1))
                .firstElement()
                .subscribe(user -> log.info("loaded: {}", user.getId()));
    }


    static Observable<RxFlatMap.User> loadFromCache(final int id) {
        Map<Object, RxFlatMap.User> cache = Arrays.asList(1, 2, 3).stream()
                .collect(Collectors.toMap(Function.identity(), RxFlatMap.User::new));

        return Observable.fromCallable(() -> {
            log.info("cache");
            return cache.get(id);
        });
    }

    static Observable<RxFlatMap.User> loadFromCache10(final int id) {
        Map<Integer, RxFlatMap.User> cache = Map.of(1, new RxFlatMap.User(1));

        return Observable.fromCallable(() -> {
            log.info("cache");
            return cache.get(id);
        });
    }


    static Observable<RxFlatMap.User> fromDb(final int id) {
        return Observable.fromCallable(() -> {
            log.info("db");
            return new RxFlatMap.User(id);
        });
    }
}

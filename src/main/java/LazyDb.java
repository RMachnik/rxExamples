import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class LazyDb {


    public static final int PAGE_SIZE = 10;
    static List<RxFlatMap.User> DB = IntStream
            .range(0, 1000)
            .boxed()
            .map(RxFlatMap.User::new)
            .collect(toList());


    public static void main(String[] args) {

        allUsers(1)
                .take(2)
                .subscribe(users -> log.info("{}", users));

        //interesting usage of scan - reduce with mutable acc
        allUsers(1)
                .scan((a, acc) -> {
                    acc.addAll(a);
                    return acc;
                })
                .take(2)
                .takeLast(1)
                .subscribe(users -> log.info("{}", users));

        allUsersRx2(1)
                .take(2)
                .subscribe(users -> log.info("{}", users));


    }

    static Observable<List<RxFlatMap.User>> allUsers(int startFrom) {
        return Observable
                .defer(() -> Observable.fromArray(loadUsers(startFrom)))
                .concatWith(Observable.defer(() -> allUsers(startFrom + PAGE_SIZE)));
    }

    static Observable<List<RxFlatMap.User>> allUsersRx2(int startFrom) {
        return Observable
                .fromCallable(() -> loadUsers(startFrom))
                .concatWith(allUsersRx2(startFrom + PAGE_SIZE));
    }


    static List<RxFlatMap.User> loadUsers(int startFrom) {
        return DB.subList(startFrom, startFrom + PAGE_SIZE);
    }


}

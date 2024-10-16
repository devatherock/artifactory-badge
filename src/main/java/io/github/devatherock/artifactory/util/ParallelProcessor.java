package io.github.devatherock.artifactory.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ParallelProcessor {
    private final Executor executor;
    private final Semaphore semaphore; // To limit parallelism in case of outbound API calls

    public <T> List<T> parallelProcess(List<Supplier<T>> suppliers) {
        Map<Integer, T> result = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(suppliers.size());

        try {
            for (int index = 0; index < suppliers.size(); index++) {
                int currentIndex = index;

                semaphore.acquire();
                executor.execute(() -> {
                    try {
                        LOGGER.trace("In parallelProcess. Index: {}", currentIndex);
                        result.put(currentIndex, suppliers.get(currentIndex).get());
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                semaphore.release();
            }

            countDownLatch.await();
        } catch (InterruptedException exception) {
            LOGGER.warn("Exception during parallel processing", exception);
        }

        return result.entrySet()
                .stream()
                .sorted(Entry.comparingByKey(Comparator.naturalOrder()))
                .map(Entry::getValue)
                .collect(Collectors.toList());
    }
}

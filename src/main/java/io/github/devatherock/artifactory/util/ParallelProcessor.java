package io.github.devatherock.artifactory.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import io.github.devatherock.artifactory.config.AppProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ParallelProcessor {
    private final Executor executor;
    private final AppProperties config;

    public <T> List<T> parallelProcess(List<Supplier<T>> suppliers) {
        Map<Integer, Optional<T>> result = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(suppliers.size());
        Semaphore semaphore = new Semaphore(config.getParallelism()); // To limit parallelism in case of outbound API
                                                                      // calls

        try {
            for (int index = 0; index < suppliers.size(); index++) {
                int currentIndex = index;

                semaphore.acquire();
                executor.execute(() -> {
                    try {
                        LOGGER.trace("In parallelProcess. Index: {}", currentIndex);
                        T currentResult = suppliers.get(currentIndex).get();
                        result.put(currentIndex, currentResult != null ? Optional.of(currentResult) : Optional.empty());
                    } catch (Exception exception) {
                        LOGGER.warn("Exception while processing index {}", currentIndex, exception);
                        result.put(currentIndex, Optional.empty());
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
                .map(entry -> entry.getValue().orElse(null))
                .toList();
    }
}

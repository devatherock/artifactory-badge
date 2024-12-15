package io.github.devatherock.artifactory.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

import io.github.devatherock.artifactory.config.AppProperties

import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@code ParallelProcessor}
 */
class ParallelProcessorSpec extends Specification {
    Executor executor = Executors.newSingleThreadExecutor()

    @Subject
    ParallelProcessor processor = new ParallelProcessor(executor, new AppProperties(parallelism: 1))

    void 'test parallel process - interrupted exception'() {
        given:
        def suppliers = (1..5).collect { number ->
            (Supplier<Long>) () -> {
                    sleep(1000)
                    return number * number
            }
        }

        when:
        Thread thread = Thread.start { processor.parallelProcess(suppliers) }
        Thread.sleep(500)
        thread.interrupt()

        then:
        noExceptionThrown()
    }

    void 'test parallel process - output order'() {
        given:
        Random random = new Random()
        def suppliers = (1..5).collect { number ->
            (Supplier<Long>) () -> {
                Thread.sleep(random.nextLong(50, 200))
                return number * number
            }
        }

        when:
        def output = processor.parallelProcess(suppliers)

        then:
        output == [1, 4, 9, 16, 25]
    }

    void 'test parallel process - null result'() {
        given:
        def suppliers = (0..1).collect { number ->
            (Supplier<Double>) () -> {
                return number == 0 ? null : 0
            }
        }

        when:
        def output = processor.parallelProcess(suppliers)

        then:
        output.size() == 2
        output == [null, 0]
    }

    void 'test parallel process - exception while processing'() {
        given:
        def suppliers = ['1', 'abc'].collect { number ->
            (Supplier<Integer>) () -> {
                return Integer.parseInt(number)
            }
        }

        when:
        def output = processor.parallelProcess(suppliers)

        then:
        output.size() == 2
        output == [1, null]
    }
}

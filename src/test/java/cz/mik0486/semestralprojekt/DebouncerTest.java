package cz.mik0486.semestralprojekt;

import cz.mik0486.semestralproject.utils.Debouncer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebouncerTest {

    @Test
    void invokesCallbackAfterDelay() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Debouncer<Integer> debouncer = new Debouncer<>(200, counter::set);
        debouncer.call(5);
        Thread.sleep(300);
        assertEquals(5, counter.get());
    }

    @Test
    void resetsDelayOnSubsequentCalls() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Debouncer<Integer> debouncer = new Debouncer<>(200, counter::set);
        debouncer.call(5);
        Thread.sleep(100);
        debouncer.call(10);
        Thread.sleep(300);
        assertEquals(10, counter.get());
    }

    @Test
    void doesNotInvokeCallbackIfCancelled() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Debouncer<Integer> debouncer = new Debouncer<>(200, counter::set);
        debouncer.call(5);
        Thread.sleep(100);
        debouncer.call(10);
        Thread.sleep(100);
        assertEquals(0, counter.get());
    }

    @Test
    void handlesNullCallbackArgument() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Debouncer<Integer> debouncer = new Debouncer<>(200, arg -> counter.set(arg == null ? -1 : arg));
        debouncer.call(null);
        Thread.sleep(300);
        assertEquals(-1, counter.get());
    }

    @Test
    void doesNotThrowExceptionForZeroDelay() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Debouncer<Integer> debouncer = new Debouncer<>(0, counter::set);
        debouncer.call(5);
        Thread.sleep(100);
        assertEquals(5, counter.get());
    }
}

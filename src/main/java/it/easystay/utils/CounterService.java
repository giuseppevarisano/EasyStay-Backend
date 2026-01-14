package it.easystay.utils;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CounterService {
    private int count = 0;

    public int incrementAndGet() {
        return ++count;
    }

    public int getCount() {
        return count;
    }
}
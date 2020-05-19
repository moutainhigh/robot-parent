package com.robot.core.chain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author mrt
 * @Date 2020/5/18 19:20
 * @Version 2.0
 */
public abstract class Invoker {
    public abstract <T> boolean invoke(T invocation) throws Exception;

    public static final Invoker buildInvokerChain(List<? extends Filter> filters) {
        sortFilters(filters);
        Invoker last = null;
        if (!filters.isEmpty())
            for (int i = filters.size() - 1; i >= 0; i--) {
                final Filter filter = filters.get(i);
                final Invoker next = last;
                last = new Invoker() {
                    @Override
                    public <T> boolean invoke(T invocation) throws Exception {
                        return filter.doFilterFinal(next, invocation);
                    }
                };
            }
        return last;
    }

    private static void sortFilters(List<? extends Filter> filters) {
        Collections.sort(filters, new Comparator<Filter>() {
            @Override
            public int compare(Filter o1, Filter o2) {
                return o1.order() - o2.order();
            }
        });
    }
}

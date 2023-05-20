package io.github.poshjosh.ratelimiter.web.spring.weblayertests.performance;

import java.util.ArrayList;
import java.util.List;

public final class ResourceLimiterUsageRecorder {
    private static final Object mutex = new Object();
    private static final List<Usage> usages = new ArrayList<>();

    private ResourceLimiterUsageRecorder() { }

    public static void record(Usage usage) {
        synchronized (mutex) {
            usages.add(usage);
        }
    }

    public static void clearUsages() {
        synchronized (mutex) {
            usages.clear();
        }
    }

    public static List<Usage> getUsages() {
        synchronized (mutex) {
            return usages;
        }
    }
}

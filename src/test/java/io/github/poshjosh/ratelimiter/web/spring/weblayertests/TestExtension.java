package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.web.spring.weblayertests.performance.Usage;
import io.github.poshjosh.ratelimiter.web.spring.weblayertests.performance.ResourceLimiterUsageRecorder;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class TestExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            beforeAllTests();
            context.getRoot().getStore(GLOBAL).put(TestExtension.class.getName(), this);
        }
    }

    private void beforeAllTests() {
        System.out.println("\n========================\n BEFORE ALL TESTS \n========================\n");
        ResourceLimiterUsageRecorder.clearUsages();
    }

    @Override
    public void close() {
        afterAllTests();
    }

    private void afterAllTests() {
        System.out.println("\n========================\n  AFTER ALL TESTS \n========================\n");
        Usage totalUsage = Usage.of(0, 0);
        final List<Usage> usageList = ResourceLimiterUsageRecorder.getUsages();
        for(Usage usage : usageList) {
            totalUsage = totalUsage.add(usage);
        }
        final Usage averageUsage = totalUsage.divideBy(usageList.size());
        System.out.println("ResourceLimiter average " + averageUsage + "\n");
    }
}
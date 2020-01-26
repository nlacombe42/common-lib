package net.nlacombe.commonlib.util;

public class ThreadUtil {

    public static void sleepWithRuntimeInterrupts(long sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

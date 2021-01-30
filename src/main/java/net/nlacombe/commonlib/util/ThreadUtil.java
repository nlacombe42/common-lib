package net.nlacombe.commonlib.util;


import net.nlacombe.commonlib.exception.RuntimeInterruptedException;

public class ThreadUtil {

    public static void sleepWithRuntimeInterrupts(long sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

}

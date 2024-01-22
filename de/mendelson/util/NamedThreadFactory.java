//$Header: /as2/de/mendelson/util/NamedThreadFactory.java 3     24/02/22 14:40 Heller $
package de.mendelson.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Thread factory that allows to name created threads - useful to verify the
 * program running state using jconsole
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger threadPoolNumber = new AtomicInteger(0);
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePattern;
    private int priority = Thread.NORM_PRIORITY;
    private final ThreadGroup group;
    private final int currentThreadPoolNumber;

    /**
     * Create a names thread factory that creates threads of the format suffix-n
     *
     * @param suffix Suffix for the created threads
     */
    public NamedThreadFactory(String suffix) {
        this(suffix, Thread.NORM_PRIORITY);
    }

    /**
     * Create a names thread factory that creates threads of the format suffix-n
     *
     * @param suffix Suffix for the created threads
     * @param priority Thread priority from 1(min)..10(max). Its also possible
     * to use the constants Thread.MIN_PRIORITY(1), Thread.NORM_PRIORITY(5),
     * Thread.MAX_PRIORITY(10)
     */
    public NamedThreadFactory(String suffix, int priority) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            this.group = securityManager.getThreadGroup();
        } else {
            this.group = Thread.currentThread().getThreadGroup();
        }
        //format is suffix-poolNo-thread-threadnumber
        this.namePattern = suffix + "-%d-thread-%d";
        this.priority = priority;
        //the thread pool number is a system wide unique number
        this.currentThreadPoolNumber = threadPoolNumber.addAndGet(1);
    }

    /**
     * A new Thread is requested in the thread group
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(this.group, runnable,
                String.format(this.namePattern,
                        this.currentThreadPoolNumber,
                        this.threadNumber.addAndGet(1)));
        thread.setPriority(this.priority);
        return (thread);
    }
}

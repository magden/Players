package org.sport.players.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JvmAnalyzer {

    public static void runJvmMemoryAnalyze(String stage){
        log.info(stage);
        Runtime runtime = Runtime.getRuntime();
        // Trigger garbage collection to get a more accurate measurement
        runtime.gc();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        log.info("Total Memory: {} MB", totalMemory / (1024 * 1024));
        log.info("Free Memory: {} MB", freeMemory / (1024 * 1024));
        log.info("Used Memory: {} MB", usedMemory / (1024 * 1024));
    }
}

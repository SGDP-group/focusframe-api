package com.focusframe.focusframe_api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logging utility class for the application.
 * Provides static methods for logging at different levels without needing to instantiate a Logger.
 * All logs are automatically written to logs/application.log via Logback configuration.
 * 
 * Usage:
 *   LoggerUtil.info("ClassName|methodName|message");
 *   LoggerUtil.error("ClassName|methodName|error message", exception);
 *   LoggerUtil.warn("ClassName|methodName|warning message");
 *   LoggerUtil.debug("ClassName|methodName|debug message");
 */
public class LoggerUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    
    /**
     * Log an info level message.
     * @param message the message to log
     */
    public static void info(String message) {
        logger.info(message);
    }
    
    /**
     * Log an info level message with an exception.
     * @param message the message to log
     * @param exception the exception to log
     */
    public static void info(String message, Exception exception) {
        logger.info(message, exception);
    }
    
    /**
     * Log an error level message.
     * @param message the message to log
     */
    public static void error(String message) {
        logger.error(message);
    }
    
    /**
     * Log an error level message with an exception and stack trace.
     * @param message the message to log
     * @param exception the exception to log with stack trace
     */
    public static void error(String message, Exception exception) {
        logger.error(message, exception);
    }
    
    /**
     * Log an error level message with an exception and stack trace.
     * @param message the message to log
     * @param exception the throwable to log with stack trace
     */
    public static void error(String message, Throwable exception) {
        logger.error(message, exception);
    }
    
    /**
     * Log a warning level message.
     * @param message the message to log
     */
    public static void warn(String message) {
        logger.warn(message);
    }
    
    /**
     * Log a warning level message with an exception.
     * @param message the message to log
     * @param exception the exception to log
     */
    public static void warn(String message, Exception exception) {
        logger.warn(message, exception);
    }
    
    /**
     * Log a debug level message.
     * @param message the message to log
     */
    public static void debug(String message) {
        logger.debug(message);
    }
    
    /**
     * Log a debug level message with an exception.
     * @param message the message to log
     * @param exception the exception to log
     */
    public static void debug(String message, Exception exception) {
        logger.debug(message, exception);
    }
    
    /**
     * Log a trace level message.
     * @param message the message to log
     */
    public static void trace(String message) {
        logger.trace(message);
    }
}

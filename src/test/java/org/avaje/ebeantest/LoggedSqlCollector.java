package org.avaje.ebeantest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * Helper that can collect the SQL that is logged via SLF4J.
 * <p>
 * Used {@link #start()} and {@link #stop()} to collect the logged messages that contain the
 * executed SQL statements.
 * <p>
 * Internally this uses a Logback Appender to collect messages for org.avaje.ebean.SQL.
 */
public class LoggedSqlCollector {

  private static BasicAppender basicAppender = new BasicAppender();

  static {

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    basicAppender.setContext(lc);

    Logger logger = (Logger) LoggerFactory.getLogger("org.avaje.ebean.SQL");
    logger.addAppender(basicAppender);
    logger.setLevel(Level.TRACE);
    logger.setAdditive(true);
  }

  /**
   * Start collection of the logged SQL statements.
   */
  public static List<String> start() {
    return basicAppender.collectStart();
  }

  /**
   * Stop collection of the logged SQL statements return the list of captured messages that contain
   * the SQL.
   */
  public static List<String> stop() {
    return basicAppender.collectEnd();
  }

  private static class BasicAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    List<String> messages = new ArrayList<String>();

    @Override
    protected void append(ILoggingEvent eventObject) {
      if (started) {
        messages.add(eventObject.getMessage());
      }
    }

    /**
     * Start collection.
     */
    List<String> collectStart() {
      List<String> tempMessages = messages;
      messages = new ArrayList<String>();
      // set started flag
      start();
      return tempMessages;
    }

    /**
     * End collection.
     */
    List<String> collectEnd() {
      // set stopped state
      stop();
      List<String> tempMessages = messages;
      messages = new ArrayList<String>();
      return tempMessages;
    }

  }
}

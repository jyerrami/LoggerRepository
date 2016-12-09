package logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;

public class UserLogger {
	
	/**
	 * class to store context keys
	 */
	public static enum LoggerKey{
		USER_ID("UserID"),
		TRACE_ID("TraceID");
		
		private String stringValue_;
		
		LoggerKey(String key){
			stringValue_ = key;
		}
		
		public String toString(){
			return stringValue_;
		}
	}
	private static final String configUserLoggerName = "UserLoggerName";

	/**
   * Name of the logger to be used as the user logger. Defaults to the canonical
   * class name of this class if not specified in the configuration properties
   * file.
   */
  private static String userLoggerName_ = Logger.class.getCanonicalName();

  /**
   * Internal key used to indicate whether user level logging was requested.
   */
  private static final String contextUserLoggerRequested_ = "UserLoggerRequested";
	  
	Logger rootLogger_ ;
	Logger userLogger_ ;
	
	/**
	 * Initialize all the loggers
	 */
	public UserLogger(){
		rootLogger_ = org.apache.logging.log4j.LogManager.getLogger(UserLogger.class);
		LoggerContext aLoggerContext = (LoggerContext)LogManager.getContext();
		userLoggerName_ = aLoggerContext.getConfiguration().getStrSubstitutor().getVariableResolver().lookup(configUserLoggerName);
		userLogger_ = org.apache.logging.log4j.LogManager.getLogger(userLoggerName_);
	}
	
	/*
	 * Return handle to rootLogger
	 */
	public Logger getRootLogger(){
		return rootLogger_ ; 
	}
	
	/**
	 * Return handle to User Logger
	 * @return
	 */
	public Logger getUserLogger(){
		return userLogger_ ;
	}
	
	/**
	 * Store information to be made available for the logging output.
	 * 
	 * @param key a field defined in ContextKey to store value under
	 * 
	 * @param value the value to store
	 */
	public static void putContext(LoggerKey key, Object value) {
		ThreadContext.put(key.toString(), value.toString());
	}

	/**
	 * Indicates if user logger was requested
	 * @return
	 */
  public static boolean isUserLoggingRequested() {
		return ThreadContext.get(contextUserLoggerRequested_ ) != null;
  }
	
  /**
   * Userlevel logging to be turned off or on. 
   * @param enabled
   */
  public static void setUserLoggingRequested(boolean enabled) {
    if (enabled) {
      ThreadContext.put(contextUserLoggerRequested_, contextUserLoggerRequested_);
    } else {
      ThreadContext.remove(contextUserLoggerRequested_);
    }
  }
  
  /**
   * Remove all context information previously stored This should <b>always</b>
   * be called at the exit point of a flow.
   */
  public static void clearContext() {
  	ThreadContext.remove(contextUserLoggerRequested_);
  	for(LoggerKey key: LoggerKey.values()){
  		ThreadContext.remove(key.toString());
  	}
  }
  
	public static void main(String[] args) {
		UserLogger aUserLogger = new UserLogger();
		
		Logger rootLogger = aUserLogger.getRootLogger();
		rootLogger.debug("Hi");
		
		// set the user logger based on the value sent in.
		// values are hard coded here. But in web service, it can be sent in the request header.
		// 
		String userName = "foo";
		boolean traceFlag = true;
		
		if(traceFlag){
			UserLogger.putContext(UserLogger.LoggerKey.USER_ID, userName);
			UserLogger.putContext(UserLogger.LoggerKey.TRACE_ID, Thread.currentThread().getName());
			UserLogger.setUserLoggingRequested(true);
		}
		
		Logger userLogger = aUserLogger.getUserLogger();
		userLogger.trace("Hello from user logger");
		
		// done set the flag to false
		UserLogger.setUserLoggingRequested(false);
		
		UserLogger.clearContext();
	}
}

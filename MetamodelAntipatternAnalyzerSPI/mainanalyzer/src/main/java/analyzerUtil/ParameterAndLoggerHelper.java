package analyzerUtil;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class ParameterAndLoggerHelper {

	public static void setLoggerLevel(int loggerLevel) {
		Level level = switch (loggerLevel) {
			case 0 -> Level.TRACE;
			case 1 -> Level.DEBUG;
			case 2 -> Level.INFO;
			case 3 -> Level.WARN;
			case 4 -> Level.ERROR;
			case 5 -> Level.FATAL;
			default -> Level.WARN;
		};
		ParameterAndLoggerHelper.setLoggerLevel(level);
	}
	
	public static void setLoggerLevel(Level level) {		
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(level);
		context.updateLoggers();
	}
}

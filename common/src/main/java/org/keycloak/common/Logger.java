package org.keycloak.common;

import java.util.logging.Level;

public class Logger{
	
	private java.util.logging.Logger inner = null;
	
	private Logger(java.util.logging.Logger in) {
		this.inner = in;
		this.name = "";
	}
	
	public static Logger getLogger(String type) {
        return new Logger(java.util.logging.Logger.getLogger(type));
	}
	
    private static final String FQCN = Logger.class.getName();

    private final String name;

    /**
     * Construct a new instance.
     *
     * @param name the logger category name
     */
    protected Logger(final String name) {
        this.name = name;
    }

    /**
     * Return the name of this logger.
     *
     * @return The name of this logger.
     */
    public String getName() {
        return name;
    }
    
    private boolean isEnabled(Level level) {
    	return true;
    }

    /**
     * Implementation log method (standard parameter formatting).
     *
     * @param level the level
     * @param loggerClassName the logger class name
     * @param message the message to log
     * @param parameters the parameters of the message
     * @param thrown the exception which was thrown, if any
     */
    protected void doLog(Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
    	inner.log(level, message.toString(), parameters);
    }

    /**
     * Implementation log method (printf formatting).
     *
     * @param level the level
     * @param loggerClassName the logger class name
     * @param format the format string to log
     * @param parameters the parameters of the message
     * @param thrown the exception which was thrown, if any
     */
    protected void doLogf(Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
    	inner.log(level, format, parameters);
    };

    /**
     * Check to see if the {@code TRACE} level is enabled for this logger.
     *
     * @return {@code true} if messages logged at {@link Level#TRACE} may be accepted, {@code false} otherwise
     */
    public boolean isTraceEnabled() {
        return isEnabled(Level.FINEST);
    }

    /**
     * Issue a log message with a level of TRACE.
     *
     * @param message the message
     */
    public void trace(Object message) {
        doLog(Level.FINEST, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of TRACE.
     *
     * @param message the message
     * @param t the throwable
     */
    public void trace(Object message, Throwable t) {
        doLog(Level.FINEST, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of TRACE and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void trace(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.FINEST, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of TRACE.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #tracev(String, Object...)} is recommended.
     */
    @Deprecated
    public void trace(Object message, Object[] params) {
        doLog(Level.FINEST, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of TRACE.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #tracev(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void trace(Object message, Object[] params, Throwable t) {
        doLog(Level.FINEST, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of TRACE.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.FINEST, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void tracev(String format, Object... params) {
        doLog(Level.FINEST, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void tracev(String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracev(String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracev(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void tracev(Throwable t, String format, Object... params) {
        doLog(Level.FINEST, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void tracev(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracev(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void tracef(String format, Object... params) {
        doLogf(Level.FINEST, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void tracef(String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracef(String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracef(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void tracef(Throwable t, String format, Object... params) {
        doLogf(Level.FINEST, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void tracef(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracef(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    public void tracef(final String format, final int arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, null);
        }
    }

    public void tracef(final String format, final int arg1, final int arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void tracef(final String format, final int arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void tracef(final String format, final int arg1, final int arg2, final int arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final String format, final int arg1, final int arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final String format, final int arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg1, final int arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg1, final int arg2, final int arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg1, final int arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final int arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void tracef(final String format, final long arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, null);
        }
    }

    public void tracef(final String format, final long arg1, final long arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void tracef(final String format, final long arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void tracef(final String format, final long arg1, final long arg2, final long arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final String format, final long arg1, final long arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final String format, final long arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg1, final long arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg1, final long arg2, final long arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg1, final long arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void tracef(final Throwable t, final String format, final long arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    /**
     * Check to see if the {@code DEBUG} level is enabled for this logger.
     *
     * @return {@code true} if messages logged at {@link Level#DEBUG} may be accepted, {@code false} otherwise
     */
    public boolean isDebugEnabled() {
        return isEnabled(Level.FINEST);
    }

    /**
     * Issue a log message with a level of DEBUG.
     *
     * @param message the message
     */
    public void debug(Object message) {
        doLog(Level.FINEST, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of DEBUG.
     *
     * @param message the message
     * @param t the throwable
     */
    public void debug(Object message, Throwable t) {
        doLog(Level.FINEST, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of DEBUG and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void debug(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.FINEST, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of DEBUG.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #debugv(String, Object...)} is recommended.
     */
    @Deprecated
    public void debug(Object message, Object[] params) {
        doLog(Level.FINEST, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of DEBUG.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #debugv(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void debug(Object message, Object[] params, Throwable t) {
        doLog(Level.FINEST, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of DEBUG.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.FINEST, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void debugv(String format, Object... params) {
        doLog(Level.FINEST, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void debugv(String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugv(String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void debugv(Throwable t, String format, Object... params) {
        doLog(Level.FINEST, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void debugv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLog(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void debugf(String format, Object... params) {
        doLogf(Level.FINEST, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void debugf(String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugf(String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void debugf(Throwable t, String format, Object... params) {
        doLogf(Level.FINEST, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void debugf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    public void debugf(final String format, final int arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, null);
        }
    }

    public void debugf(final String format, final int arg1, final int arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void debugf(final String format, final int arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void debugf(final String format, final int arg1, final int arg2, final int arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final String format, final int arg1, final int arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final String format, final int arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg1, final int arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg1, final int arg2, final int arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg1, final int arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final int arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void debugf(final String format, final long arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, null);
        }
    }

    public void debugf(final String format, final long arg1, final long arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void debugf(final String format, final long arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, null);
        }
    }

    public void debugf(final String format, final long arg1, final long arg2, final long arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final String format, final long arg1, final long arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final String format, final long arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg1, final long arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg1, final Object arg2) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg1, final long arg2, final long arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg1, final long arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    public void debugf(final Throwable t, final String format, final long arg1, final Object arg2, final Object arg3) {
        if (isEnabled(Level.FINEST)) {
            doLogf(Level.FINEST, FQCN, format, new Object[] { arg1, arg2, arg3 }, t);
        }
    }

    /**
     * Check to see if the {@code INFO} level is enabled for this logger.
     *
     * @return {@code true} if messages logged at {@link Level#INFO} may be accepted, {@code false} otherwise
     */
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    /**
     * Issue a log message with a level of INFO.
     *
     * @param message the message
     */
    public void info(Object message) {
        doLog(Level.INFO, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of INFO.
     *
     * @param message the message
     * @param t the throwable
     */
    public void info(Object message, Throwable t) {
        doLog(Level.INFO, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of INFO and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void info(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.INFO, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of INFO.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #infov(String, Object...)} is recommended.
     */
    @Deprecated
    public void info(Object message, Object[] params) {
        doLog(Level.INFO, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of INFO.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #infov(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void info(Object message, Object[] params, Throwable t) {
        doLog(Level.INFO, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of INFO.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.INFO, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void infov(String format, Object... params) {
        doLog(Level.INFO, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void infov(String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infov(String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infov(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void infov(Throwable t, String format, Object... params) {
        doLog(Level.INFO, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void infov(Throwable t, String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infov(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLog(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void infof(String format, Object... params) {
        doLogf(Level.INFO, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void infof(String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infof(String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infof(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void infof(Throwable t, String format, Object... params) {
        doLogf(Level.INFO, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void infof(Throwable t, String format, Object param1) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infof(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.INFO)) {
            doLogf(Level.INFO, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a log message with a level of WARN.
     *
     * @param message the message
     */
    public void warn(Object message) {
        doLog(Level.WARNING, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of WARN.
     *
     * @param message the message
     * @param t the throwable
     */
    public void warn(Object message, Throwable t) {
        doLog(Level.WARNING, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of WARN and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void warn(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.WARNING, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of WARN.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #warnv(String, Object...)} is recommended.
     */
    @Deprecated
    public void warn(Object message, Object[] params) {
        doLog(Level.WARNING, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of WARN.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #warnv(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void warn(Object message, Object[] params, Throwable t) {
        doLog(Level.WARNING, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of WARN.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.WARNING, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void warnv(String format, Object... params) {
        doLog(Level.WARNING, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void warnv(String format, Object param1) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnv(String format, Object param1, Object param2) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void warnv(Throwable t, String format, Object... params) {
        doLog(Level.WARNING, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void warnv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARNING)) {
            doLog(Level.WARNING, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void warnf(String format, Object... params) {
        doLogf(Level.WARNING, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void warnf(String format, Object param1) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnf(String format, Object param1, Object param2) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void warnf(Throwable t, String format, Object... params) {
        doLogf(Level.WARNING, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void warnf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.WARNING)) {
            doLogf(Level.WARNING, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a log message with a level of ERROR.
     *
     * @param message the message
     */
    public void error(Object message) {
        doLog(Level.SEVERE, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of ERROR.
     *
     * @param message the message
     * @param t the throwable
     */
    public void error(Object message, Throwable t) {
        doLog(Level.SEVERE, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of ERROR and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void error(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.SEVERE, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of ERROR.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #errorv(String, Object...)} is recommended.
     */
    @Deprecated
    public void error(Object message, Object[] params) {
        doLog(Level.SEVERE, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of ERROR.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #errorv(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void error(Object message, Object[] params, Throwable t) {
        doLog(Level.SEVERE, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of ERROR.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.SEVERE, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void errorv(String format, Object... params) {
        doLog(Level.SEVERE, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void errorv(String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorv(String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void errorv(Throwable t, String format, Object... params) {
        doLog(Level.SEVERE, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void errorv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void errorf(String format, Object... params) {
        doLogf(Level.SEVERE, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void errorf(String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorf(String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void errorf(Throwable t, String format, Object... params) {
        doLogf(Level.SEVERE, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void errorf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a log message with a level of FATAL.
     *
     * @param message the message
     */
    public void fatal(Object message) {
        doLog(Level.SEVERE, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable with a level of FATAL.
     *
     * @param message the message
     * @param t the throwable
     */
    public void fatal(Object message, Throwable t) {
        doLog(Level.SEVERE, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable with a level of FATAL and a specific logger class name.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void fatal(String loggerFqcn, Object message, Throwable t) {
        doLog(Level.SEVERE, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters with a level of FATAL.
     *
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #fatalv(String, Object...)} is recommended.
     */
    @Deprecated
    public void fatal(Object message, Object[] params) {
        doLog(Level.SEVERE, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of FATAL.
     *
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #fatalv(Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void fatal(Object message, Object[] params, Throwable t) {
        doLog(Level.SEVERE, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of FATAL.
     *
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
        doLog(Level.SEVERE, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param params the parameters
     */
    public void fatalv(String format, Object... params) {
        doLog(Level.SEVERE, FQCN, format, params, null);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void fatalv(String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalv(String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalv(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void fatalv(Throwable t, String format, Object... params) {
        doLog(Level.SEVERE, FQCN, format, params, t);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void fatalv(Throwable t, String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalv(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     *
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLog(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void fatalf(String format, Object... params) {
        doLogf(Level.SEVERE, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void fatalf(String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalf(String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalf(String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void fatalf(Throwable t, String format, Object... params) {
        doLogf(Level.SEVERE, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void fatalf(Throwable t, String format, Object param1) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalf(Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     *
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(Level.SEVERE)) {
            doLogf(Level.SEVERE, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Log a message at the given level.
     *
     * @param level the level
     * @param message the message
     */
    public void log(Level level, Object message) {
        doLog(level, FQCN, message, null, null);
    }

    /**
     * Issue a log message and throwable at the given log level.
     *
     * @param level the level
     * @param message the message
     * @param t the throwable
     */
    public void log(Level level, Object message, Throwable t) {
        doLog(level, FQCN, message, null, t);
    }

    /**
     * Issue a log message and throwable at the given log level and a specific logger class name.
     *
     * @param level the level
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void log(Level level, String loggerFqcn, Object message, Throwable t) {
        doLog(level, loggerFqcn, message, null, t);
    }

    /**
     * Issue a log message with parameters at the given log level.
     *
     * @param level the level
     * @param message the message
     * @param params the message parameters
     * @deprecated To log a message with parameters, using {@link #logv(Level, String, Object...)} is recommended.
     */
    @Deprecated
    public void log(Level level, Object message, Object[] params) {
        doLog(level, FQCN, message, params, null);
    }

    /**
     * Issue a log message with parameters and a throwable at the given log level.
     *
     * @param level the level
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     * @deprecated To log a message with parameters, using {@link #logv(Level, Throwable, String, Object...)} is recommended.
     */
    @Deprecated
    public void log(Level level, Object message, Object[] params, Throwable t) {
        doLog(level, FQCN, message, params, t);
    }

    /**
     * Issue a log message with parameters and a throwable at the given log level.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
        doLog(level, loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(Level level, String format, Object... params) {
        doLog(level, FQCN, format, params, null);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(Level level, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(Level level, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(Level level, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(Level level, Throwable t, String format, Object... params) {
        doLog(level, FQCN, format, params, t);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        doLog(level, loggerFqcn, format, params, t);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLog(level, loggerFqcn, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void logf(Level level, String format, Object... params) {
        doLogf(level, FQCN, format, params, null);
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void logf(Level level, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1 }, null);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(Level level, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1, param2 }, null);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(Level level, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1, param2, param3 }, null);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void logf(Level level, Throwable t, String format, Object... params) {
        doLogf(level, FQCN, format, params, t);
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     *
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, FQCN, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Log a message at the given level.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[] { param1 }, t);
        }
    }

    /**
     * Log a message at the given level.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[] { param1, param2 }, t);
        }
    }

    /**
     * Log a message at the given level.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (isEnabled(level)) {
            doLogf(level, loggerFqcn, format, new Object[] { param1, param2, param3 }, t);
        }
    }

    /**
     * Log a message at the given level.
     *
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the message parameters
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        doLogf(level, loggerFqcn, format, params, t);
    }

    /**
     * Get a Logger instance given the logger name with the given suffix.
     * <p/>
     * <p>This will include a logger separator between logger name and suffix.
     *
     * @param name the logger name
     * @param suffix a suffix to append to the logger name
     *
     * @return the logger
     */
    public static Logger getLogger(String name, String suffix) {
        return getLogger(name == null || name.length() == 0 ? suffix : name + "." + suffix);
    }

    /**
     * Get a Logger instance given the name of a class. This simply calls create(clazz.getName()).
     *
     * @param clazz the Class whose name will be used as the logger name
     *
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Get a Logger instance given the name of a class with the given suffix.
     * <p/>
     * <p>This will include a logger separator between logger name and suffix
     *
     * @param clazz the Class whose name will be used as the logger name
     * @param suffix a suffix to append to the logger name
     *
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz, String suffix) {
        return getLogger(clazz.getName(), suffix);
    }

    /**
     * Get a typed logger which implements the given interface.  The current default locale will be used for the new logger.
     *
     * @param type the interface to implement
     * @param category the logger category
     * @param <T> the logger type
     * @return the typed logger
     */
    public static <T> T getMessageLogger(Class<T> type, String category) {
        return getMessageLogger(type, category);
    }


    private static String join(String interfaceName, String a, String b, String c, String d) {
        final StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }
	
	

}

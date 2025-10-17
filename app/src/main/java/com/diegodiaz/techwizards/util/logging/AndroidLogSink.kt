package com.diegodiaz.techwizards.util.logging

import android.util.Log

/**
 * Sink que escribe a Logcat usando android.util.Log.
 */
class AndroidLogSink : LogSink {
    override fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            "VERBOSE" -> if (throwable != null) Log.v(tag, message, throwable) else Log.v(tag, message)
            "DEBUG"   -> if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
            "INFO"    -> if (throwable != null) Log.i(tag, message, throwable) else Log.i(tag, message)
            "WARN"    -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
            "ERROR"   -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
            else      -> if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
        }
    }
}

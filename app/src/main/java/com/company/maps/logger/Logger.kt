package com.company.maps.logger

import android.content.Intent
import android.util.Log

object Logger {
    fun log(message: String) {
        val caller = Thread.currentThread().stackTrace[1]
        Log.d("${caller.className}::${caller.methodName}", message)
    }

    fun logIntent(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (intent != null) {
            log("Intent finished: request code: '$requestCode', resultcode $resultCode, intent: ${intent.extras}")
        }
        log("Intent finished: request code: '$requestCode', resultcode $resultCode")
    }
}
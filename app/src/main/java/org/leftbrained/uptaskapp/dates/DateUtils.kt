package org.leftbrained.uptaskapp.dates

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

object DateUtils {
    fun instantToDate(dueDate: Instant?): String {
        return dueDate!!.toLocalDateTime(TimeZone.UTC).let {
            "${it.dayOfMonth} ${
                it.month.name.let { name ->
                    name.substring(0, 1)
                        .uppercase(Locale.ROOT) + name.substring(1)
                        .lowercase(Locale.ROOT)
                }
            }, ${it.year} ${it.hour}:${it.minute}"
        }
    }
}
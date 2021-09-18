package org.indiv.dls.onerepmax.data.db

import java.util.UUID
import javax.inject.Inject

class IdGenerator @Inject constructor() {
    fun exerciseId(): String = create()
    fun exerciseDayId(): String = create()
    fun exerciseDayEntryId(): String = create()

    private fun create(): String {
        return UUID.randomUUID().toString()
    }
}

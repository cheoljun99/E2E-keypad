package bob.e2e.dto

import java.time.LocalDateTime
import java.util.*

data class KeypadResponseDto(
    val id: UUID,
    val keyList: List<String>,
    val keypadImage: String,
    val time: LocalDateTime
)




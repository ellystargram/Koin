package bot.koin.dto

import java.time.LocalDateTime

data class UserDTO(
    val id: Long,
    val joinedDate: LocalDateTime
);
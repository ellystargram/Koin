package bot.koin.dto

import java.time.LocalDateTime

data class WalletDTO(
    val id: Long,
    val address: String,
    val userId: Long,
    val exchangerId: Long,
    val createdDate: LocalDateTime
)

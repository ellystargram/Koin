package bot.koin.dto

data class CommandDTO(
    val id:Long,
    val keyword:String,
    val operateAs:String,
    val userId:Long?
)

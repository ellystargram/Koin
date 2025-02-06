package bot.koin.dto

import org.jetbrains.exposed.sql.javatime.Day

data class CryptoCurrencyDTO(
    val id:Long,
    val name:String,
    val symbol:String,
    val simpleName:String,
    val priceUSD:Double,
    val exchangeFeeBoostRate:Double,
    val maxDecimalPoint: Int,
    val maxTransactionAmount:Double,
    val maxTransactionAmountPerDay: Double,
    val maxTransactionMatchTime: Long,
)
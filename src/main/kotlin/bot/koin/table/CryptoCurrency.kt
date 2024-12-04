package bot.koin.table

import org.jetbrains.exposed.sql.Table

object CryptoCurrency:Table("crypto_currency"){
    val id = long("id").autoIncrement()
    val name = text("name")
    val symbol = text("symbol")
    val simpleName = text("simple_name")
    val priceUSD = double("price_usd")
    val exchangeFeeBoostRate = double("exchange_fee_boost_rate")
    val maxDecimalPoint = integer("max_decimal_point")
    val maxTransactionAmount = double("max_transaction_amount")
    val maxTransactionAmountPerDay = double("max_transaction_amount_per_day")
    val maxTransactionMatchTime = integer("max_transaction_match_time")

    override val primaryKey = PrimaryKey(id)
}
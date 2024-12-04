package bot.koin.table

import org.jetbrains.exposed.sql.Table

object CryptoExchanger:Table("crypto_exchanger") {
    val id = long("id").autoIncrement()
    val name = text("name")
    val defaultFeeRate = double("default_fee_rate")

    override val primaryKey = PrimaryKey(id)
}
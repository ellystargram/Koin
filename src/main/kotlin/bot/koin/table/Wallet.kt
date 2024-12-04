package bot.koin.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Wallet:Table("wallet") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Member.id)
    val address = text("address")
    val exchangerId = long("exchanger_id").references(CryptoExchanger.id)
    val createdDate = datetime("created_date")

    override val primaryKey = PrimaryKey(id)
}
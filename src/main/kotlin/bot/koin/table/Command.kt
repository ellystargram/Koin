package bot.koin.table

import org.jetbrains.exposed.sql.Table

object Command: Table("command") {
    val id = long("id").autoIncrement()
    val keyword = text("keyword")
    val operateAs = text("operate_as")

    override val primaryKey = PrimaryKey(id)
}
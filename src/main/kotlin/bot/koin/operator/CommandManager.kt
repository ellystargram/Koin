package bot.koin.operator

import bot.koin.exception.NoCommandFoundException
import bot.koin.table.Command
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CommandManager {
    fun addCommand(command: String, operateAs: String, userID:Long) {
        val realOperateAsCommand = transaction {
            Command.select(Command.keyword eq operateAs and ((Command.userId eq userID) or (Command.userId.isNull())))
                .firstOrNull()
        }
        if (realOperateAsCommand == null){
            throw NoCommandFoundException()
        }
        transaction {
            Command.insert {
                it[Command.keyword] = command
                it[Command.operateAs] = realOperateAsCommand[Command.operateAs]
                it[Command.userId] = userID
            }
        }
    }
}
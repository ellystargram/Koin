package bot.koin.operator

import bot.koin.exceptions.UserAlreadyExistException
import bot.koin.exceptions.UserNotExistException
import bot.koin.table.Member
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class UserManager {
    fun joinUser(userID: Long) {
        if (isUserExist(userID)) {
            throw UserAlreadyExistException()
        }
        transaction {
            Member.insert {
                it[id] = userID
                it[joinedDate] = LocalDateTime.now()
            }
        }
    }

    fun leaveUser(userID: Long) {
        if (!isUserExist(userID)) {
            throw UserNotExistException()
        }
        WalletManager(null).deleteAllWallets(userID)
        transaction {
            Member.deleteWhere {
                Member.id eq userID
            }
        }
    }

    fun isUserExist(userID: Long): Boolean {
        val userInfo = transaction {
            Member.select {
                (Member.id eq userID)
            }.toList()
        }
        return userInfo.isNotEmpty()
    }
}
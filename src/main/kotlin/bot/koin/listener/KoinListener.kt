package bot.koin.listener

import bot.koin.exceptions.UserAlreadyExistException
import bot.koin.exceptions.UserNotExistException
import bot.koin.operator.ChatManager
import bot.koin.operator.UserManager
import bot.koin.operator.WalletManager
import bot.koin.startsWith
import bot.koin.table.Command
import bot.koin.table.Pronounce
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class KoinListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot) return

        val userID = event.author.idLong
        var userMessage = event.message.contentRaw

        val prefixes = transaction {
            Pronounce.select {
                (Pronounce.userId eq userID) or (Pronounce.userId.isNull())
            }.toList()
        }

        val prefixRemoved = checkPrefixInListAndRemove(prefixes.map { it[Pronounce.pronounce] }, userMessage)
        if (prefixRemoved == null && event.isFromGuild) {
            return
        }
        prefixRemoved?.let { userMessage = it }

        val channel = event.channel
        val chatManager = ChatManager(channel)

        val userCommand = transaction {
            println(userMessage)
            Command
//                .select { userMessage.startsWith(Command.keyword) }
//                .firstOrNull()
            for (row in Command.selectAll()) {
                if (userMessage.startsWith(row[Command.keyword])) {
                    return@transaction row
                }
            }
            return@transaction null
        }
        val userWantCommand = userCommand?.get(Command.operateAs)
        userCommand?.let { userMessage = userMessage.removePrefix(it[Command.keyword]).trim() }

        if (userWantCommand == "join"){
            // join command
            try {
                val userManager = UserManager()
                userManager.joinUser(userID)
                chatManager.sendGoodMessage("가입 성공!", "가족이 된걸 환영합니다.")
            } catch (e: UserAlreadyExistException) {
                chatManager.sendErrorMessage("유저 가입 실패", e.message)
            } catch (e: Exception) {
                chatManager.sendUnknownErrorMessage(e.message)
            }
            return
        } else if (userWantCommand == "leave") {
            // leave command
            try {
                val userManager = UserManager()
                userManager.leaveUser(userID)
                chatManager.sendGoodMessage("잘가시게!", "누군가 저에대해 묻는다면, 모른다 답하세요!")
            } catch (e: UserNotExistException) {
                chatManager.sendErrorMessage("유저 탈퇴 실패", e.message)
            } catch (e: Exception) {
                chatManager.sendUnknownErrorMessage(e.message)
            }
            return
        } else if(userWantCommand == "create_wallet"){
            val walletManager = WalletManager(chatManager)
            try {
                walletManager.createWallet(userID)
            } catch (e: Exception) {
                chatManager.sendUnknownErrorMessage(e.message)
            }
        } else if (userWantCommand == "delete_wallet") {
            val walletManager = WalletManager(chatManager)
            try {
                walletManager.deleteWallet(userID, userMessage)
            } catch (e: Exception) {
                chatManager.sendUnknownErrorMessage(e.message)
            }
        } else if(userWantCommand == "get_wallet"){
            val walletManager = WalletManager(chatManager)
            try {
                walletManager.searchWallet(userID, userMessage)
            } catch (e: Exception) {
                chatManager.sendUnknownErrorMessage(e.message)
            }
        }
        else {
            // something else fucked up
            channel.sendMessage("debug $userMessage").queue()
        }


    }

    fun checkPrefixInList(prefixes: List<String>, userMessage: String): Boolean {
        return prefixes.any { prefix -> userMessage.trim().startsWith(prefix) }
    }

    fun checkPrefixInListAndRemove(prefixes: List<String>, userMessage: String): String? {
        prefixes.forEach { prefix ->
            if (userMessage.trim().startsWith(prefix)) {
                return userMessage.removePrefix(prefix).trim()
            }
        }
        return null
    }
}
package bot.koin.listener

import bot.koin.exceptions.UserAlreadyExistException
import bot.koin.exceptions.UserNotExistException
import bot.koin.operator.ChatManager
import bot.koin.operator.UserManager
import bot.koin.startsWith
import bot.koin.table.Command
import bot.koin.table.Pronounce
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
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

//        val joinCommandList = transaction {
//            Command.select {
//                (Command.operateAs eq "join")
//            }.toList()
//        }
        val userCommand = transaction {
            Command
                .select { userMessage.startsWith(Command.keyword) }
                .firstOrNull()
        }
        val userWantCommand = userCommand?.get(Command.operateAs)
        userCommand?.let { userMessage = userMessage.removePrefix(it[Command.keyword]) }

//        if (checkPrefixInList(joinCommandList.map { it[Command.keyword] }, userMessage)) {
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
        } else if(userWantCommand == null){
            // not command
            channel.sendMessage("debug null $userMessage").queue()
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
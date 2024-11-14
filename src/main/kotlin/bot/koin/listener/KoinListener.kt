package bot.koin.listener

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

        channel.sendMessage("debug $userMessage").queue()


    }

    fun checkPrefixInList(prefixes: List<String>, userMessage: String): Boolean {
        return prefixes.any { prefix -> userMessage.startsWith(prefix) }
    }

    fun checkPrefixInListAndRemove(prefixes: List<String>, userMessage: String): String? {
        prefixes.forEach { prefix ->
            if (userMessage.startsWith(prefix)) {
                return userMessage.removePrefix(prefix)
            }
        }
        return null
    }
}
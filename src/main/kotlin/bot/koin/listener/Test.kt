package bot.koin.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class TestListener: ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot) return
        val inputMessage = event.message.contentRaw
        if (inputMessage == "ping") {
            event.channel.sendMessage("pong").queue()
        }
    }
}
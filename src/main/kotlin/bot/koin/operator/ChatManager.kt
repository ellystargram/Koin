package bot.koin.operator

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import java.awt.Color

class ChatManager(val channel: MessageChannel) {
    fun sendGoodMessage(title: String, message: String) {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0x00ff00))
            .build()
        channel.sendMessageEmbeds(embed).queue()
    }

    fun sendErrorMessage(title: String, message: String) {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0xff0000))
            .build()
        channel.sendMessageEmbeds(embed).queue()
    }

    fun sendUnknownErrorMessage(exceptionMessage: String?){
        val embed = EmbedBuilder()
            .setTitle("잘 모르겠는 에러 발생")
            .setDescription("에러라는 친구가 다음의 메시지를 남기고 갔어요!:\n$exceptionMessage")
            .setColor(Color(0xff0000))
            .build()
        channel.sendMessageEmbeds(embed).queue()
    }
}
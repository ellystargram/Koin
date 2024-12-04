package bot.koin.operator

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import java.awt.Color

class ChatManager(private val channel: MessageChannel) {
    fun sendGoodMessage(title: String, message: String): Message {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0x00ff00))
            .build()

        return sendEmbedThenReturnMessage(embed)
    }

    fun sendWarningMessage(title: String, message: String): Message {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0xffff00))
            .build()

        return sendEmbedThenReturnMessage(embed)
    }

    fun sendErrorMessage(title: String, message: String): Message {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0xff0000))
            .build()

        return sendEmbedThenReturnMessage(embed)
    }

    fun sendUnknownErrorMessage(exceptionMessage: String?): Message {
        val embed = EmbedBuilder()
            .setTitle("잘 모르겠는 에러 발생")
            .setDescription("에러라는 친구가 다음의 메시지를 남기고 갔어요!:\n$exceptionMessage")
            .setColor(Color(0xff0000))
            .build()

        return sendEmbedThenReturnMessage(embed)
    }

    fun sendQuestionMessage(title: String, message: String): Message {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color(0x0000ff))
            .build()

        return sendEmbedThenReturnMessage(embed)
    }

    private fun sendEmbedThenReturnMessage(embed: MessageEmbed): Message {
        try {
            val futureMessage = channel.sendMessageEmbeds(embed).submit()
            return futureMessage.get()
        } catch (e: Exception) {
            e.printStackTrace()
            return channel.sendMessage("메시지 전송에 실패했습니다.").complete()
        }
    }

    fun modifyMessageToGood(message: Message, title: String, description: String): Message {
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color(0x00ff00))
            .build()

        return message.editMessageEmbeds(embed).complete()
    }
}
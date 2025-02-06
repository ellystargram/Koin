package bot.koin.listener

import bot.koin.operator.ChatManager
import bot.koin.operator.WalletManager
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class WalletManagerButtonListener: ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        super.onButtonInteraction(event)
        if (event.user.isBot) return

        val user = event.user
        val buttonID = event.componentId
         
        val buttonIDMap = mutableMapOf<String, String>()
        val buttonIDSplit = buttonID.split("</>")
        buttonIDSplit.forEach {
            val keyValueSplit = it.split(":")
            buttonIDMap[keyValueSplit[0].trim()] = keyValueSplit[1].trim()
        }

        if (buttonIDMap["userID"] != user.id) return
        if (buttonIDMap["messageID"] != event.messageId) return

        val walletManager = WalletManager(null)
        var returnText = ""
        when(buttonIDMap["from"]){
            "WM.CW"->{
                returnText = walletManager.createWallet(user.idLong, buttonIDMap["value"]?:"", "" )
            }
        }

        val chatManager = ChatManager(event.channel)
        val title = returnText.split("</>")[0].trim()
        val message = returnText.split("</>")[1].trim()
        chatManager.sendGoodEmbedMessage(title, message)
    }
}
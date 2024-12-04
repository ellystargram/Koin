package bot.koin.operator

import bot.koin.dto.CryptoExchangerDTO
import bot.koin.dto.WalletDTO
import bot.koin.exceptions.WalletNotFoundException
import bot.koin.pow
import bot.koin.table.CryptoExchanger
import bot.koin.table.Wallet
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class WalletManager(val chatManager: ChatManager?) {
    fun getWallet(userID: Long, address: String): WalletDTO {
        val userWallet = transaction {
            Wallet.select { (Wallet.userId eq userID) and (Wallet.address eq address) }
                .firstOrNull()
        }
        if (userWallet == null) {
            throw WalletNotFoundException()
        }
        return WalletDTO(
            userWallet[Wallet.userId],
            userWallet[Wallet.address],
            userWallet[Wallet.userId],
            userWallet[Wallet.exchangerId],
            userWallet[Wallet.createdDate]
        )
    }

    fun getWallets(userID: Long): List<WalletDTO> {
        val userWallets = transaction {
            Wallet.select { Wallet.userId eq userID }
                .toList()
        }
        if (userWallets.isEmpty()) {
            throw WalletNotFoundException()
        }
        return userWallets.map {
            WalletDTO(
                it[Wallet.userId],
                it[Wallet.address],
                it[Wallet.userId],
                it[Wallet.exchangerId],
                it[Wallet.createdDate]
            )
        }

    }

    fun createWallet(userID: Long) {
        if (chatManager == null) {
            return
        }

        val userManager = UserManager()
        if (!userManager.isUserExist(userID)) {
            chatManager.sendErrorMessage("지갑 생성 실패", "가입을 먼저 해주세요.")
            return
        }


        val exchangers = transaction {
            CryptoExchanger.selectAll()
                .map {
                    CryptoExchangerDTO(
                        it[CryptoExchanger.id],
                        it[CryptoExchanger.name],
                        it[CryptoExchanger.defaultFeeRate]
                    )
                }
        }

        if (exchangers.isEmpty()) {
            chatManager.sendErrorMessage("지갑 생성 실패", "지갑을 생성할 수 있는 거래소가 없습니다.")
            return
        }
        var exchangerFeatures = "어떤 거래소에서 지갑을 생성하시겠습니까?\n\n"
        exchangers.forEach {
            exchangerFeatures += ("${it.name} - 수수료율: ${it.defaultFeeRate*100}%\n")
        }
        val askExchangerMassage = chatManager.sendQuestionMessage("지갑 생성", exchangerFeatures)

        exchangers.forEachIndexed { index, _ ->
            val emoji = Emoji.fromUnicode("${0x31.toChar() + index}\uFE0F\u20E3")
//            val emoji = chatManager.channel.jda.getEmojisByName(":number_${index+1}:", true).first()
            askExchangerMassage.addReaction(emoji).queue()
        }

        val reactionListener = object : ListenerAdapter(){
            override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
                super.onMessageReactionAdd(event)
                if (event.user?.isBot == true) return
                if (event.messageId != askExchangerMassage.id) return
                if (event.user?.idLong != userID) return

                println(event.emoji.name[0].code)
                println(exchangers.size)
                val exchanger = exchangers[event.emoji.name[0].code - 0x0031]

                val existAddresses = transaction {
                    Wallet.selectAll()
                        .orderBy(Wallet.address, SortOrder.ASC)
                        .map { it[Wallet.address] }
                }

                var newAddressLengthTarget = 6
                while (existAddresses.size > 52L.pow(newAddressLengthTarget) / 4) {
                    newAddressLengthTarget++
                }

                var newAddress = ""

                do {
                    newAddress = ""
                    for (i in 0 until newAddressLengthTarget) {
                        when ((0..1).random()) {
                            0 -> newAddress += ('A'..'Z').random()
                            1 -> newAddress += ('a'..'z').random()
                        }
                    }
                } while (existAddresses.contains(newAddress))

                val newWallet = WalletDTO(
                    existAddresses.size.toLong() + 1,
                    newAddress,
                    userID,
                    exchanger.id,
                    LocalDateTime.now()
                )

                transaction {
                    Wallet.insert {
                        it[userId] = newWallet.userId
                        it[address] = newWallet.address
                        it[exchangerId] = newWallet.exchangerId
                        it[createdDate] = newWallet.createdDate
                    }
                }

                val walletInfo = "지갑 주소: ${newWallet.address}\n거래소: ${exchanger.name}"
                chatManager.modifyMessageToGood(askExchangerMassage, "지갑 생성 성공", "지갑이 생성되었습니다.\n\n$walletInfo")
                askExchangerMassage.clearReactions().queue()
                askExchangerMassage.jda.removeEventListener(this)
            }
        }
        askExchangerMassage.jda.addEventListener(reactionListener)
    }

    fun deleteWallet(userID: Long, address: String) {
        if (chatManager == null) {
            return
        }
        val userManager = UserManager()
        if (!userManager.isUserExist(userID)) {
            chatManager.sendErrorMessage("지갑 삭제 실패", "가입을 먼저 해주세요.")
            return
        }

        if (!address.matches(Regex("\\w{6,}"))){
            chatManager.sendErrorMessage("지갑 삭제 실패", "잘못된 주소입니다.\n 주소는 8자 이상의 영문자로 이루어져야 합니다.\n 명령어: 지갑 삭제 [주소]")
            return
        }

        try {
            getWallet(userID, address)
        } catch (e: WalletNotFoundException) {
            chatManager.sendErrorMessage("지갑 삭제 실패", "존재하지 않는 지갑이거나, 소유한 지갑이 아닙니다.")
            return
        } catch (e: Exception) {
            chatManager.sendUnknownErrorMessage(e.message)
            return
        }

        transaction {
            Wallet.deleteWhere { (Wallet.userId eq userID) and (Wallet.address eq address) }
        }

        chatManager.sendGoodMessage("지갑 삭제 성공", "지갑이 삭제되었습니다.")
    }

    fun deleteAllWallets(userID: Long) {
        transaction {
            Wallet.deleteWhere { Wallet.userId eq userID }
        }
    }
}

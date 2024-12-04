package bot.koin.exceptions

class WalletNotFoundException : Exception() {
    override val message: String
        get() = "지갑을 찾을 수 없습니다."
}
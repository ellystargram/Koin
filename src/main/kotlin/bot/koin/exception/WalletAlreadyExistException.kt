package bot.koin.exception

class WalletAlreadyExistException: Exception() {
    override val message: String
        get() = "이미 지갑이 존재합니다."
}
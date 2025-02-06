package bot.koin.exception

class ExchangerNotExistException: Exception() {
    override val message: String
        get() = "해당 거래소가 존재하지 않습니다."
}
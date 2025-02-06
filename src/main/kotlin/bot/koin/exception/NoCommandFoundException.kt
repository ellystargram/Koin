package bot.koin.exception

class NoCommandFoundException: Exception() {
    override val message: String
        get() = "No command found"
}
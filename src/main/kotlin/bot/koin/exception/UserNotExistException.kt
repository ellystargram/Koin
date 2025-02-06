package bot.koin.exception

class UserNotExistException: Exception() {
    override val message: String
        get() = "User does not exist"
}
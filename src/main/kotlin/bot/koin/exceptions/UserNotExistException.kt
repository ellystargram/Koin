package bot.koin.exceptions

class UserNotExistException: Exception() {
    override val message: String
        get() = "User does not exist"
}
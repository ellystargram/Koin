package bot.koin.exception

class UserAlreadyExistException: Exception() {
    override val message: String
        get() = "User already exist"
}
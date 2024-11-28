package bot.koin.exceptions

class UserAlreadyExistException: Exception() {
    override val message: String
        get() = "User already exist"
}
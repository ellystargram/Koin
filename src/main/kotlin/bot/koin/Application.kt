package bot.koin

import bot.koin.listener.TestListener
import bot.koin.plugins.configureRouting
import bot.koin.plugins.configureSerialization
import io.ktor.server.application.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.jetbrains.exposed.sql.Database
import java.util.*

var jda: JDA? = null

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

//    jda?.addEventListener(TestListener())
}

fun Application.module() {
    configureSerialization()
    configureRouting()

    val token = environment.config.property("bot.token").getString()

    jda = JDABuilder.createDefault(token)
        .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
        .build()

    jda?.addEventListener(TestListener())

    println("Bot is running")

    val dbDriver = environment.config.property("database.driver").getString()
    val dbUrl = environment.config.property("database.url").getString()
    val dbUserName = environment.config.property("database.username").getString()
    val dbPassword = environment.config.property("database.password").getString()

    Database.connect(
        url = dbUrl,
        driver = dbDriver,
        user = dbUserName,
        password = dbPassword
    )
}

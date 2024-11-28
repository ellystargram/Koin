package bot.koin

import bot.koin.listener.KoinListener
import bot.koin.plugins.configureRouting
import bot.koin.plugins.configureSerialization
import io.ktor.server.application.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import java.util.*
import kotlin.time.Duration.Companion.hours

var jda: JDA? = null
var database: Database? = null

fun main(args: Array<String>) = runBlocking {
    io.ktor.server.netty.EngineMain.main(args)

//    jda?.addEventListener(TestListener())

    launch {
        while (isActive) {
            try {
                // koin price update
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(1.hours)
        }
    }
}

fun Application.module() {
    configureSerialization()
    configureRouting()

    val token = environment.config.property("bot.token").getString()

    jda = JDABuilder.createDefault(token)
        .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
        .build()

//    jda?.addEventListener(TestListener())
    jda?.addEventListener(KoinListener())

    println("Bot is running")

    val dbDriver = environment.config.property("database.driver").getString()
    val dbUrl = environment.config.property("database.url").getString()
    val dbUserName = environment.config.property("database.username").getString()
    val dbPassword = environment.config.property("database.password").getString()

    database = Database.connect(
        url = dbUrl,
        driver = dbDriver,
        user = dbUserName,
        password = dbPassword
    )
    /* not working properly */
//    transaction { //initialize database
//        SchemaUtils.createMissingTablesAndColumns(Member, Pronounce)
//
//        if (Pronounce.selectAll().empty()){
//            Pronounce.insert {
//                it[userId] = null
//                it[pronounce] = "코인아"
//            }
//        }
//    }
}

fun String.startsWith(column: Column<String>): Op<Boolean> {
    val likeCriteria = "$this%"
    return column like likeCriteria
}
package bot.koin.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Member:Table("member") {
    val id = long("id").autoIncrement() // `member` 테이블의 기본 키, auto_increment 설정
    val joinedDate = datetime("joined_date") // `member` 테이블의 `joined_date` 필드

    override val primaryKey = PrimaryKey(id)
}
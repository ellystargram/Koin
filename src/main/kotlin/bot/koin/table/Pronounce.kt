package bot.koin.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object Pronounce : Table("pronounce") {
    val id = long("id").autoIncrement() // BIGINT auto_increment 필드
    val userId = long("user_id").references(Member.id, onDelete = ReferenceOption.CASCADE).nullable()// 외래 키
    val pronounce = text("pronounce") // TEXT 필드

    override val primaryKey = PrimaryKey(id) // 기본 키 설정
}
package com.health.openhealth

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant
import kotlin.reflect.KClass

val gson: Gson = GsonBuilder().registerTypeAdapter(Instant::class.java, InstantDeserializer()).create()


class InstantDeserializer : JsonDeserializer<Instant?>, JsonSerializer<Instant> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant {
        return Instant.ofEpochSecond(json.asLong)
    }

    override fun serialize(
        src: Instant?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.epochSecond)
    }
}


@Entity
data class RecordData(
    @PrimaryKey
    val id: String,
    val originalData: String,
    @TypeConverters(Converters::class)
    val type: KClass<*>,
)

class Converters {
    @TypeConverter
    fun fromType(type: KClass<*>): String {
        return type.qualifiedName!!
    }
    @TypeConverter
    fun toType(name: String): KClass<*> {
        return Class.forName(name).kotlin
    }
}

@Dao
interface RecordDataDao {
    @Insert
    suspend fun insert(recordData: RecordData)
    @Query("SELECT * FROM RecordData WHERE id = :id")
    suspend fun get(id: String): RecordData?
    @Query("DELETE FROM RecordData WHERE id = :id")
    suspend fun delete(id: String)
    @Query("SELECT * FROM RecordData WHERE type = :clazz")
    suspend fun getAllOfClass(clazz: KClass<*>): List<RecordData>
    @Query("SELECT COUNT(*) FROM RecordData")
    suspend fun count(): Long
}

@Database(version = 1, entities = [RecordData::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDataDao(): RecordDataDao
}
package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AmalDao
import com.example.data.local.dao.QuranVerseDao
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.QuranVerseEntity
import com.example.data.local.entity.SalahLogEntity

/**
 * Main Room Database for Al-Hujur AI, storing and tracking daily Islamic practices (Amal),
 * including obligatory/voluntary Salah, Quran recitation logs, and Quran verses repository.
 */
@Database(
    entities = [
        DailyAmalEntity::class,
        SalahLogEntity::class,
        QuranLogEntity::class,
        DhikrLogEntity::class,
        QuranVerseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun amalDao(): AmalDao
    abstract fun quranVerseDao(): QuranVerseDao

    companion object {
        // Preserve v1 Amal, Salah, Quran and Dhikr logs when adding the offline verse reader.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `quran_verses` (
                  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                  `surahNumber` INTEGER NOT NULL, `ayahNumber` INTEGER NOT NULL,
                  `surahNameArabic` TEXT NOT NULL, `surahNameEnglish` TEXT NOT NULL,
                  `surahNameBengali` TEXT NOT NULL, `totalAyahs` INTEGER NOT NULL,
                  `revelationType` TEXT NOT NULL, `arabicText` TEXT NOT NULL,
                  `pronunciationBengali` TEXT NOT NULL,
                  `translationBengali` TEXT NOT NULL, `translationEnglish` TEXT NOT NULL,
                  `isBookmarked` INTEGER NOT NULL, `lastReadTimestamp` INTEGER NOT NULL
                )""")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "al_hujur_amal_database.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

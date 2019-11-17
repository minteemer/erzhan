package iu.quaraseequi.erzhan.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import iu.quaraseequi.erzhan.data.db.dao.ImagesDao
import iu.quaraseequi.erzhan.data.db.models.TargetImageModel

@Database(
    entities = [
        TargetImageModel::class
    ],
    version = 1
)
abstract class ErzhanDatabase : RoomDatabase() {

    abstract fun imagesDao(): ImagesDao

    companion object {

        private const val DB_NAME = "erzhan_db"

        fun createInstance(context: Context): ErzhanDatabase =
            Room.databaseBuilder(context, ErzhanDatabase::class.java, DB_NAME)
                .build()
    }

}
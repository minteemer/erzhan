package iu.quaraseequi.erzhan.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.db.models.TargetImageModel

@Dao
interface ImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveImage(image: TargetImageModel): Single<Long>

    @Query("SELECT * FROM images WHERE id = :id LIMIT 1")
    fun getImage(id: Long): Single<TargetImageModel>

    @Query("SELECT * FROM images")
    fun getAllImages(): Single<List<TargetImageModel>>

    @Query("DELETE FROM images WHERE id = :imageId")
    fun removeImage(imageId: Long): Completable

}
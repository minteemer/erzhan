package iu.quaraseequi.erzhan.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

@Entity(tableName = "images")
data class TargetImageModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "image_path")
    val imagePath: String,

    @ColumnInfo(name = "descriptor")
    val descriptor: String
) {

    fun toEntity(): TargetImage = TargetImage(
        id, imagePath,
        descriptor
    )

    constructor(image: TargetImage) : this(image.id, image.path, image.descriptor)
}
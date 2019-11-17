package iu.quaraseequi.erzhan.repositories.featureExtraction

import android.graphics.Bitmap

interface FeatureExtractionRepository {

    fun getFeatures(bitmap: Bitmap): FloatArray

}
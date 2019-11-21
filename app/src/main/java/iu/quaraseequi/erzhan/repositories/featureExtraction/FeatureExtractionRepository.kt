package iu.quaraseequi.erzhan.repositories.featureExtraction

import org.opencv.core.Mat

interface FeatureExtractionRepository {

    fun getFeatures(image: Mat): FloatArray

}
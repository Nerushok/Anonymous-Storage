package io.anonymous.storage.data.remote.cloud_functions

import com.google.firebase.functions.FirebaseFunctions

class CloudFunctionsController {

    private var cloudFunctions: FirebaseFunctions? = null

    fun getInstance(): FirebaseFunctions {
        if (cloudFunctions == null) initCloudFunctions()

        return cloudFunctions!!
    }

    private fun initCloudFunctions() {
        cloudFunctions = FirebaseFunctions.getInstance()
    }
}
package io.anonymous.storage.domain.base.interactor

import io.anonymous.storage.domain.base.Result

abstract class BaseInteractor<Request, Response> {

    suspend fun execute(request: Request): Result<Response> {
        return try {
            success(executeInternal(request))
        } catch (e: Exception) {
            e.printStackTrace()
            failure(e)
        }
    }

    protected abstract suspend fun executeInternal(request: Request): Response

    protected fun success(response: Response): Result.Success<Response> =
        Result.Success(response)

    protected fun failure(exception: Exception): Result.Failure<Response> =
        Result.Failure(exception)
}
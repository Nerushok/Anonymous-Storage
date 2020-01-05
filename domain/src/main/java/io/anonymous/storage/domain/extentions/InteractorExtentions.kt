package io.anonymous.storage.domain.extentions

import io.anonymous.storage.domain.base.Result
import io.anonymous.storage.domain.base.interactor.BaseInteractor

suspend inline fun <Request, Response> BaseInteractor<Request, Response>.execute(
    request: Request,
    onSuccess: ((Response) -> Unit) = {},
    onFailure: ((Exception) -> Unit) = {}
) {
    val result = execute(request)

    if (result is Result.Success) onSuccess.invoke(result.data)
    else if (result is Result.Failure) onFailure.invoke(result.error)
    else throw RuntimeException("Unsupported result type")
}
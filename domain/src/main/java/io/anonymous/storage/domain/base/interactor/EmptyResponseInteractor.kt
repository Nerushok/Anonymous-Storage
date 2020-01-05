package io.anonymous.storage.domain.base.interactor

import io.anonymous.storage.domain.base.Nothing

abstract class EmptyResponseInteractor<Request> : BaseInteractor<Request, Nothing>() {

    override suspend fun executeInternal(request: Request): Nothing {
        action(request)
        return Nothing
    }

    protected abstract suspend fun action(request: Request)
}
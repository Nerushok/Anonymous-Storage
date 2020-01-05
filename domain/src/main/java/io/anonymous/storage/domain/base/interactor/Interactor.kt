package io.anonymous.storage.domain.base.interactor

abstract class Interactor<Request, Response> : BaseInteractor<Request, Response>() {

    override suspend fun executeInternal(request: Request): Response {
        return action(request)
    }

    protected abstract suspend fun action(request: Request): Response
}
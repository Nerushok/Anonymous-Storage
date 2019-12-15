package io.anonymous.storage.domain.base.interactor

abstract class Interactor<Request, Response> : BaseInteractor<Request, Response>() {

    protected abstract suspend fun action(request: Request): Response
}
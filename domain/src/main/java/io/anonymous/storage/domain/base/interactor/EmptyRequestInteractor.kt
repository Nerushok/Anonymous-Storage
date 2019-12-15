package io.anonymous.storage.domain.base.interactor

import io.anonymous.storage.domain.base.Nothing

abstract class EmptyRequestInteractor<Response> : BaseInteractor<Nothing, Response>() {

    override suspend fun executeInternal(request: Nothing): Response {
        return action()
    }

    protected abstract fun action(): Response
}
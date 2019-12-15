package io.anonymous.storage.domain.base

sealed class Result<Data> {

    data class Success<Data>(val data: Data) : Result<Data>()

    data class Failure<Data>(val error: Exception) : Result<Data>()
}
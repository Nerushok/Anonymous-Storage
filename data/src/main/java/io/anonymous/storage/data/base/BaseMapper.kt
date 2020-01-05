package io.anonymous.storage.data.base

abstract class BaseMapper<In, Out> {

    abstract fun map(fromModel: In): Out

    fun map(fromModels: List<In>): List<Out> = fromModels.map { map(it) }
}
package com.hfzq.xfn.framework.commlib

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * @author xwang
 * 2023/8/16 00:13
 * @description 
 **/
object RxBus {

    private val mBus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    fun post(event: IRxBusEvent) {
        mBus.onNext(event)
    }

    @Deprecated("use inline fun instead")
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    inline fun <reified T> toObservable(): Observable<T> {
        return toObservable(T::class.java)
    }

    fun toObservable() = mBus

}

interface IRxBusEvent
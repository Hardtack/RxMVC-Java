package kr.geonu.util.rx

import rx.Observable
import rx.Observer
import rx.lang.kotlin.PublishSubject

/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(): Pair<Observable<T>, Observable<T>> {
    val first = this.take(1)
    val rest = this.skip(1)

    return Pair(first, rest)
}


/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(onFirst: (T) -> Unit, onRest: (T) -> Unit) {
    val (first, rest) = this.firstAndRest()
    first.subscribe(onFirst)
    rest.subscribe(onRest)
}


/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(firstObserver: Observer<T>, restObserver: Observer<T>) {
    val (first, rest) = this.firstAndRest()
    first.subscribe(restObserver)
    rest.subscribe(firstObserver)
}

/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(firstObserver: Observer<T>): Observable<T> {
    val (first, rest) = firstAndRest()
    first.subscribe(firstObserver)
    return rest
}

/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(onFirst: (T) -> Unit): Observable<T> {
    val (first, rest) = firstAndRest()
    first.subscribe(onFirst)
    return rest
}

package kr.geonu.util.rx

import rx.Observable
import rx.Observer
import rx.lang.kotlin.PublishSubject

/**
 * Split stream into first and rest.
 */
fun <T> Observable<T>.firstAndRest(): Pair<Observable<T>, Observable<T>> {
    val first = PublishSubject<T>()
    val rest = PublishSubject<T>()

    val shared = this.share()
    var sentFirst = false

    shared.first().subscribe(object : Observer<T> {
        override fun onCompleted() {
            first.onCompleted()
            if (!sentFirst) {
                rest.onCompleted()
            }
        }

        override fun onError(e: Throwable?) {
            first.onError(e)
            rest.onError(e)
        }

        override fun onNext(t: T) {
            first.onNext(t)
            sentFirst = true
            shared.subscribe(object : Observer<T> {
                override fun onError(e: Throwable?) {
                    rest.onError(e)
                }

                override fun onCompleted() {
                    if (!rest.hasCompleted()) {
                        rest.onCompleted()
                    }
                }

                override fun onNext(t: T) {
                    rest.onNext(t)
                }

            })
        }
    })
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

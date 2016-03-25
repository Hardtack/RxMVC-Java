package kr.geonu.util.rx

import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.lang.kotlin.PublishSubject

/**
 * Merge right stream with latest value of left stream.
 */
fun <T1, T2> Observable<T1>.mergeLatest(right: Observable<T2>, accumulator: (T1, T2) -> T1): Observable<T1> {
    val left = this
    return Observable.create({ subscriber ->
        val sharedLeft = left.share()
        sharedLeft.first().subscribe(object : Subscriber<T1>() {
            override fun onCompleted() {
                // Nothing to do
            }

            override fun onNext(value: T1) {
                var leftCompleted = false
                var rightCompleted = false
                var latestValue = value

                subscriber.onNext(latestValue)

                var rightSubscriber: Subscriber<T2>? = null

                val leftSubscriber = object : Subscriber<T1>() {
                    override fun onNext(value: T1) {
                        latestValue = value
                        subscriber.onNext(latestValue)
                    }

                    override fun onError(e: Throwable?) {
                        rightSubscriber?.unsubscribe()
                        subscriber.onError(e)
                    }

                    override fun onCompleted() {
                        leftCompleted = true
                        if (rightCompleted) {
                            subscriber.onCompleted()
                        }
                    }

                }
                rightSubscriber = object : Subscriber<T2>() {
                    override fun onNext(value: T2) {
                        latestValue = accumulator(latestValue, value)
                        subscriber.onNext(latestValue)
                    }

                    override fun onError(e: Throwable?) {
                        leftSubscriber.unsubscribe()
                        subscriber.onError(e)
                    }

                    override fun onCompleted() {
                        rightCompleted = true
                        if (leftCompleted) {
                            subscriber.onCompleted()
                        }
                    }
                }

                sharedLeft.subscribe(leftSubscriber)
                right.subscribe(rightSubscriber)
            }

            override fun onError(e: Throwable?) {
                subscriber.onError(e)
            }

        })
    })
}


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

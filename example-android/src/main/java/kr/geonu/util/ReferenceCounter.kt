package kr.geonu.util

class ReferenceCounter<T>(private val creator: () -> T, private val onRelease: (T) -> Unit = { x -> }) {
    private var instance: T? = null
    private var count = 0

    fun retain(): T {
        synchronized(this) {
            if (instance == null) {
                assert(count == 0)
                count = 1
                instance = creator()
            } else {
                count += 1
            }
            return instance!!
        }
    }

    fun get(): T? {
        return instance
    }

    fun release(): Boolean {
        synchronized(this) {
            count -= 1
            if (count == 0) {
                assert(instance != null)
                onRelease(instance!!)
                instance = null
                return true
            }
            return false
        }
    }

    inline fun <R> with(body: (instance: T) -> R) {
        val instance = retain()
        try {
            body(instance)
        } finally {
            release()
        }
    }
}

package kr.geonu.mvc;

import rx.Observable;

/**
 * Controller is a function that converts model observable to event observable.
 */
public interface ViewMixin<M> {
    /**
     * Observe event stream & returns event stream
     *
     * @param modelStream the stream to be observed, not null
     * @return the event stream, not null
     */
    Observable<Event> observeModel(Observable<M> modelStream);

}

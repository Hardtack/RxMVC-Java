package kr.geonu.mvc;

import rx.Observable;

/**
 * Controller is a function that converts event observable to model observable.
 */
public interface ControllerMixin<M> {
    /**
     * Observe event stream & return model stream
     *
     * @param eventStream the stream to be observed, not null
     * @return the model stream, not null
     */
    Observable<M> observeEvent(Observable<Event> eventStream);
}

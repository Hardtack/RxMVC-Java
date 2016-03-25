package kr.geonu.mvc;

import rx.Observable;

/**
 * Controller is a function that converts event observable to model observable.
 */
public interface ControllerMixin<M> {
    Observable<M> observeEvent(Observable<Event> eventStream);
}

package kr.geonu.mvc;

import rx.Observable;

/**
 * Controller is a function that converts model observable to event observable.
 */
public interface ViewMixin<M> {
    Observable<Event> observeModel(Observable<M> modelStream);

}

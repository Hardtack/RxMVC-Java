package kr.geonu.mvc;

import org.jetbrains.annotations.NotNull;
import rx.Observable;

/**
 * Controller is a function that converts model observable to event observable.
 */
public interface ViewMixin<M> {
    @NotNull
    Observable<Event> observeModel(@NotNull Observable<M> modelStream);

}

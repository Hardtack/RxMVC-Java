package kr.geonu.mvc;

import org.jetbrains.annotations.NotNull;
import rx.Observable;

/**
 * Controller is a function that converts event observable to model observable.
 */
public interface ControllerMixin<M> {
    @NotNull
    Observable<M> observeEvent(@NotNull Observable<Event> eventStream);
}

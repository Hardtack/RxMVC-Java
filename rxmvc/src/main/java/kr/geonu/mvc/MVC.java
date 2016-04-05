package kr.geonu.mvc;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Utilities for MVC
 */
public class MVC {
    /**
     * Combine view with controller.
     */
    public static <M> void combine(@NotNull ViewMixin<M> view,
                                   @NotNull ControllerMixin<M> controller) {
        combine(view, controller, new Observable.Transformer<M, M>() {
            @Override
            public Observable<M> call(Observable<M> observable) {
                return observable;
            }
        }, new Observable.Transformer<Event, Event>() {
            @Override
            public Observable<Event> call(Observable<Event> observable) {
                return observable;
            }
        });
    }

    /**
     * Combine view with controller
     * With lifecycles
     */
    public static <M> void combine(@NotNull final ViewMixin<M> view,
                                   @NotNull final ControllerMixin<M> controller,
                                   @NotNull final Observable.Transformer<M, M> modelLifecycle,
                                   @NotNull final Observable.Transformer<Event, Event> eventLifecycle) {
        final PublishSubject<Event> eventSubject = PublishSubject.create();
        final ReplaySubject<M> modelSubject = ReplaySubject.create();

        // Provide event stream and get model stream
        Observable<M> modelStream = controller.observeEvent(eventSubject).compose(modelLifecycle);
        modelStream.subscribe(new Subscriber<M>() {
            public void onError(Throwable e) {
                modelSubject.onError(e);
            }

            public void onNext(M m) {
                modelSubject.onNext(m);
            }

            public void onCompleted() {
                modelSubject.onCompleted();
            }
        });

        // Provide model stream and get event stream
        Observable<Event> eventStream = view.observeModel(modelSubject).compose(eventLifecycle);
        eventStream.subscribe(new Subscriber<Event>() {
            public void onError(Throwable e) {
                eventSubject.onError(e);
            }

            public void onNext(Event event) {
                eventSubject.onNext(event);
            }

            public void onCompleted() {
                eventSubject.onCompleted();
            }
        });
    }
}

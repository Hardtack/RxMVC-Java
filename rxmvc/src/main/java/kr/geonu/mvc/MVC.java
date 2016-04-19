package kr.geonu.mvc;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

/**
 * Utilities for MVC
 */
public class MVC {
    /**
     * Combine view with controller.
     *
     * @param view        the view, not null
     * @param controller, the controller, not null
     * @param <M>         model type
     */
    public static <M> void combine(ViewMixin<M> view,
                                   ControllerMixin<M> controller) {
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
     *
     * @param view            the view, not null
     * @param controller,     the controller, not null
     * @param modelLifecycle, transformer that adds lifecycle to model stream, not null
     * @param eventLifecycle, transformer that adds lifecycle to event stream, not null
     * @param <M>             model type
     */
    public static <M> void combine(final ViewMixin<M> view,
                                   final ControllerMixin<M> controller,
                                   final Observable.Transformer<M, M> modelLifecycle,
                                   final Observable.Transformer<Event, Event> eventLifecycle) {
        // We should pass model stream to view lazily,
        // so we use subject.
        final BehaviorSubject<Observable<M>> modelStreamSubject = BehaviorSubject.create();

        // Pass event stream to controller
        Observable<M> modelStream = controller.observeEvent(Observable.create(new Observable.OnSubscribe<Event>() {
            @Override
            public void call(final Subscriber<? super Event> subscriber) {
                modelStreamSubject.subscribe(new Action1<Observable<M>>() {
                    @Override
                    public void call(Observable<M> modelStream) {
                        final Observable<Event> eventStream = view
                                .observeModel(modelStream)
                                .compose(eventLifecycle);
                        eventStream.subscribe(subscriber);
                    }
                });
            }
        })).compose(modelLifecycle);
        modelStreamSubject.onNext(modelStream);
        modelStreamSubject.onCompleted();
    }
}

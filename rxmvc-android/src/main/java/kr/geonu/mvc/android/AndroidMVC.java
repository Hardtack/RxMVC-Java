package kr.geonu.mvc.android;

import android.support.annotation.NonNull;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.FragmentLifecycleProvider;
import kr.geonu.mvc.ControllerMixin;
import kr.geonu.mvc.Event;
import kr.geonu.mvc.MVC;
import kr.geonu.mvc.ViewMixin;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AndroidMVC {
    /**
     * Combine view with controller & ensure main thread for model stream
     */
    public static <M> void combine(@NonNull ViewMixin<M> view,
                                   @NonNull ControllerMixin<M> controller) {
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
     * Combine view with controller & ensure main thread for model stream
     */
    public static <M> void combine(@NonNull final ViewMixin<M> view,
                                   @NonNull final ControllerMixin<M> controller,
                                   @NonNull final Observable.Transformer<M, M> modelLifecycle,
                                   @NonNull final Observable.Transformer<Event, Event> eventLifecycle) {
        MVC.combine(view, controller, new Observable.Transformer<M, M>() {
            @Override
            public Observable<M> call(Observable<M> observable) {
                return modelLifecycle.call(observable).observeOn(AndroidSchedulers.mainThread());
            }
        }, eventLifecycle);
    }

    /**
     * Combine view with controller
     * With RxLifecycleProvider
     */
    public static <M> void combine(@NonNull final ViewMixin<M> view,
                                   @NonNull final ControllerMixin<M> controller,
                                   @NonNull final ActivityLifecycleProvider lifecycleProvider) {
        combine(view,
                controller,
                lifecycleProvider.<M>bindToLifecycle(),
                lifecycleProvider.<Event>bindToLifecycle());
    }

    /**
     * Combine view with controller
     * With RxLifecycleProvider
     */
    public static <M> void combine(@NonNull final ViewMixin<M> view,
                                   @NonNull final ControllerMixin<M> controller,
                                   @NonNull final ActivityLifecycleProvider lifecycleProvider,
                                   @NonNull ActivityEvent unsubscribeOn) {
        combine(view,
                controller,
                lifecycleProvider.<M>bindUntilEvent(unsubscribeOn),
                lifecycleProvider.<Event>bindUntilEvent(unsubscribeOn));

    }

    /**
     * Combine view with controller
     * With RxLifecycleProvider
     */
    public static <M> void combine(@NonNull final ViewMixin<M> view,
                                   @NonNull final ControllerMixin<M> controller,
                                   @NonNull final FragmentLifecycleProvider lifecycleProvider) {
        combine(view,
                controller,
                lifecycleProvider.<M>bindToLifecycle(),
                lifecycleProvider.<Event>bindToLifecycle());
    }

    /**
     * Combine view with controller
     * With RxLifecycleProvider
     */
    public static <M> void combine(@NonNull final ViewMixin<M> view,
                                   @NonNull final ControllerMixin<M> controller,
                                   @NonNull final FragmentLifecycleProvider lifecycleProvider,
                                   @NonNull FragmentEvent unsubscribeOn) {
        combine(view,
                controller,
                lifecycleProvider.<M>bindUntilEvent(unsubscribeOn),
                lifecycleProvider.<Event>bindUntilEvent(unsubscribeOn));

    }
}

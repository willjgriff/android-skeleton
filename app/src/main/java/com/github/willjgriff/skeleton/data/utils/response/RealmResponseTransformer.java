package com.github.willjgriff.skeleton.data.utils.response;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

import static com.github.willjgriff.skeleton.data.utils.response.ResponseHolder.Source.STORAGE;

/**
 * Created by Will on 03/11/2016.
 */

public class RealmResponseTransformer<RESPONSETYPE> implements Observable.Transformer<RESPONSETYPE, ResponseHolder<RESPONSETYPE>> {

	@Override
	public Observable<ResponseHolder<RESPONSETYPE>> call(Observable<RESPONSETYPE> observable) {
		return observable
			// Only take the first emission. More data will be emitted if/when this RealmQuery's data is updated.
			.first()
			// Put data into ResponseHolder, ResponseHolder is necessary to differentiate between
			// storage and network sourced data and retain any errors.
			.map(responseData -> new ResponseHolder<RESPONSETYPE>(STORAGE).setData(responseData))
			// Put any error into ResponseHolder so it is passed to onNext like any other response.
			// Then the error can be relayed to the user.
			.onErrorReturn(throwable -> {
				Timber.e(throwable, "Error getting data from Storage");
				return new ResponseHolder<RESPONSETYPE>(STORAGE).setError(throwable);
			});
	}
}

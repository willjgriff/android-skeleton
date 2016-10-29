package com.github.willjgriff.skeleton.data;

import android.support.annotation.NonNull;

import com.github.willjgriff.skeleton.data.storage.updaters.RealmUpdater;

import io.realm.RealmModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Will on 14/08/2016.
 */
public class NetworkFetchAndUpdateItem<RETURNTYPE extends RealmModel> {

	private Observable<RETURNTYPE> mRetrofitObservable;
	private RealmUpdater<RETURNTYPE> mRealmUpdater;

	public NetworkFetchAndUpdateItem(@NonNull Observable<RETURNTYPE> networkCall, @NonNull RealmUpdater<RETURNTYPE> realmUpdater) {
		mRetrofitObservable = networkCall;
		mRealmUpdater = realmUpdater;
	}

	public Observable<RETURNTYPE> fetchAndUpdateData() {

		return mRetrofitObservable
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.filter(returntype -> returntype != null)
			.doOnNext(returnData -> mRealmUpdater.update(returnData));
	}
}

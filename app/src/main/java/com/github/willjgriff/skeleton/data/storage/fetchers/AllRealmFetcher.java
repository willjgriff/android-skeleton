package com.github.willjgriff.skeleton.data.storage.fetchers;

import com.github.willjgriff.skeleton.data.models.helpers.Timestamp;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Will on 14/08/2016.
 */
// TODO: This is now coupled to the Timestamp interface. See if we can find a way to avoid this.
public class AllRealmFetcher<RETURNTYPE extends RealmModel & Timestamp> extends RealmFetcher<RETURNTYPE> {

	private Class<RETURNTYPE> mReturnClass;
	private Realm mRealm;

	public AllRealmFetcher(Class<RETURNTYPE> returnClass, Realm realm) {
		mReturnClass = returnClass;
		mRealm = realm;
	}

	protected RealmQuery<RETURNTYPE> select() {
		return mRealm.where(mReturnClass);
	}

	@Override
	public RealmResults<RETURNTYPE> fetchCurrentData() {
		return select().lessThan(Timestamp.TIMESTAMP_FIELD, System.currentTimeMillis()).findAll();
	}

	@Override
	public Observable<RealmResults<RETURNTYPE>> getAsyncObservable() {

		return select().findAllAsync()
			.asObservable()
			.subscribeOn(AndroidSchedulers.mainThread())
			.observeOn(AndroidSchedulers.mainThread())
			// Ensure data is valid and available
			.filter(RealmResults::isLoaded)
			.filter(RealmResults::isValid);
	}
}

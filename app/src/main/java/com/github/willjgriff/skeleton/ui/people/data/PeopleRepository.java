package com.github.willjgriff.skeleton.ui.people.data;

import android.support.annotation.NonNull;

import com.github.willjgriff.skeleton.data.utils.customtransformers.TakeUntilNetwork;
import com.github.willjgriff.skeleton.data.models.Person;
import com.github.willjgriff.skeleton.data.utils.response.ResponseHolder;
import com.github.willjgriff.skeleton.ui.people.data.datasources.PeopleNetworkDataSource;
import com.github.willjgriff.skeleton.ui.people.data.datasources.PeopleStorageDataSource;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Will on 06/09/2016.
 */
public class PeopleRepository {

	private PeopleStorageDataSource mPeopleStorageDataSource;
	private PeopleNetworkDataSource mPeopleNetworkDataSource;
	private PublishSubject<Void> mNetworkFetchTrigger;

	public PeopleRepository(@NonNull PeopleStorageDataSource peopleStorageDataSource,
	                        @NonNull PeopleNetworkDataSource peopleNetworkDataSource) {
		mPeopleStorageDataSource = peopleStorageDataSource;
		mPeopleNetworkDataSource = peopleNetworkDataSource;
		mNetworkFetchTrigger = PublishSubject.create();
	}

	public Observable<ResponseHolder<List<Person>>> getPeopleObservable(int countPeople) {
		return Observable
			.merge(getPeopleFromNetworkTrigger(countPeople), mPeopleStorageDataSource.getPeopleFromStorage())
			.compose(new TakeUntilNetwork<>())
			.replay(1)
			.autoConnect();
	}

	private Observable<ResponseHolder<List<Person>>> getPeopleFromNetworkTrigger(int countPeople) {
		return mNetworkFetchTrigger
			.startWith((Void) null)
			.flatMap(aVoid -> getPeopleFromNetwork(countPeople));
	}

	private Observable<ResponseHolder<List<Person>>> getPeopleFromNetwork(int countPeople) {
		return mPeopleNetworkDataSource.getPeopleFromNetwork(countPeople).doOnNext(listResponseHolder -> {
				if (listResponseHolder.hasData()) {
					mPeopleStorageDataSource.savePeopleToStorage(listResponseHolder.getData());
				}
			});
	}

	public void triggerNetworkUpdate() {
		mNetworkFetchTrigger.onNext(null);
	}

	public void closeDataManager() {
		mPeopleStorageDataSource.close();
	}

}
package com.github.willjgriff.skeleton.ui.people2.mvp;

import com.github.willjgriff.skeleton.data.ListCacheRepository;
import com.github.willjgriff.skeleton.data.PeopleQuery;
import com.github.willjgriff.skeleton.data.models.Person;
import com.github.willjgriff.skeleton.mvp.listmvp.ListMvpPresenter;
import com.github.willjgriff.skeleton.ui.people.di.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Will on 12/11/2016.
 */

@FragmentScope
public class People2Presenter extends ListMvpPresenter<Person, People2View, PeopleQuery> {

	private ListCacheRepository<Person, PeopleQuery> mPersonListCacheRepository;

	@Inject
	People2Presenter(ListCacheRepository<Person, PeopleQuery> personListCacheRepository) {
		mPersonListCacheRepository = personListCacheRepository;
	}

	@Override
	protected ListCacheRepository<Person, PeopleQuery> getRepository() {
		return mPersonListCacheRepository;
	}

	@Override
	protected Observable<List<Person>> getDataObservable() {
		return mPersonListCacheRepository.getData(new PeopleQuery.Builder()
			.withNumberOfPeople(20)
			.build());
	}
}
package com.github.willjgriff.skeleton.ui.people2.di;

import com.github.willjgriff.skeleton.data.ListCacheRepository;
import com.github.willjgriff.skeleton.data.PeopleQuery;
import com.github.willjgriff.skeleton.data.models.Person;
import com.github.willjgriff.skeleton.data.network.services.PeopleService;
import com.github.willjgriff.skeleton.data.storage.BasicDiskDataSource;
import com.github.willjgriff.skeleton.ui.people.di.FragmentScope;
import com.github.willjgriff.skeleton.ui.people2.data.People2NetworkDataSource;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by Will on 23/12/2016.
 */
@Module
public class People2Module {

	@Provides
	@FragmentScope
	@Named("people2_realm")
	Realm providesRealm(Realm realm) {
		return realm;
	}

	@Provides
	@FragmentScope
	BasicDiskDataSource<Person, PeopleQuery> providesDiskDataSource(@Named("people2_realm") Realm realm) {
		return new BasicDiskDataSource<>(realm, Person.class);
	}

	@Provides
	@FragmentScope
	People2NetworkDataSource providesNetworkDataSource(PeopleService peopleService) {
		return new People2NetworkDataSource(peopleService);
	}

	@Provides
	@FragmentScope
	ListCacheRepository<Person, PeopleQuery> providesRepository(BasicDiskDataSource<Person, PeopleQuery> people2DiskDataSource, People2NetworkDataSource people2NetworkDataSource) {
		return new ListCacheRepository<>(people2NetworkDataSource, people2DiskDataSource);
	}
}
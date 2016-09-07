package com.github.willjgriff.skeleton.data.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Will on 06/09/2016.
 */

public class People extends RealmObject {

	@PrimaryKey
	private int id;

	@SerializedName("results")
	private RealmList<Person> people;

	public RealmList<Person> getPeople() {
		return people;
	}

	public void setPeople(RealmList<Person> people) {
		this.people = people;
	}
}

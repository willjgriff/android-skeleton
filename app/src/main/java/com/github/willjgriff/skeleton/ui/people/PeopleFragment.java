package com.github.willjgriff.skeleton.ui.people;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.willjgriff.skeleton.R;
import com.github.willjgriff.skeleton.data.models.Person;
import com.github.willjgriff.skeleton.mvp.RxFragment;
import com.github.willjgriff.skeleton.ui.ErrorDisplayer;
import com.github.willjgriff.skeleton.ui.people.di.PeopleInjector;
import com.github.willjgriff.skeleton.ui.people.viewholders.PeopleItemViewHolder.PeopleListener;
import com.github.willjgriff.skeleton.ui.navigation.DetailFragmentListener;
import com.github.willjgriff.skeleton.ui.navigation.NavigationToolbarListener;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import static com.github.willjgriff.skeleton.ui.people.PersonDetailsActivity.ARG_PERSON_FOR_ACTIVITY;

/**
 * Created by Will on 17/08/2016.
 */
public class PeopleFragment extends RxFragment<PeoplePresenter> implements PeopleListener {

	@Inject
	PeoplePresenter mPresenter;

	private PeopleAdapter mPeopleAdapter;
	private NavigationToolbarListener mToolbarListener;
	private DetailFragmentListener mDetailFragmentListener;
	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mToolbarListener = (NavigationToolbarListener) context;
		mDetailFragmentListener = (DetailFragmentListener) context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PeopleInjector.INSTANCE.getComponent().inject(this);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_people, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupView(view);
		setupSubscriptions();
	}

	private void setupView(View view) {
		RecyclerView peopleRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_people_recycler_view);
		mPeopleAdapter = new PeopleAdapter();
		peopleRecyclerView.setAdapter(mPeopleAdapter);
		peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mToolbarListener.setToolbarTitle(R.string.fragment_people_title);
		mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_people_progress_bar);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_people_swipe_refresh);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);

		showCacheLoading();
		showNetworkLoading();
	}

	private void setupSubscriptions() {
		RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout).subscribe(aVoid -> {
			// TODO: Try to minimise this closing of fragment
			mDetailFragmentListener.closeDetailFragment();
			mPresenter.triggerRefreshFetch();
		});

		addSubscription(mPresenter.getPeopleList().subscribe(this::setPeople));

		addSubscription(mPresenter.getCacheLoaded().subscribe(aBoolean -> {
			hideCacheLoading();
		}));

		addSubscription(mPresenter.getNetworkLoaded().subscribe(aBoolean -> {
			hideNetworkLoading();
			hideCacheLoading();
		}));

		addSubscription(mPresenter.getCacheErrors().subscribe(throwable -> {
			showCacheError();
			hideCacheLoading();
		}));

		addSubscription(mPresenter.getNetworkErrors().subscribe(throwable -> {
			showNetworkError(throwable);
			hideNetworkLoading();
			hideCacheLoading();
		}));

		mDetailFragmentListener.closeDetailFragment();
		mPresenter.triggerInitialFetch();
	}

	private void showCacheLoading() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void showNetworkLoading() {
		mToolbarListener.showNetworkLoadingView();
	}

	private void hideCacheLoading() {
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	private void hideNetworkLoading() {
		mToolbarListener.hideNetworkLoadingView();
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void showCacheError() {
		Snackbar.make(getView(), R.string.fragment_people_cache_error_string, Snackbar.LENGTH_LONG).show();
	}

	private void showNetworkError(Throwable throwable) {
		ErrorDisplayer.displayNetworkError(getView(), throwable);
	}

	@Override
	public void onDestroyView() {
		mToolbarListener.hideNetworkLoadingView();
		super.onDestroyView();
	}

	@Override
	protected PeoplePresenter getPresenter() {
		return mPresenter;
	}

	private void setPeople(List<Person> people) {
		mPeopleAdapter.setPeople(people, this);
	}

	@Override
	public void openPersonDetails(Person person) {
		// TODO: We could move this logic to the NavigationActivity if we have a secondary activity that accepts a Fragment
		if (mDetailFragmentListener.isTwoPaneView()) {
			mDetailFragmentListener.openDetailFragment(PersonDetailsFragment.createInstance(person));
		} else {
			Intent personDetailsIntent = new Intent(getContext(), PersonDetailsActivity.class);
			personDetailsIntent.putExtra(ARG_PERSON_FOR_ACTIVITY, person);
			startActivity(personDetailsIntent);
		}
	}

}
package com.example.ecopath.ui.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ecopath.R;
import com.example.ecopath.di.Injectable;

import dagger.android.AndroidInjection;

public class CategoryFragment extends Fragment implements Injectable {
    private Toolbar appToolbar;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_fragment, container, false);

        assert getArguments() != null;
        String categoryName = getArguments().getString("category_name");

        appToolbar = (Toolbar) rootView.findViewById(R.id.include_toolbar);
        appToolbar.setTitle(categoryName);
        appToolbar.setSubtitle("");

        return rootView;
    }
}

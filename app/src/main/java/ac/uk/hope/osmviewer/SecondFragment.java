package ac.uk.hope.osmviewer;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import ac.uk.hope.osmviewer.OrientableMapView.OrientableMapView;
import ac.uk.hope.osmviewer.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // handle shared element transition
        setSharedElementEnterTransition(
                TransitionInflater
                        .from(requireActivity())
                        .inflateTransition(R.transition.change_bounds)
        );
        setSharedElementReturnTransition(
                TransitionInflater
                        .from(requireActivity())
                        .inflateTransition(R.transition.change_bounds)
        );

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // instantiate transition for drawer opening
        OrientableMapView mMap = (OrientableMapView) view.findViewById(R.id.map);

        binding.buttonSecond.setOnClickListener(v -> {
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(mMap, "fullscreenMap")
                    .build();
            NavHostFragment.findNavController(SecondFragment.this).navigate(
                    R.id.action_SecondFragment_to_FirstFragment,
                    null,
                    null,
                    extras);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
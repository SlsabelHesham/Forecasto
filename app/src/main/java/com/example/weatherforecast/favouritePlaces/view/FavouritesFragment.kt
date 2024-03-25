package com.example.weatherforecast.favouritePlaces.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FragmentFavouritesBinding
import com.example.weatherforecast.dp.LocationLocalDataSourceImplementation
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModel
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModelFactory
import com.example.weatherforecast.model.LatLng
import com.example.weatherforecast.model.LocationRepositoryImplementation
import com.example.weatherforecast.network.LocationRemoteDataSourceImplementation

class FavouritesFragment : Fragment(), OnDeletePlaceClickListener {
    private lateinit var favouritePlacesViewModelFactory: FavouritePlacesViewModelFactory
    private lateinit var favouritePlacesViewModel: FavouritePlacesViewModel

    private lateinit var favouritePlacesAdapter: FavouritePlacesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container,false)
        favouritePlacesViewModelFactory = FavouritePlacesViewModelFactory(
            LocationRepositoryImplementation.getInstance(
                LocationRemoteDataSourceImplementation(),
                LocationLocalDataSourceImplementation(requireContext())
            )
        )
        favouritePlacesViewModel = ViewModelProvider(
            this,
            favouritePlacesViewModelFactory
        )[FavouritePlacesViewModel::class.java]

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        favouritePlacesAdapter = FavouritePlacesAdapter(this)

        binding.favRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = favouritePlacesAdapter
        }
        favouritePlacesViewModel.places.observe(this) { places ->
            favouritePlacesAdapter.submitList(places)
        }
        return binding.root
    }

    override fun onDeletePlaceClick(latLng: LatLng) {
        favouritePlacesViewModel.deletePlace(latLng)
    }
}
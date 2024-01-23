package com.example.storyapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.viewmodel.DetailViewModel
import com.example.storyapp.databinding.FragmentDetailBinding
import com.example.storyapp.ui.HomeFragment
import com.google.android.material.appbar.MaterialToolbar

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater,container,false)
        val view = binding.root
        val materialToolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(materialToolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataId = arguments?.getString(HomeFragment.EXTRA_ID_DETAIL)
        val dataName = arguments?.getString(HomeFragment.EXTRA_NAME_DETAIL)
        val dataImage = arguments?.getString(HomeFragment.EXTRA_IMAGE_DETAIL)
        val dataDescription = arguments?.getString(HomeFragment.EXTRA_DESCRIPTION_DETAIL)
        viewModel.getToken().observe(viewLifecycleOwner){ token ->
            if (dataId != null) {
                viewModel.detailStories(token, dataId).observe(viewLifecycleOwner){
                    if (it != null) {
                        when (it) {
                            is com.example.storyapp.data.Result.Success-> {
                                showLoading(false)
                                binding.tvUserDetail.text = dataName
                                binding.tvDescriptionDetail.text = dataDescription
                                Glide.with(this@DetailFragment)
                                    .load(dataImage)
                                    .into(binding.ivPhotoDetail)
                            }

                            is com.example.storyapp.data.Result.Loading -> {
                                showLoading(true)
                            }

                            is com.example.storyapp.data.Result.Error -> {
                                showLoading(false)
                                Toast.makeText(requireContext(), "Failed! ${it.error}", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_detailFragment_to_logInFragment)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
            R.id.menu_logout -> {
                viewModel.saveToken("")
                viewModel.saveName("")
                Toast.makeText(requireContext(), "Your session has ended!", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_map -> {
                findNavController().navigate(R.id.action_detailFragment_to_mapsFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
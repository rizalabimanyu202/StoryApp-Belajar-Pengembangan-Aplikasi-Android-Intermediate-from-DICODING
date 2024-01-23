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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.viewmodel.HomeViewModel
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.StoryAdapter
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    companion object{
        val EXTRA_ID_DETAIL = "ID_DETAIL"
        val EXTRA_NAME_DETAIL = "NAME_DETAIL"
        val EXTRA_IMAGE_DETAIL = "IMAGE_DETAIL"
        val EXTRA_DESCRIPTION_DETAIL = "DESCRIPTION_DETAIL"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        val view = binding.root
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storyAdapter = StoryAdapter()
        val materialToolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(materialToolbar)
        val dataToken = arguments?.getString(LogInFragment.EXTRA_TOKEN)
        val dataName = arguments?.getString(LogInFragment.EXTRA_NAME)
        if (dataToken != null) {
            viewModel.saveToken(dataToken)
        }
        if (dataName != null) {
            viewModel.saveName(dataName)
        }
        interactiveHome(view)
        binding.fabAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_storyAddFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.saveToken("")
                viewModel.saveName("")
                Toast.makeText(requireContext(), "Your session has ended!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
            }
            R.id.menu_map -> {
                findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun interactiveHome(view: View) {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        val storyAdapter = StoryAdapter()
        binding.apply {
            recycleViewMain.layoutManager = LinearLayoutManager(context)
            recycleViewMain.setHasFixedSize(true)
            recycleViewMain.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
        viewModel.getToken().observe(viewLifecycleOwner){ token ->
            viewModel.getName().observe(viewLifecycleOwner){ name ->
                actionBar?.title = "Hello, ${name}"
                viewModel.getStories(token).observe(viewLifecycleOwner) {
                    if (it != null) {
                        binding.apply {
                            storyAdapter.submitData(lifecycle, it)
                        }
                        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ListStoryItem) {
                                val mBundle = Bundle()
                                mBundle.putString(EXTRA_ID_DETAIL, data.id)
                                mBundle.putString(EXTRA_NAME_DETAIL, data.name)
                                mBundle.putString(EXTRA_IMAGE_DETAIL, data.photoUrl)
                                mBundle.putString(EXTRA_DESCRIPTION_DETAIL, data.description)
                                view.findNavController().navigate(R.id.action_homeFragment_to_detailFragment, mBundle)
                            }
                        })
                    }else{
                        Toast.makeText(requireContext(), "Failed Viewed Story!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
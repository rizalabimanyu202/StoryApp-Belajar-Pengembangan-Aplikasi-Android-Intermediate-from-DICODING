package com.example.storyapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.utils.createCustomTempFile
import com.example.storyapp.data.utils.getImageUri
import com.example.storyapp.data.utils.reduceFileImage
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.requestdata.RequestNewStory
import com.example.storyapp.data.viewmodel.StoryAddViewModel
import com.example.storyapp.databinding.FragmentStoryAddBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class StoryAddFragment : Fragment() {

    private lateinit var binding: FragmentStoryAddBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<StoryAddViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryAddBinding.inflate(inflater,container,false)
        val view = binding.root
        val materialToolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(materialToolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getMyLastLocation()
        binding.buttonGallery.setOnClickListener {
            startGallery()
        }
        binding.buttonCamera.setOnClickListener {
            startCamera()
        }
        binding.buttonUpload.setOnClickListener {
            viewModel.getToken().observe(viewLifecycleOwner){ token ->
                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
                    val description = binding.inputDescription.text.toString()
                    val lat = viewModel.latitute.value
                    val lon = viewModel.longitude.value
                    if (binding.checkboxMylocation.isChecked) {
                        viewModel.addStoriesWithLocation(
                            token,
                            RequestNewStory(imageFile, description, lat, lon)
                        ).observe(viewLifecycleOwner) {
                            if (it != null) {
                                when (it) {
                                    is com.example.storyapp.data.Result.Success -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            requireContext(),
                                            "Succesfully! You Add Story ${it.data.message} with location!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        findNavController().navigate(R.id.action_storyAddFragment_to_homeFragment)
                                    }

                                    is com.example.storyapp.data.Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is com.example.storyapp.data.Result.Error -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed! ${it.error}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> {

                                    }
                                }
                            }
                        }
                    } else {
                        viewModel.addStoriesWithoutLocation(
                            token,
                            RequestNewStory(imageFile, description, null, null)
                        ).observe(viewLifecycleOwner) {
                            if (it != null) {
                                when (it) {
                                    is com.example.storyapp.data.Result.Success -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            requireContext(),
                                            "Succesfully! You Add Story ${it.data.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        findNavController().navigate(R.id.action_storyAddFragment_to_homeFragment)
                                    }

                                    is com.example.storyapp.data.Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is com.example.storyapp.data.Result.Error -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed! ${it.error}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageContainer.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {

                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.setLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location is not found. Try Again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
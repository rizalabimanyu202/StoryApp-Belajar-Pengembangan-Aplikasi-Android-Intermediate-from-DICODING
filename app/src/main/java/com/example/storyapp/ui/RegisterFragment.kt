package com.example.storyapp.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.requestdata.RequestUser
import com.example.storyapp.data.viewmodel.RegisterViewModel
import com.example.storyapp.databinding.FragmentSignInBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializedAnimtaion()
        binding.buttonGoToLogin.setOnClickListener {
            val name = binding.textInputEditTextName.text.toString()
            val email = binding.textInputEditTextEmail.text.toString()
            val password = binding.textInputEditTextPassword.text.toString()
            interactiveRegister(view, name, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun initializedAnimtaion() {
        val logo = ObjectAnimator.ofFloat(binding.logo, View.ALPHA, 1f).setDuration(500)
        val tv1 = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val textInputLayoutName = ObjectAnimator.ofFloat(binding.textInputLayoutName, View.ALPHA, 1f).setDuration(500)
        val textInputLayoutEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val textInputLayoutPassword = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val buttonGoToLogin = ObjectAnimator.ofFloat(binding.buttonGoToLogin, View.ALPHA, 1f).setDuration(500)
        val together = AnimatorSet().apply { playTogether(textInputLayoutName, textInputLayoutEmail, textInputLayoutPassword) }
        playAnimation(logo, tv1, together, buttonGoToLogin)
    }

    private fun playAnimation(logo: Animator, tv1: Animator, together: Animator, buttonGoToLogin: Animator){
        AnimatorSet().apply {
            playSequentially(logo, tv1, together, buttonGoToLogin)
            start()
        }
    }

    private fun interactiveRegister(view: View, name: String, email: String, password: String){
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            val dataUser = RequestUser(name, email, password)
            viewModel.getRegister(dataUser).observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        is com.example.storyapp.data.Result.Success-> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Succesfully! ${it.data.message}", Toast.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_signInFragment_to_logInFragment)
                        }

                        is com.example.storyapp.data.Result.Loading -> {
                            showLoading(true)
                        }

                        is com.example.storyapp.data.Result.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Failed! ${it.error}", Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
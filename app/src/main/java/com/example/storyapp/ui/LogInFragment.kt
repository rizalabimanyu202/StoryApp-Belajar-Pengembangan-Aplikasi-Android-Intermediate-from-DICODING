package com.example.storyapp.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.viewmodel.LoginViewModel
import com.example.storyapp.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    companion object{
        val EXTRA_TOKEN = "EXTRA_TOKEN"
        val EXTRA_NAME = "EXTRA_NAME"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLogInBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intializedAnimation()
        validationLogIn(view)
        binding.buttonSignIn.setOnClickListener {
            view.findNavController().navigate(R.id.action_logInFragment_to_signInFragment)
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            interactiveEmail(view, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun intializedAnimation() {
        val logo = ObjectAnimator.ofFloat(binding.logo, View.ALPHA, 1f).setDuration(500)
        val tv1 = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val tv2 = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(500)
        val tv3 = ObjectAnimator.ofFloat(binding.textView3, View.ALPHA, 1f).setDuration(500)
        val textInputLayoutEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val textInputLayoutPassword = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val buttonLogIn = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(500)
        val buttonSignIn = ObjectAnimator.ofFloat(binding.buttonSignIn, View.ALPHA, 1f).setDuration(500)
        val together = AnimatorSet().apply { playTogether(tv1, tv2) }
        val together2 = AnimatorSet().apply { playTogether(textInputLayoutEmail, textInputLayoutPassword) }
        val together3 = AnimatorSet().apply { playTogether(tv3, buttonSignIn) }
        playAnimation(logo, together, together2, buttonLogIn, together3)
    }

    private fun playAnimation(logo: Animator, together: Animator, together2: Animator, buttonLogIn: Animator, together3: Animator){
        AnimatorSet().apply {
            playSequentially(logo, together, together2, buttonLogIn, together3)
            start()
        }
    }

    private fun interactiveEmail(view: View, email: String, password:String){
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModel.getLogin(email, password).observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        is com.example.storyapp.data.Result.Success-> {
                            val mBundle = Bundle()
                            mBundle.putString(EXTRA_TOKEN, it.data.loginResult.token)
                            mBundle.putString(EXTRA_NAME, it.data.loginResult.name)
                            viewModel.saveToken(it.data.loginResult.token)
                            viewModel.saveName(it.data.loginResult.name)
                            showLoading(false)
                            Toast.makeText(requireContext(), "Succesfully! Welcome ${it.data.loginResult.name}", Toast.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_logInFragment_to_homeFragment, mBundle)
                        }

                        is com.example.storyapp.data.Result.Loading -> {
                            showLoading(true)
                        }

                        is com.example.storyapp.data.Result.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), "Failed! ${it.error}", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun validationLogIn(view: View){
        viewModel.getToken().observe(viewLifecycleOwner){ token ->
            viewModel.getName().observe(viewLifecycleOwner){ name ->
                if(token != "" && name != ""){
                    view.findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                }
            }
        }
    }
}
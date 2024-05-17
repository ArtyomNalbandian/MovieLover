package com.example.movielover.view.authorization

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movielover.R
import com.example.movielover.databinding.FragmentSignInBinding
import com.example.movielover.viewModel.SearchViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        viewModel.getMoviesByGenre("криминал")
        viewModel.getMoviesByGenre("триллер")
        viewModel.getMoviesByGenre("боевик")
        viewModel.getMoviesByGenre("мелодрама")
        viewModel.getMoviesByGenre("драма")
        viewModel.getMoviesByGenre("фантастика")
        viewModel.getAnimeSeriesCartoon()

//        viewModel.downloadMyUserInfo()
        viewModel.downloadFavouriteMovies()
        viewModel.getMySubscriptions()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.signInBtn.setOnClickListener {
            if (mBinding.loginSignInET.text.toString().isEmpty() || mBinding.passwordSignInET.text.toString().isEmpty()) {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("testLog", "else scope --- ${mBinding.loginSignInET.text.toString()} - ${mBinding.passwordSignInET.text.toString()}")
                loginUser()
            }
        }

        mBinding.signInTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (FirebaseAuth.getInstance().currentUser != null) {
//            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
//        }
//    }

    private fun loginUser() {
        val login = mBinding.loginSignInET.text.toString()
        val password = mBinding.passwordSignInET.text.toString()

        viewModel.loginUser(login, password, onSuccess = {
            Log.d("testLog", "successed signIn")
            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
        })
    }
}
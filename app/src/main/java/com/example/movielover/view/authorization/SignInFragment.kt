package com.example.movielover.view.authorization

import android.os.Bundle
import android.text.InputType
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

        /////////////////////////////////////////////////
        viewModel.getMoviesByGenre("криминал")   //
        viewModel.getMoviesByGenre("триллер")    // перенести на splash экран
        viewModel.getMoviesByGenre("боевик")     //
        viewModel.getMoviesByGenre("мелодрама")  //
        viewModel.getMoviesByGenre("драма")      //
        viewModel.getMoviesByGenre("фантастика") //
        viewModel.getAnimeSeriesCartoon()              //

        viewModel.getMySubscriptions()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.signInBtn.setOnClickListener {
            if(mBinding.loginSignInET.text.toString().isEmpty() || mBinding.passwordSignInED.text.toString().isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(mBinding.loginSignInET.text.toString().trim(), mBinding.passwordSignInED.text.toString().trim())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
                        }
                    }
            }
        }

        mBinding.showPassword.setOnClickListener {
            if (mBinding.passwordSignInED.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                mBinding.passwordSignInED.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                mBinding.passwordSignInED.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            mBinding.passwordSignInED.setSelection(mBinding.passwordSignInED.text.length)
        }

        mBinding.signInAsGuestBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Log.d("testLog", "current user after logout is --- ${FirebaseAuth.getInstance()}")
            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
        }

        mBinding.signInTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

}
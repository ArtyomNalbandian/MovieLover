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
import com.example.movielover.databinding.FragmentSignUpBinding
import com.example.movielover.viewModel.SearchViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.signUpBtn.setOnClickListener {
            if (isFieldsEmpty()) {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                createAccount()
            }
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return mBinding.emailSignUpET.text.toString().isEmpty() ||
                mBinding.loginSignUpET.text.toString().isEmpty() ||
                mBinding.passwordSignUpET.text.toString().isEmpty()
    }

    private fun createAccount() {
        val email = mBinding.emailSignUpET.text.toString().trim()
        val login = mBinding.loginSignUpET.text.toString().trim()
        val password = mBinding.passwordSignUpET.text.toString().trim()

        viewModel.createAccount(email, password, login,
            onSuccess = {
                Log.d("testLog", "onSuccessSignUp")
                Toast.makeText(context, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            },
            onError = {
                Log.d("testLog", "onErrorSignUp")
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            })
        FirebaseAuth.getInstance().signOut()
    }

}
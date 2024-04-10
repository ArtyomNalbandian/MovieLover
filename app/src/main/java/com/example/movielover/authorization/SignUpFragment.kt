package com.example.movielover.authorization

import android.os.Bundle
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
import com.example.movielover.searchmovie.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
                viewModel.createAccount(
                    mBinding.emailSignUpET,
                    mBinding.passwordSignUpET,
                    mBinding.loginSignUpET)
                Toast.makeText(context, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return mBinding.loginSignUpET.text.toString().isEmpty() ||
                mBinding.passwordSignUpET.text.toString().isEmpty() ||
                mBinding.emailSignUpET.text.toString().isEmpty()
    }

    /*private fun registerUser() {

        val email = mBinding.emailSignUpET.text.toString()
        val password = mBinding.passwordSignUpET.text.toString()
        val login = mBinding.loginSignUpET.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "uid" to user.uid,
                            "email" to email,
                            "login" to login,
                            "profileImage" to ""
                        )

                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .setValue(userInfo)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                                } else {
                                    Toast.makeText(context, "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Ошибка при регистрации пользователя", Toast.LENGTH_SHORT).show()
                }
            }

    }*/

}
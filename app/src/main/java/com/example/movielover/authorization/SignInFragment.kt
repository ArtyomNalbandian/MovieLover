package com.example.movielover.authorization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.movielover.R
import com.example.movielover.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.signInBtn.setOnClickListener {
            if(mBinding.loginSignInET.text.toString().isEmpty() || mBinding.passwordSignInED.text.toString().isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(mBinding.loginSignInET.text.toString(), mBinding.passwordSignInED.text.toString())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
                        }
                    }
            }
        }

        mBinding.signInTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
        }
    }

}
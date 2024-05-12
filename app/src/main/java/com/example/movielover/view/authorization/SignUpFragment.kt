package com.example.movielover.view.authorization

import android.os.Bundle
import android.text.InputType
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

        mBinding.showPassword.setOnClickListener {
            if (mBinding.passwordSignUpET.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                mBinding.passwordSignUpET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                mBinding.passwordSignUpET.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            mBinding.passwordSignUpET.setSelection(mBinding.passwordSignUpET.text.length)
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return mBinding.loginSignUpET.text.toString().isEmpty() ||
                mBinding.passwordSignUpET.text.toString().isEmpty() ||
                mBinding.emailSignUpET.text.toString().isEmpty()
    }

}
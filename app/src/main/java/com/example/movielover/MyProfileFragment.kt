package com.example.movielover

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movielover.databinding.FragmentHomePageBinding
import com.example.movielover.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }
}
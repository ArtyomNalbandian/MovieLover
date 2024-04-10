package com.example.movielover.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.movielover.databinding.FragmentMyProfileBinding
import com.example.movielover.searchmovie.SearchViewModel

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var adapter: ProfileMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }
}
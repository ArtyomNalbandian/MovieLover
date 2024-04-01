package com.example.movielover.searchmovie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentSearchBinding
import java.util.Currency

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = SearchAdapter(this, viewModel)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerView.adapter = adapter

        viewModel.getLiveData().observe(viewLifecycleOwner) {
            adapter.moviesList = viewModel.getLiveData().value!!
            adapter.updateData()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.setSearchText(newText)
                }
                return true
            }

        })

    }
}
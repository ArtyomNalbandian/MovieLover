package com.example.movielover.searchmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.movielover.R
import com.example.movielover.databinding.FragmentMovieCardBinding
import com.example.movielover.repository.dataclasses.Doc
import com.squareup.picasso.Picasso

class MovieCardFragment : Fragment() {

    private var _binding: FragmentMovieCardBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var currentMovie: Doc
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMovieCardBinding.inflate(inflater, container, false)

        currentMovie = viewModel.getMoreDocPostAboutFragment()!!
        mBinding.textViewMovieCard.text = currentMovie.name
        mBinding.movieCardYear.text = currentMovie.year.toString()
        mBinding.movieCardDescription.text = currentMovie.description
        mBinding.movieCardCountry.text = currentMovie.countries[0].name
        Picasso.get().load(currentMovie.poster.previewUrl).into(mBinding.movieCardPoster)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backFromMovieCardBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        mBinding.addToMyFavouriteBtn.setOnClickListener {
            //Функция добавления в список моих фильмов addToMyFavouriteList
            Toast.makeText(context, "Фильм успешно добавлен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
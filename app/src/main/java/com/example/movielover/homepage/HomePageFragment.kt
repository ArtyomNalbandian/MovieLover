package com.example.movielover.homepage

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentHomePageBinding
import com.example.movielover.profile.ProfileMoviesAdapter
import com.example.movielover.searchmovie.SearchViewModel

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var profileMoviesAdapter: ProfileMoviesAdapter
    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var criminalMoviesAdapter: CriminalMoviesAdapter
    private lateinit var thrillerMoviesAdapter: ThrillerMoviesAdapter
    private lateinit var actionMoviesAdapter: ActionMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        handler = Handler()
        ///////////////////
        //БЛОК С ОБЪЯВЛЕНИЯМИ ВСЕХ РЕСАЙКЛЕРОВ
        val criminalRecyclerView = mBinding.homePageCriminalRV
        val thrillerRecyclerView = mBinding.homePageThrillerRV
        val actionRecyclerView   = mBinding.homePageActionRV
        ///////////////////
        //////////////////////////////////////////////////////////////////////
        profileMoviesAdapter = ProfileMoviesAdapter(this, viewModel)//
        //mBinding.  .layoutManager = LinearLayoutManager(context)          //
        //mBinding.profileRecyclerView.adapter = adapter                    //
        //Надо будет потом на splash экран                                  //
        viewModel.downloadFavouriteMovies()                                 //
        //////////////////////////////////////////////////////////////////////

        homePageAdapter = HomePageAdapter(this, viewModel)
        criminalMoviesAdapter = CriminalMoviesAdapter(this, viewModel)
        thrillerMoviesAdapter = ThrillerMoviesAdapter(this, viewModel)
        actionMoviesAdapter   = ActionMoviesAdapter(this, viewModel)
        criminalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        thrillerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        actionRecyclerView.layoutManager   = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        criminalRecyclerView.adapter = criminalMoviesAdapter
        thrillerRecyclerView.adapter = thrillerMoviesAdapter
        actionRecyclerView.adapter   = actionMoviesAdapter
        criminalMoviesAdapter.moviesList = viewModel.getCriminalMoviesList()
        criminalMoviesAdapter.updateData()
        viewModel.getCriminalMoviesLiveData().observe(viewLifecycleOwner) {// если че можно вот так наговнокодить, походу проблема с апишкой а не моим кодом(((
            criminalMoviesAdapter.moviesList = viewModel.getCriminalMoviesLiveData().value!!
            criminalMoviesAdapter.updateData()
        }
        viewModel.getThrillerMoviesLiveData().observe(viewLifecycleOwner) {
            thrillerMoviesAdapter.moviesList = viewModel.getThrillerMoviesLiveData().value!!
            thrillerMoviesAdapter.updateData()
        }
        viewModel.getActionMoviesLiveData().observe(viewLifecycleOwner) {
            actionMoviesAdapter.moviesList = viewModel.getActionMoviesLiveData().value!!
            actionMoviesAdapter.updateData()
        }

        Log.d("testLog", "${viewModel.getMoviesByGenre("криминал")}")

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ////////////////////////////////////////////////
        viewModel.getMoviesByGenre("криминал")  //
        viewModel.getMoviesByGenre("триллер")   // перенести на splash экран
        viewModel.getMoviesByGenre("боевик")    //
        ////////////////////////////////////////////////
        mBinding.homePageSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getMoviesByGenre("криминал")
            viewModel.getMoviesByGenre("триллер")
            viewModel.getMoviesByGenre("боевик")
            Handler().postDelayed({
                mBinding.homePageSwipeRefreshLayout.isRefreshing = false
            }, 1700)
        }

        profileMoviesAdapter.updateData()
    }
}
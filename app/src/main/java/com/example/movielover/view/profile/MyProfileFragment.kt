package com.example.movielover.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentMyProfileBinding
import com.example.movielover.viewModel.SearchViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var moviesAdapter: ProfileMoviesAdapter
    private lateinit var usersAdapter: ProfileUsersAdapter

    /////////////////////////////
    private var filePath: Uri? = null
    private lateinit var user: User
    ///////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        moviesAdapter = ProfileMoviesAdapter(this, viewModel)
        usersAdapter = ProfileUsersAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.profileRecyclerView.adapter = moviesAdapter


        moviesAdapter.moviesList = viewModel.getMyFavouriteMoviesList()
        moviesAdapter.updateData()

        viewModel.getMyFavouriteMoviesLiveData().observe(viewLifecycleOwner) {
            moviesAdapter.moviesList = viewModel.getMyFavouriteMoviesLiveData().value!!
            moviesAdapter.updateData()
        }

        usersAdapter.usersList = viewModel.getMySubsList()
        usersAdapter.updateData()

        viewModel.downloadMyUserInfo()
        user = viewModel.getMyUserInfo()

        if (user.email == null) {
            mBinding.profileTabs.visibility = View.GONE
            mBinding.profileRecyclerView.setPadding(0, 0, 0, 800)
        }

        mBinding.profileTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        mBinding.profileRecyclerView.adapter = moviesAdapter
                    }

                    1 -> {
                        mBinding.profileRecyclerView.adapter = usersAdapter
                    }
                }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        mBinding.userNameProfile.text = user.login
        if (user.profileImage != "") {
            Picasso.get().load(user.profileImage).into(mBinding.profileImage)
        }

        return mBinding.root
    }

    private val pickImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null) {
                filePath = result.data!!.data

                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver, filePath
                    )
                    mBinding.profileImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewModel.uploadImage(filePath)
                Toast.makeText(context, "Фото профиля обновлено!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.profileImage.setOnClickListener {
            selectImage()
        }

    }
}
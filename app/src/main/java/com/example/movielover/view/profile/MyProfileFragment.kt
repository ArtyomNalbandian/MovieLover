package com.example.movielover.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var adapter: ProfileMoviesAdapter

    /////////////////////////////
    private var filePath: Uri? = null
    private lateinit var user: User
    ///////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        adapter = ProfileMoviesAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.profileRecyclerView.adapter = adapter


        adapter.moviesList = viewModel.getFavouriteMoviesList()// переделать на LiveData
        adapter.updateData()

        viewModel.downloadMyUserInfo()
        user = viewModel.getMyUserInfo()

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
                uploadImage()
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }

    private fun uploadImage() {
        filePath?.let { filePath ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val storageReference = FirebaseStorage.getInstance().getReference("images/$uid")

            storageReference.putFile(filePath)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Фото профиля обновлено",
                        Toast.LENGTH_SHORT
                    ).show()

                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
                            .child("profileImage").setValue(uri.toString())
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.profileImage.setOnClickListener {
            selectImage()
        }

    }
}
package com.example.playlistmaker.library.ui

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMakerBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.MakerViewModel.CreatingPlaylistState
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class MakerFragment : Fragment() {
    private var _binding: FragmentMakerBinding? = null
    private val binding: FragmentMakerBinding get() = requireNotNull(_binding) { }

    private val viewModel: MakerViewModel by viewModel<MakerViewModel>()


    private var newPlaylistTitle = ""
    private var newPlaylistDescription: String? = null
    private var newPlaylistCoverPath: String? = null
    private var isCreationStarted  = false

    private var savedPlaylist: Playlist? = null
    private val args by navArgs <MakerFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jsonPlaylist = args.playlist
        savedPlaylist = Gson().fromJson(jsonPlaylist, Playlist::class.java)

        binding.apply {
            myToolbar.setNavigationOnClickListener {
                checkIfSaveIsNeeded()
            }
            inputPlaylistTitle.addTextChangedListener(getTextWatcher(InputType.TITLE))
            inputPlaylistDescription.addTextChangedListener(getTextWatcher(InputType.DESCRIPTION))

        }

        viewModel.checkIfSavedPlaylistExist(savedPlaylist)
        viewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

        setPhotoPicker()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    checkIfSaveIsNeeded()
                }
            })

    }


    private fun render(state: CreatingPlaylistState) {
        when (state) {
            CreatingPlaylistState.Loading -> showProgressBar()
            CreatingPlaylistState.NewPlaylistState -> setCreatingScreen()
            is CreatingPlaylistState.Success -> processSavingResult(state.message)
            is CreatingPlaylistState.EditingPlaylist -> setEditingScreen(state.playlist)
        }
    }


    private fun setEditingScreen(playlist: Playlist) {
        binding.apply {
            with(playlist) {
                myToolbar.title = getString(R.string.edit)
                buttonCreate.text = getString(R.string.save)

                if (playlist.coverPath != null) {
                    newPlaylistCoverPath = coverPath
                    Glide.with(requireActivity())
                        .load(coverPath)
                        .centerCrop()
                        .into(playlistImage)
                }

                newPlaylistTitle = title
                inputPlaylistTitle.setText(title)
                if (description != null) {
                    inputPlaylistDescription.setText(description)
                    newPlaylistDescription = description
                }

                buttonCreate.setOnClickListener {
                    viewModel.savePlaylist(
                        Playlist(
                            id = id,
                            title = newPlaylistTitle,
                            description = newPlaylistDescription,
                            coverPath = newPlaylistCoverPath,
                            tracks = tracks,
                            tracksQuantity = tracksQuantity
                        )
                    )
                }
            }
        }
    }


    private fun setCreatingScreen() {
        binding.buttonCreate.setOnClickListener {
            viewModel.savePlaylist(
                Playlist(
                    id = 0,
                    title = newPlaylistTitle,
                    description = newPlaylistDescription,
                    coverPath = newPlaylistCoverPath,
                    tracks = listOf(),
                    tracksQuantity = 0
                )
            )
        }
    }


    private fun processSavingResult(message: String?) {
        if (message != null) {
            showToast(message)
        }
        findNavController().navigateUp()
    }


    private fun showProgressBar() {
        binding.apply {
            buttonCreate.isVisible = false
            progressbar.isVisible = true
        }

    }


    private fun setPhotoPicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    isCreationStarted  = true

                    saveImageToPrivateStorage(uri)

                    Glide.with(requireActivity())
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(binding.playlistImage)
                }
            }

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistCovers"
            )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        newPlaylistCoverPath = file.path
    }

    private fun getTextWatcher(inputType: InputType): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmpty = s.isNullOrEmpty()
                isCreationStarted  = !isEmpty

                val strokeColor = getStrokeColor(isEmpty)
                val hintColor = getHintColorStateList(isEmpty)

                binding.apply {
                    when (inputType) {
                        InputType.TITLE -> {
                            fieldPlaylistTitle.apply {
                                boxStrokeColor = strokeColor
                                hintTextColor = hintColor
                            }
                            buttonCreate.isEnabled = !isEmpty
                        }

                        InputType.DESCRIPTION -> {
                            fieldPlaylistDescription.apply {
                                boxStrokeColor = strokeColor
                                hintTextColor = hintColor
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

                when (inputType) {
                    InputType.TITLE -> {
                        newPlaylistTitle = s.toString()
                        binding.buttonCreate.isEnabled = !s.isNullOrEmpty()
                    }

                    InputType.DESCRIPTION -> {
                        newPlaylistDescription = s.toString()

                    }
                }
            }
        }
    }

    private fun getHintColorStateList(isEmpty: Boolean): ColorStateList {

        if (isEmpty) {
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)

            return ColorStateList.valueOf(typedValue.data)
        } else {
            return ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun getStrokeColor(isEmpty: Boolean): Int {

        return ContextCompat.getColor(
            requireContext(),
            if (isEmpty) R.color.grey else R.color.blue
        )
    }

    private fun checkIfSaveIsNeeded() {
        val currentState = viewModel.observeState().value
        if (currentState is CreatingPlaylistState.NewPlaylistState) showExitConfirmationDialog()
        else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {

        if (isCreationStarted ) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.complete))
                .setMessage(getString(R.string.warning))
                .setPositiveButton(getString(R.string.close)) { _, _ ->
                    findNavController().navigateUp()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } else findNavController().navigateUp()
    }

    private fun showToast(title: String) {

        Toast.makeText(requireActivity(), title, Toast.LENGTH_SHORT)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        enum class InputType {
            TITLE,
            DESCRIPTION
        }
    }
}




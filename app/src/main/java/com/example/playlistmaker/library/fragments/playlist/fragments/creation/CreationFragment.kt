package com.example.playlistmaker.library.fragments.playlist.fragments.creation

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreationBinding
import com.example.playlistmaker.library.fragments.playlist.activities.CreationActivity
import com.example.playlistmaker.library.fragments.playlist.fragments.creation.viewmodels.CreationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreationFragment : Fragment(R.layout.fragment_creation) {

    protected open var _binding: FragmentCreationBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: CreationViewModel by viewModel()

    private var titleTextWatcher: TextWatcher? = null
    private var imageUri: Uri? = null

    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imageUri = uri
                viewModel.onImageSelected(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreationBinding.bind(view)

        setupTextWatcher()
        setupClickListeners()
        setupViewModelObservers()
        setupBackButton()
    }

    private fun setupTextWatcher() {
        titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.saveButton.isEnabled = !s.isNullOrEmpty()
            }
        }
        binding.titleField.addTextChangedListener(titleTextWatcher)
    }

    open fun setupClickListeners() {
        binding.imagePlaceholder.setOnClickListener {
            pickImage()
        }
        binding.imagePreview.setOnClickListener {
            pickImage()
        }

        binding.saveButton.setOnClickListener {
            viewModel.createPlaylist(
                name = binding.titleField.text.toString(),
                description = binding.editTextDescription.text.toString()
            )
        }
    }

    private fun pickImage() {
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    open fun setupViewModelObservers() {
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.imagePlaceholder.visibility = View.GONE
                binding.imagePreview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
            } else {
                binding.imagePlaceholder.visibility = View.VISIBLE
                binding.imagePreview.visibility = View.GONE
            }
        }

        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT).show()
            closeScreen()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            handleBackPress()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    open fun handleBackPress() {
        if (isFormDirty()) {
            showConfirmationDialog()
        } else {
            closeScreen()
        }
    }

    private fun isFormDirty(): Boolean {
        return imageUri != null ||
                !binding.titleField.text.isNullOrEmpty() ||
                !binding.editTextDescription.text.isNullOrEmpty()
    }


    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Завершить") { _, _ ->
                closeScreen()
            }
            .show()
    }

    protected fun closeScreen() {
        if (requireActivity() is CreationActivity) {
            requireActivity().finish()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        titleTextWatcher?.let { binding.titleField.removeTextChangedListener(it) }
        _binding = null
    }

    companion object {
        fun newInstance() = CreationFragment()
    }
}
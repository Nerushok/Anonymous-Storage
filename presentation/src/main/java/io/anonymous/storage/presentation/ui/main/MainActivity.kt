package io.anonymous.storage.presentation.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import io.anonymous.storage.R
import io.anonymous.storage.databinding.ActivityMainBinding
import io.anonymous.storage.presentation.base.BaseActivity
import io.anonymous.storage.presentation.ui.document.DocumentContentActivity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observeViewModel()
    }

    override fun setError(error: Exception?) {
        Toast.makeText(this, error?.localizedMessage ?: getString(R.string.text_unknown_error), Toast.LENGTH_LONG).show()
    }

    override fun setLoading(isLoading: Boolean) {
        setLoadingDialogVisibility(isLoading)
    }

    private fun initViews() {
        setListeners()
    }

    private fun observeViewModel() {
        observeLoading(viewModel.loading)
        observeError(viewModel.error)
        viewModel.foundedDocument.observe(this, Observer { it?.let { onDocumentReceived() } })
    }

    private fun setListeners() {
        binding.buttonFindDocument.setOnClickListener {
            val documentKey = binding.editTextDocumentKey.text.toString()
            viewModel.getDocumentByKey(documentKey)
        }
    }

    private fun onDocumentReceived() {
        DocumentContentActivity.start(this)
    }
}
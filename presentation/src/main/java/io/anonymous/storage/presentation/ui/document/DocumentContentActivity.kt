package io.anonymous.storage.presentation.ui.document

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import io.anonymous.storage.R
import io.anonymous.storage.databinding.ActivityDocumentContentBinding
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentPurchasingType
import io.anonymous.storage.presentation.base.BaseActivity
import io.anonymous.storage.presentation.ui.purchase.PurchaseActivity
import io.anonymous.storage.presentation.utils.LiveEventCallback
import io.anonymous.storage.presentation.utils.extentions.toast
import org.koin.android.viewmodel.ext.android.viewModel

class DocumentContentActivity : BaseActivity() {

    private val REQUEST_CODE_PURCHASE_ACTIVITY = 1024

    private val viewModel: DocumentContentViewModel by viewModel()
    private lateinit var binding: ActivityDocumentContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PURCHASE_ACTIVITY && PurchaseActivity.isActivityResultPurchased(resultCode)) {
            viewModel.syncDocumentWithRemote()
            showCongratsDialog()
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setError(error: Exception?) {
        Toast.makeText(this, error?.localizedMessage ?: getString(R.string.text_unknown_error), Toast.LENGTH_LONG).show()
    }

    override fun setLoading(isLoading: Boolean) {
        setLoadingDialogVisibility(isLoading)
    }

    private fun initViews() {
        initAppbar()
        setListeners()
    }

    private fun observeViewModel() {
        observeLoading(viewModel.loading)
        observeError(viewModel.error)
        viewModel.document.observe(this, Observer { updateDocument(it ?: return@Observer) })
        viewModel.isSaveEnabled.observe(this, Observer { updateSaveButtonEnableState(it ?: return@Observer) })
    }

    private fun initAppbar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            onSupportNavigateUp()
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setListeners() {
        binding.textRawData.addTextChangedListener { editable ->
            onDocumentContentChanged(editable ?: return@addTextChangedListener)
        }
        binding.buttonSaveDocument.setOnClickListener { saveDocument() }
        binding.buttonBuyDocument.setOnClickListener { buyDocumentKey() }
    }

    private fun updateDocument(document: Document) {
        updateLifeTimeTimer(document)
        updateLifeTimeType(document)
        with(binding.textRawData) {
            setText(document.rawData)
            setSelection(document.rawData.length)
        }
    }

    private fun updateSaveButtonEnableState(isSaveEnabled: Boolean) {
        binding.buttonSaveDocument.isEnabled = isSaveEnabled
    }

    private fun updateLifeTimeTimer(document: Document) {
        if (document.lifeTimeInDays < 0) {
            binding.layoutLifeTimeTimer.isVisible = false
            return
        }

        val msInDay: Long = 1000 * 60 * 60 * 24
        val lifeTimeInMs: Long = document.lifeTimeInDays * msInDay
        val endOfLifeTimeInMs: Long = document.dateOfCreationTimestamp + lifeTimeInMs
        var daysLeft: Int = ((endOfLifeTimeInMs - System.currentTimeMillis()) / msInDay).toInt()

        if (daysLeft < 0) daysLeft = 0

        binding.textDaysLeft.text = resources.getQuantityString(R.plurals.daysLeft, daysLeft).format(daysLeft)
        binding.layoutLifeTimeTimer.isVisible = true
    }

    private fun updateLifeTimeType(document: Document) {
        binding.imageDocumentPurchased.isVisible = document.documentPurchasingType == DocumentPurchasingType.Purchased
        binding.buttonBuyDocument.isVisible = document.documentPurchasingType == DocumentPurchasingType.Trial
    }

    private fun onDocumentContentChanged(editable: Editable) {
        viewModel.onDocumentContentChanged(editable.toString())
    }

    private fun saveDocument() {
        val documentContent = binding.textRawData.text.toString()
        viewModel.saveDocumentContent(
            documentContent,
            onSuccessCallback = LiveEventCallback(this) {
                toast(R.string.text_saved)
            })
    }

    private fun buyDocumentKey() {
        PurchaseActivity.start(this, viewModel.document.value?.key ?: return, REQUEST_CODE_PURCHASE_ACTIVITY)
    }

    private fun showCongratsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.text_congrats_dialog_title)
            .setMessage(R.string.text_congrats_dialog_content)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }


    companion object {

        fun start(context: Context) {
            val intent = Intent(context, DocumentContentActivity::class.java)
            context.startActivity(intent)
        }
    }
}
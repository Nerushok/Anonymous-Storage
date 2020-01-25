package io.anonymous.storage.presentation.ui.purchase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.android.billingclient.api.SkuDetails
import io.anonymous.storage.databinding.ActivityPurchaseBinding
import io.anonymous.storage.presentation.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class PurchaseActivity : BaseActivity() {

    private val viewModel: PurchaseViewModel by viewModel()

    private lateinit var binding: ActivityPurchaseBinding

    private val documentKey: String by lazy { intent.getStringExtra(EXTRA_DOCUMENT_KEY) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        setListeners()
    }

    private fun observeViewModel() {
        observeLoading(viewModel.loading)
        observeError(viewModel.error)
        viewModel.skuDetails.observe(this, Observer { skuDetails ->
            setPurchaseDetails(skuDetails)
        })
        viewModel.setDocumentKey(documentKey)
    }

    private fun setListeners() {
        binding.buttonCancel.setOnClickListener { onBackPressed() }
        binding.buttonBuy.setOnClickListener { viewModel.purchaseDocumentLifetime(this) }
    }

    private fun setPurchaseDetails(skuDetails: SkuDetails) {
        binding.title.text = skuDetails.title
        binding.textPurchaseDetails.text = skuDetails.description
    }


    companion object {

        private val EXTRA_DOCUMENT_KEY = "document_key"

        fun start(context: Context, documentKey: String) {
            val intent = Intent(context, PurchaseActivity::class.java)
            intent.putExtra(EXTRA_DOCUMENT_KEY, documentKey)
            context.startActivity(intent)
        }
    }
}
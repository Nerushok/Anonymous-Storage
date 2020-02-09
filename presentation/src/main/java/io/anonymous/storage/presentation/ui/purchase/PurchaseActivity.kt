package io.anonymous.storage.presentation.ui.purchase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import io.anonymous.storage.R
import io.anonymous.storage.databinding.ActivityPurchaseBinding
import io.anonymous.storage.presentation.base.BaseActivity
import io.anonymous.storage.presentation.utils.extentions.toast
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

    override fun setLoading(isLoading: Boolean) {
        setLoadingDialogVisibility(isLoading)
    }

    override fun setError(error: Exception?) {
        toast(R.string.text_unknown_error)
    }

    private fun initViews() {
        setListeners()
    }

    private fun observeViewModel() {
        observeLoading(viewModel.loading)
        observeError(viewModel.error)
        viewModel.skuDetails.observe(this, Observer { skuDetails -> setPurchaseDetails(skuDetails) })
        viewModel.successPurchase.observe(this, Observer { purchase -> onSuccessPurchase(purchase) })
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

    private fun onSuccessPurchase(purchase: Purchase) {
        setResult(RESULT_PURCHASED)
        finish()
    }


    companion object {

        private val RESULT_PURCHASED = Activity.RESULT_OK

        private val EXTRA_DOCUMENT_KEY = "document_key"

        fun start(activity: Activity, documentKey: String, requestCode: Int) {
            val intent = Intent(activity, PurchaseActivity::class.java)
            intent.putExtra(EXTRA_DOCUMENT_KEY, documentKey)
            activity.startActivityForResult(intent, requestCode)
        }

        fun isActivityResultPurchased(activityResult: Int): Boolean {
            return activityResult == RESULT_PURCHASED
        }
    }
}
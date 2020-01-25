package io.anonymous.storage.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.android.billingclient.api.SkuDetails
import io.anonymous.storage.databinding.DialogPurchaseBinding

class PurchaseDialog : DialogFragment() {

    private var skuDetails: SkuDetails? = null
    private var dialogListener: DialogListener? = null
    private lateinit var binding: DialogPurchaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPurchaseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun setSkuDetails(skuDetails: SkuDetails): PurchaseDialog {
        this.skuDetails = skuDetails
        return this
    }

    fun setDialogListener(listener: DialogListener): PurchaseDialog {
        dialogListener = listener
        return this
    }

    private fun initViews() {
        binding.title.text = skuDetails?.title ?: ""
        binding.textPurchaseDetails.text = skuDetails?.description ?: ""
        setListeners()
    }

    private fun setListeners() {
        binding.buttonCancel.setOnClickListener { dialogListener?.onCancelClick() }
        binding.buttonBuy.setOnClickListener { dialogListener?.onBuyClick() }
    }


    companion object {

        fun newInstance(skuDetails: SkuDetails, listener: DialogListener): PurchaseDialog {
            return PurchaseDialog()
                .setSkuDetails(skuDetails)
                .setDialogListener(listener)
        }
    }


    interface DialogListener {

        fun onBuyClick()

        fun onCancelClick() {}
    }
}
package io.anonymous.storage.presentation.di

import io.anonymous.storage.data.feature.billing.BillingController
import io.anonymous.storage.data.feature.billing.PurchaseController
import io.anonymous.storage.data.feature.documents.DocumentsRepositoryImpl
import io.anonymous.storage.data.feature.purchase.PurchaseRepositoryImpl
import io.anonymous.storage.data.mapper.DocumentPurchasingTypeMapper
import io.anonymous.storage.data.mapper.DocumentRemoteMapper
import io.anonymous.storage.data.remote.cloud_functions.CloudFunctionsController
import io.anonymous.storage.data.remote.firestore.FirestoreController
import io.anonymous.storage.domain.feature.documents.CreateEmptyDocumentUseCase
import io.anonymous.storage.domain.feature.documents.DocumentsRepository
import io.anonymous.storage.domain.feature.documents.GetDocumentUseCase
import io.anonymous.storage.domain.feature.documents.SaveDocumentContentUseCase
import io.anonymous.storage.domain.feature.purchase.PurchaseRepository
import io.anonymous.storage.domain.feature.purchase.ValidatePurchaseUseCase
import io.anonymous.storage.presentation.common.DocumentLinkHolder
import io.anonymous.storage.presentation.ui.document.DocumentContentViewModel
import io.anonymous.storage.presentation.ui.main.MainViewModel
import io.anonymous.storage.presentation.ui.purchase.PurchaseViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val UseCasesModule = module {
    factory { GetDocumentUseCase(get(), get()) }
    factory { CreateEmptyDocumentUseCase() }
    factory { SaveDocumentContentUseCase(get()) }
    factory { ValidatePurchaseUseCase(get()) }
}

val MappersModule = module {
    factory { DocumentPurchasingTypeMapper() }
    factory { DocumentRemoteMapper(get()) }
}

val ViewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { DocumentContentViewModel(get(), get(), get()) }
    viewModel { PurchaseViewModel(get()) }
}

val DataModule = module {
    single { FirestoreController() }
    single { CloudFunctionsController() }

    single { PurchaseRepositoryImpl(get()) } bind PurchaseRepository::class
    single { DocumentsRepositoryImpl(get(), get(), get()) } bind DocumentsRepository::class

    single { BillingController(get(), get()) } bind PurchaseController::class
}

val PresentationModule = module {
    single { DocumentLinkHolder() }
}
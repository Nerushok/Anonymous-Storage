package io.anonymous.storage.presentation.di

import io.anonymous.storage.data.feature.documents.DocumentsRepositoryImpl
import io.anonymous.storage.data.mapper.DocumentPurchasingTypeMapper
import io.anonymous.storage.data.mapper.DocumentRemoteMapper
import io.anonymous.storage.data.remote.cloud_functions.CloudFunctionsController
import io.anonymous.storage.data.remote.firestore.FirestoreController
import io.anonymous.storage.domain.feature.documents.CreateEmptyDocumentUseCase
import io.anonymous.storage.domain.feature.documents.DocumentsRepository
import io.anonymous.storage.domain.feature.documents.GetDocumentUseCase
import io.anonymous.storage.domain.feature.documents.SaveDocumentContentUseCase
import io.anonymous.storage.presentation.common.DocumentLinkHolder
import io.anonymous.storage.presentation.ui.document.DocumentContentViewModel
import io.anonymous.storage.presentation.ui.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val UseCasesModule = module {
    factory { GetDocumentUseCase(get(), get()) }
    factory { CreateEmptyDocumentUseCase() }
    factory { SaveDocumentContentUseCase(get()) }
}

val MappersModule = module {
    factory { DocumentPurchasingTypeMapper() }
    factory { DocumentRemoteMapper(get()) }
}

val ViewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { DocumentContentViewModel(get(), get()) }
}

val DataModule = module {
    single { FirestoreController() }
    single { CloudFunctionsController() }

    single { DocumentsRepositoryImpl(get(), get(), get()) } bind DocumentsRepository::class
}

val PresentationModule = module {
    single { DocumentLinkHolder() }
}
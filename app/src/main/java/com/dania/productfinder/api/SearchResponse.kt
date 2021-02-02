package com.dania.productfinder.api

import com.google.gson.annotations.SerializedName
import com.dania.productfinder.vo.Product

/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class SearchResponse(
    val status: Status,
    val pageType: String,
    val plpResults: PlpResults
) {
    var nextPage: Int? = null
}

data class PlpResults (
    val label: String,
    val plpState: PlpState,
    val sortOptions: List<Any?>,
    val refinementGroups: List<Any?>,
    val records: List<Product>,
    val navigation: Navigation
)

data class Navigation (
    val ancester: List<Any?>,
    val current: List<Current>,
    val childs: List<Any?>
)

data class Current (
    val label: String,
    @SerializedName("categoryId")
    val categoryID: String
)

data class PlpState (
    @SerializedName("categoryId")
    val categoryID: String,

    val currentSortOption: String,
    val currentFilters: String,
    val firstRecNum: Int,
    val lastRecNum: Int,
    val recsPerPage: Int,
    val totalNumRecs: Int,
    val originalSearchTerm: String
)

data class Status (
    val status: String,
    val statusCode: Long
)

data class PlpRecord (
    @SerializedName("productId")
    val productID: String,

    @SerializedName("skuRepositoryId")
    val skuRepositoryID: String,

    val productDisplayName: String,
    val productType: String,
    val productRatingCount: Int,
    val productAvgRating: Double,
    val listPrice: Double,
    val minimumListPrice: Double,
    val maximumListPrice: Double,
    val promoPrice: Double,
    val minimumPromoPrice: Double,
    val maximumPromoPrice: Double,
    val isHybrid: Boolean,
    val marketplaceSLMessage: String? = null,
    val marketplaceBTMessage: String? = null,
    val isMarketPlace: Boolean,
    val isImportationProduct: Boolean,
    val brand: String,
    val seller: String,
    val category: String,
    val smImage: String,
    val lgImage: String,
    val xlImage: String,
    val groupType: String,
    val plpFlags: List<Any?>,
    val variantsColor: List<VariantsColor>
)

data class VariantsColor (
    val colorName: String,
    val colorHex: String,
    val colorImageURL: String
)
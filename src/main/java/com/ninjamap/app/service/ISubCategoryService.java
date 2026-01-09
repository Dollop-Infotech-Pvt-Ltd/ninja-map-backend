package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SubCategoryRequest;
import com.ninjamap.app.payload.request.UpdateSubCategoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface ISubCategoryService {

	ApiResponse addSubCategory(SubCategoryRequest subCategoryRequest);

	ApiResponse getSubCategories(PaginationRequest paginationRequest);

	ApiResponse getSubCategoryById(String id);

	ApiResponse updateSubCategory(String id, UpdateSubCategoryRequest updateSubCategoryRequest);

	ApiResponse deleteSubCategory(String id);

	ApiResponse getSubCategoriesByCategory(String categoryId, PaginationRequest paginationRequest);

	ApiResponse getAllSubCategoriesAdmin();

}
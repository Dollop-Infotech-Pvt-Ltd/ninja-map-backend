package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface ICategoryService {

	ApiResponse addCategory(CategoryRequest categoryRequest);

	ApiResponse getCategories(PaginationRequest paginationRequest);

	ApiResponse getCategoryById(String id);

	ApiResponse updateCategory(String id, CategoryRequest categoryRequest);

	ApiResponse deleteCategory(String id);

	ApiResponse getAllCategoriesAdmin();
}

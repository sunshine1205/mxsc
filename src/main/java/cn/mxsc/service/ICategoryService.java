package cn.mxsc.service;

import cn.mxsc.common.ServerResponse;
import cn.mxsc.pojo.Category;

import java.util.List;

public interface ICategoryService {


    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}

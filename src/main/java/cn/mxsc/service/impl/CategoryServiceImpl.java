package cn.mxsc.service.impl;

import cn.mxsc.common.ServerResponse;
import cn.mxsc.dao.CategoryMapper;
import cn.mxsc.pojo.Category;
import cn.mxsc.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)){
           return ServerResponse.createByErrorMessage("添加品类失败，参数异常");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0){
          return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {

        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新错误，参数异常");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新分类名成功");
        }
        return ServerResponse.createByErrorMessage("更新分类名失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        if(categoryId == null ){
            return ServerResponse.createByErrorMessage("更新错误，参数异常");
        }
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categories)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    /***
     * 递归查询节点id以及子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        if(categoryId == null ){
            return ServerResponse.createByErrorMessage("查询错误，参数异常");
        }
        Set<Category> categorySet  = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryList = Lists.newArrayList();
        for (Category category:categorySet) {
            categoryList.add(category.getId());
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //递归算法，算出子节点
    private Set<Category> findChildCategory(Set<Category> categorys,Integer categoryId){
          Category category = categoryMapper.selectByPrimaryKey(categoryId);
          if(category!=null){
              categorys.add(category);
          }
          //遍历子节点
          List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
          for (Category categoryItem : categoryList) {
            findChildCategory(categorys,categoryItem.getId());
        }
        return  categorys;
    }

}

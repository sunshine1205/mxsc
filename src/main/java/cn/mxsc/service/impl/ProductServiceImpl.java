package cn.mxsc.service.impl;

import cn.mxsc.common.Const;
import cn.mxsc.common.ResponseCode;
import cn.mxsc.common.ServerResponse;
import cn.mxsc.dao.CategoryMapper;
import cn.mxsc.dao.ProductMapper;
import cn.mxsc.pojo.Category;
import cn.mxsc.pojo.Product;
import cn.mxsc.service.IProductService;
import cn.mxsc.util.DateTimeUtil;
import cn.mxsc.util.PropertiesUtil;
import cn.mxsc.vo.ProductDetailVo;
import cn.mxsc.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {

        if(product!=null){

            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] split = product.getSubImages().split(",");
                if(split.length>0){
                    product.setMainImage(split[0]);
                }
            }
            if(product.getId()!=null){
                int resultCount = productMapper.updateByPrimaryKey(product);
                if(resultCount>0){
                    return ServerResponse.createBySuccessMessage("更新商品信息成功");
                }
                return ServerResponse.createByErrorMessage("更新商品信息失败");
            }else {
                int resultCount = productMapper.insert(product);
                if(resultCount>0){
                    return ServerResponse.createBySuccessMessage("添加商品信息成功");
                }
                return ServerResponse.createByErrorMessage("添加商品信息失败");
            }
        }
        return ServerResponse.createByErrorMessage("保存和更新商品失败，参数异常");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId==null || status == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if(resultCount > 0){
            ServerResponse.createBySuccessMessage("更新销售状态成功");
        }
        return ServerResponse.createByErrorMessage("更新销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if(productId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return  ServerResponse.createByErrorMessage("该商品已下架或已删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo =  assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProductByNameAndId(String productName, Integer productId, int pageNum, int pageSize) {
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList  = productMapper.searchProductByNameAndId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo =  assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
           ProductListVo productListVo = new ProductListVo();
           productListVo.setId(product.getId());
           productListVo.setCategoryId(product.getCategoryId());
           productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mxsc.com/"));
           productListVo.setMainImage(product.getMainImage());
           productListVo.setName(product.getName());
           productListVo.setSubtitle(product.getSubtitle());
           productListVo.setStatus(product.getStatus());
           productListVo.setPrice(product.getPrice());
           return productListVo;
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategotyId(product.getCategoryId());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStock(product.getStock());//库存
        productDetailVo.setStatus(product.getStatus());
        // imageHost;
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mxsc.com/"));
        // parentCategoryId;
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        // createTime;
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
       // updateTime;
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return  productDetailVo;
    }
}

package cn.mxsc.service;

import cn.mxsc.common.ServerResponse;
import cn.mxsc.pojo.Product;
import cn.mxsc.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo>  searchProductByNameAndId(String productName, Integer productId, int pageNum, int pageSize);
}

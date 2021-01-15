package com.ubtedu.deviceconnect.libs.base.product;

import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoProductManager {

    private static URoProductManager instance;

    private final HashSet<URoCoreProduct> products;

    private URoProductManagerDelegate delegate = null;

    public static URoProductManager getInstance() {
        synchronized (URoProductManager.class) {
            if(instance == null) {
                instance = new URoProductManager();
            }
            return instance;
        }
    }

    private URoProductManager() {
        products = new HashSet<>();
    }

    public void addProduct(URoCoreProduct product) {
        if(product == null) {
            return;
        }
        synchronized (products) {
            if(products.contains(product)) {
                return;
            }
            URoLogUtils.e("AddProduct: %s", product.getProductID());
            products.add(product);
            if(delegate != null) {
                delegate.onProductAdd(product.asProduct());
            }
            product.init();
        }
    }

    public void removeProduct(URoCoreProduct product) {
        if(product == null) {
            return;
        }
        synchronized (products) {
            if(!products.contains(product)) {
                return;
            }
            URoLogUtils.e("RemoveProduct: %s", product.getProductID());
            products.remove(product);
            if(delegate != null) {
                delegate.onProductRemove(product.asProduct());
            }
        }
    }

    public URoCoreProduct getProduct(String id) {
        if(TextUtils.isEmpty(id)) {
            return null;
        }
        synchronized (products) {
            for(URoCoreProduct product : products) {
                if(TextUtils.equals(product.getProductID(), id)) {
                    return product;
                }
            }
        }
        return null;
    }

    public URoProduct getProductById(String id) {
        if(TextUtils.isEmpty(id)) {
            return null;
        }
        synchronized (products) {
            for(URoCoreProduct product : products) {
                if(TextUtils.equals(product.getProductID(), id)) {
                    return product.asProduct();
                }
            }
        }
        return null;
    }

    public ArrayList<URoProduct> getAllProduct() {
        ArrayList<URoProduct> result = new ArrayList<>();
        synchronized (products) {
            for(URoCoreProduct product : products) {
                result.add(product.asProduct());
            }
        }
        return result;
    }

    public void setProductManagerDelegate(URoProductManagerDelegate delegate) {
        this.delegate = delegate;
    }

}

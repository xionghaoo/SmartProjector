/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.ubtedu.alpha1x.core.mvp.ViewModelFactory;

/**
 * 用于创建ViewModel
 * @Author qinicy
 * @Date 2018/9/4
 **/
public class DemoViewModelFactory extends ViewModelFactory {



    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DemoViewModel.class)) {
//            return (T) new DemoViewModel(UKitApplication.getInstance(), DataRepository.getInstance());
            return (T) new DemoViewModel(DataRepository.getInstance());
        }
        return super.create(modelClass);
    }

}

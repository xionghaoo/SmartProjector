/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import com.ubtedu.alpha1x.core.base.fragment.UbtBaseFragment;
import com.ubtedu.alpha1x.core.mvp.IPresenter;
import com.ubtedu.alpha1x.core.mvp.IView;
import com.ubtedu.alpha1x.core.mvp.UIDelegate;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitBaseFragment<P extends IPresenter, V extends IView> extends UbtBaseFragment<P, V> {

    @Override
    protected UIDelegate createUIDelegate() {
        UKitUIDelegate delegate = new UKitUIDelegate(getContext());
        if (getActivity() instanceof UKitBaseActivity) {
            UKitBaseActivity activity = (UKitBaseActivity) getActivity();
            delegate.setToastView(activity.getUIDelegate().getToastView());
        }
        return delegate;
    }


    @Override
    protected UKitUIDelegate getUIDelegate() {
        return (UKitUIDelegate) super.getUIDelegate();
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
    }
}

/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.region;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.ServerEnv;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.http.RxClientFactory;
import com.ubtedu.ukit.common.locale.LanguageUtil;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class MenuRegionFragment extends UKitBaseFragment {

    private static final int REGION_RECYCLER_VIEW_COLUMNS_COUNT = 4;
    private RecyclerView mRegionRcv;
    private RegionAdapter mRegionAdapter;
    private List<RegionInfo> mRegionList;
    private RegionInfo mSelectedRegion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_menu_region, null);
        mRegionRcv = root.findViewById(R.id.menu_region_rcv);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), REGION_RECYCLER_VIEW_COLUMNS_COUNT);

        mRegionList = initRegionList();
        mRegionRcv.setLayoutManager(layoutManager);
        mRegionAdapter = new RegionAdapter(getContext(), mRegionList);
        mRegionAdapter.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //设置默认选中
        int selected = getCurrentRegionPosition();
        mSelectedRegion = mRegionList.get(selected);
        mRegionAdapter.getCheckItemPositions().put(selected, true);
        mRegionRcv.setAdapter(mRegionAdapter);
        return root;
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser) {
            mRegionRcv.performClick();
        }
    }

    private int getCurrentRegionPosition() {
        RegionInfo region = Settings.getRegion();
        if (region != null) {
            for (int i = 0; i < mRegionList.size(); i++) {
                if (mRegionList.get(i).name.equals(region.name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private List<RegionInfo> initRegionList() {
        List<RegionInfo> list = new ArrayList<>();
        RegionFactory factory = new RegionFactory(getContext());
        list.add(factory.createCN());
        list.add(factory.createNA());
        list.add(factory.createPL());
        // FIXME: 当前版本（v1.4.0）正式环境不上线韩语 qinicy at 2019/7/9
        if (ServerConfig.getServerEnvConfig() != ServerEnv.RELEASE) {
            list.add(factory.createKR());
        }
        list.add(factory.createGL());
        // FIXME: 当前版本（v1.6.0）正式环境不上线越南语 duanxinning at 2019/12/4
        if (ServerConfig.getServerEnvConfig() != ServerEnv.RELEASE) {
            list.add(factory.createVN());
        }
        // FIXME: 当前版本（v1.6.0）正式环境不上线孟加拉语 duanxinning at 2020/04/21
        if (ServerConfig.getServerEnvConfig() != ServerEnv.RELEASE) {
            list.add(factory.createBD());
        }
        return list;
    }

    /**
     * 国内和国外的地区切换需要重新登录
     */
    private boolean isNeedRelogin(RegionInfo old, RegionInfo newOne) {
        if (!UserManager.getInstance().isGuest() && old != null && newOne != null) {
            return (RegionInfo.REGION_CN.equals(old.name) && !RegionInfo.REGION_CN.equals(newOne.name))||
                    (!RegionInfo.REGION_CN.equals(old.name) && RegionInfo.REGION_CN.equals(newOne.name));
        }
        return false;
    }

    private void onRegionChanged() {
        //重置api接口服务器
        RxClientFactory.reset();

        final RegionInfo old = Settings.getRegion();
        Settings.setRegion(mSelectedRegion);
        LanguageUtil.getInstance().updateConfiguration();
        final Activity activity = getActivity();
        if (activity != null) {
            if (isNeedRelogin(old,mSelectedRegion)){
                UserDataSynchronizer.getInstance().cancel();
                UserManager.getInstance().clearLocalAccount();
                UserManager.getInstance().loginGuest();
                UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(), null);
                LoginActivity.open(activity,false);
            }
            //延时，可以有效减少切换语言黑屏的情况
            getUIDelegate().showLoading(false);
            mRegionRcv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUIDelegate().hideLoading();
                    activity.recreate();

                }
            }, 800);

        }

    }

    private View.OnClickListener mRegionItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d("on selected position:" + v.getTag());
            final RegionCheckView checkView = (RegionCheckView) v;

            RegionInfo region = checkView.getItemInfo();
            if (mSelectedRegion.name.equals(region.name)) {
                return;
            }
            final RegionInfo old = Settings.getRegion();
            String message = isNeedRelogin(old,region)?getString(R.string.menu_region_change_logout_desc):getString(R.string.menu_region_change_desc);
            PromptDialogFragment.newBuilder(getContext())
                    .title(getString(R.string.menu_region_change_title))
                    .message(message)
                    .negativeButtonText(getString(R.string.menu_region_change_negative))
                    .positiveButtonText(getString(R.string.menu_region_change_position))
                    .cancelable(false)
                    .onPositiveClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedRegion = checkView.getItemInfo();
                            onRegionChanged();
                            Map<String, String> args = new HashMap<>(1);
                            args.put("region", mSelectedRegion.name);
                            UBTReporter.onEvent(Events.Ids.app_menu_region_change, args);
                        }
                    })
                    .build()
                    .show(getFragmentManager(), "RegionChangePromptDialogFragment");
        }
    };

    private class RegionAdapter extends CommonAdapter<RegionInfo> {

        public RegionAdapter(Context context, List<RegionInfo> datas) {
            super(context, R.layout.item_menu_region, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final RegionInfo info, final int position) {

            RegionCheckView regionCheckView = holder.getView(R.id.region_check_view);
            regionCheckView.setItemInfo(info);
            regionCheckView.setTag(position);
            regionCheckView.setOnClickListener(mRegionItemOnClickListener);
        }
    }
}

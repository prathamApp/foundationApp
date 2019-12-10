package com.pratham.foundation.ui.home_screen.profile_new;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.ui.home_screen.profile_new.certificate_display.CertificateDisplayActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;


@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends Fragment implements ProfileContract.ProfileView, ProfileContract.ProfileItemClicked{

    @Bean(ProfilePresenter.class)
    ProfileContract.ProfilePresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;

    String[] progressArray = {"Progress", "Share"};
    private ProfileOuterDataAdapter adapterParent;

    @AfterViews
    public void initialize() {
        presenter.setView(ProfileFragment.this);
        if (adapterParent == null) {
            adapterParent = new ProfileOuterDataAdapter(getActivity(), progressArray, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(getActivity()), true));
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(adapterParent);
        }
    }

    @Override
    public void itemClicked(String usage) {
        showUsage();
    }

    private void showUsage() {
        startActivity(new Intent(getActivity(), CertificateDisplayActivity_.class));
    }

//    @Click({R.id.rl_share_app, R.id.btn_share_app})
//    public void share_app() {
//        KotlinPermissions.with(getActivity())
//                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .onAccepted(permissionResult -> {
//                    try {
//                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                        PackageManager pm = ApplicationClass.getInstance().getPackageManager();
//                        ApplicationInfo ai = pm.getApplicationInfo(ApplicationClass.getInstance().getPackageName(), 0);
//                        File localFile = new File(ai.publicSourceDir);
//                        Uri uri = FileProvider.getUriForFile(getActivity(),
//                                BuildConfig.APPLICATION_ID + ".provider", localFile);
//                        intentShareFile.setType("*/*");
//                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
//                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Please download apk from here...");
//                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pratham.foundation");
//                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(intentShareFile, "Share through"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                })
//                .ask();
//    }
}
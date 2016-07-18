package ru.daria.singers;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dsukmanova on 18.07.16.
 */
public class MusicDialog extends DialogFragment {
    private final String YA_MUSIC_APP_NAME = "ru.yandex.music";
    private final String YA_RADIO_APPNAME = "ru.yandex.radio";
    private final String GOOGLE_PLAY_NAME = "market://details?id=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        getDialog().setTitle(R.string.chooseApp);
        View view = inflater.inflate(R.layout.start_music, null);
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        View.OnClickListener onIconClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appName = "";
                switch (v.getId()) {
                    case R.id.yaMusic:
                        appName = YA_MUSIC_APP_NAME;
                        break;
                    case R.id.yaRadio:
                        appName = YA_RADIO_APPNAME;
                }
                Intent intent;
                if (isAppInstalled(appName)) {
                    intent = getActivity().getPackageManager().getLaunchIntentForPackage(appName);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_NAME + appName));
                }
                dismiss();
                startActivity(intent);
            }
        };
        view.findViewById(R.id.yaRadio).setOnClickListener(onIconClickListener);
        view.findViewById(R.id.yaMusic).setOnClickListener(onIconClickListener);
        return view;
    }

    private boolean isAppInstalled(String appPkgName) {
        boolean isAppInstalled = false;
        try {
            if (getActivity().getPackageManager().getPackageInfo(appPkgName, PackageManager.GET_META_DATA) != null) {
                isAppInstalled = true;
            }
            ;
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }
        return isAppInstalled;
    }
}
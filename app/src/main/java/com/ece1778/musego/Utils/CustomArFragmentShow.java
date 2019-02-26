package com.ece1778.musego.Utils;

import com.ece1778.musego.UI.Tour.ArCreateTourActivity;
import com.ece1778.musego.UI.Tour.ArShowTourActivity;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragmentShow extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {

        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        session.configure(config);

        this.getArSceneView().setupSession(session);

        ((ArShowTourActivity) getActivity()).setupDatabase(config, session);

        return config;

    }
}

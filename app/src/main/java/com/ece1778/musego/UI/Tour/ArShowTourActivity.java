package com.ece1778.musego.UI.Tour;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.CustomArFragment;
import com.ece1778.musego.Utils.CustomArFragmentShow;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ArShowTourActivity extends BaseActivity implements Scene.OnUpdateListener, View.OnClickListener {

    private static final String TAG = ArShowTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int START_MARKER = 1;
    private static final int ARROW = 2;
    private static final int END_MARKER = 3;
    private static final int WHEEL = 4;
    private static final int CROWD = 5;
    private static final int FOOD = 6;
    private static final int LIGHT = 7;
    private static final int NOISE = 8;
    private static final int TEMP = 9;
    private static final int WASH = 10;

    private CustomArFragmentShow arFragment;
    private ModelRenderable startRenderable, endRenderable, arrowRenderable;
    private ModelRenderable wheelRenderable, crowdRenderable, foodRenderable, lightRenderable, noiseRenderable, tempRenderable, washRenderable;


    private CollectionReference pathRef;
    private int counter = 0;
    private Node current_starter;
    private Node previous_starter;
    private List<Node> nodes;
    private Node previous_end;
    private NodeList nodeList;

    private Button cancelArBtn;
    private ZXingView scanBox;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_show_tour);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        initView();
        initData();
        setRenderable();

    }


    private void initView() {

        arFragment = (CustomArFragmentShow) getSupportFragmentManager().findFragmentById(R.id.ux_fragment_download);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);

        cancelArBtn = (Button) findViewById(R.id.cancelArBtn);
        cancelArBtn.setOnClickListener(this);
        cancelArBtn.setVisibility(View.GONE);

        scanBox = (ZXingView) findViewById(R.id.scanbox);


    }

    private void initData() {
        pathRef = new FirebaseManager(ArShowTourActivity.this).getRef();

        String nodeListJson = getIntent().getStringExtra("nodeList");
        nodeList = new Gson().fromJson(nodeListJson, NodeList.class);

        previous_starter = nodeList.getStart_node();
        previous_end = nodeList.getEnd_node();
        nodes = nodeList.getNodes();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancelArBtn) {
            ArShowTourActivity.this.finish();
        }

    }

    private void setRenderable() {

        ModelRenderable.builder()
                .setSource(this, R.raw.marker)
                .build()
                .thenAccept(renderable -> startRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load start marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.model)
                .build()
                .thenAccept(renderable -> arrowRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load arrow renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.marker)
                .build()
                .thenAccept(renderable -> endRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.tinker)
                .build()
                .thenAccept(renderable -> wheelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load wheel marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.crowd)
                .build()
                .thenAccept(renderable -> crowdRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load crow renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.food)
                .build()
                .thenAccept(renderable -> foodRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.light)
                .build()
                .thenAccept(renderable -> lightRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.noise)
                .build()
                .thenAccept(renderable -> noiseRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.temp)
                .build()
                .thenAccept(renderable -> tempRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.wash)
                .build()
                .thenAccept(renderable -> washRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

    private void downloadPath(Pose starterPose) {

        //removePreviousAnchors();

        if (startRenderable == null || endRenderable == null || arrowRenderable == null || wheelRenderable == null) {
            Log.d(TAG, "!!!!!!!Renderable unprovided!");
            return;
        }

        Translation t = new Translation(
                starterPose.tx(),
                starterPose.ty(),
                starterPose.tz());

        Rotation r = new Rotation(
                starterPose.qx(),
                starterPose.qy(),
                starterPose.qz(),
                starterPose.qw());


        for (Node node : nodes) {
            renderObj(node, t, r);
        }
        renderObj(previous_end, t, r);


        // Toast.makeText(ArShowTourActivity.this, "Get Path", Toast.LENGTH_SHORT).show();


    }

    private void renderObj(Node node, Translation t, Rotation r) {

        float[] f1 = calToffset(node, t);
        float[] f2 = calRoffset(node, r);

        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(new com.google.ar.core.Pose(f1, f2));
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        //andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 90f));
        andy.setParent(anchorNode);

        if (node.getTag() == START_MARKER) {

            andy.setRenderable(startRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));

        } else if (node.getTag() == ARROW) {

            andy.setRenderable(arrowRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225f));
            andy.setLocalPosition(new Vector3(0f, 0.2f, 0f));

        } else if (node.getTag() == WHEEL) {

            andy.setRenderable(wheelRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        } else if (node.getTag() == CROWD) {

            andy.setRenderable(crowdRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        }else if (node.getTag() == FOOD) {

            andy.setRenderable(foodRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        }else if (node.getTag() == LIGHT) {

            andy.setRenderable(lightRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        }else if (node.getTag() == NOISE) {

            andy.setRenderable(noiseRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        }else if (node.getTag() == TEMP) {

            andy.setRenderable(tempRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        }else if (node.getTag() == WASH) {

            andy.setRenderable(washRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));

        } else {

            andy.setRenderable(endRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
        }

        andy.select();

    }

    private float[] calToffset(Node node, Translation t) {

        float[] f1 = new float[]{
                t.getTx() - previous_starter.getT().getTx() + node.getT().getTx(),
                t.getTy() - previous_starter.getT().getTy() + node.getT().getTy(),
                t.getTz() - previous_starter.getT().getTz() + node.getT().getTz()};

        return f1;

    }

    private float[] calRoffset(Node node, Rotation r) {
//
        float[] f2 = new float[]{
                r.getQx() - previous_starter.getR().getQx() + node.getR().getQx(),
                r.getQy() - previous_starter.getR().getQy() + node.getR().getQy(),
                r.getQz() - previous_starter.getR().getQz() + node.getR().getQz(),
                r.getQw() - previous_starter.getR().getQw() + node.getR().getQw()};

//        float[] f2 = new float[]{
//                node.getR().getQx(),
//                node.getR().getQy(),
//                node.getR().getQz(),
//                node.getR().getQw()};
        return f2;

    }


    private void removePreviousAnchors() {
        List<com.google.ar.sceneform.Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (com.google.ar.sceneform.Node childNode : nodeList) {
            if (childNode instanceof AnchorNode) {
                if (((AnchorNode) childNode).getAnchor() != null) {
                    ((AnchorNode) childNode).getAnchor().detach();
                    ((AnchorNode) childNode).setParent(null);
                }
            }
        }
    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }

        return true;
    }

    public void setupDatabase(Config config, Session session) {

        Bitmap ramenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scanme);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("ramen", ramenBitmap);
        config.setAugmentedImageDatabase(aid);

    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image : images) {
            if (image.getTrackingState() == TrackingState.TRACKING) {
                if (image.getName().equals("ramen")) {
                    Anchor anchor = image.createAnchor(image.getCenterPose());
                    scanBox.setVisibility(View.GONE);

                    if (!existAnchor()) {

                        placeModel(startRenderable, anchor);

                        TastyToast.makeText(getApplicationContext(), "Success!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                        cancelArBtn.setVisibility(View.VISIBLE);
                        downloadPath(image.getCenterPose());
                    }

                }
            }

        }

    }

    private void createModel(Anchor anchor) {

        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable, anchor));

    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setName("starter");
        //anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private boolean existAnchor() {

        List<com.google.ar.sceneform.Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (com.google.ar.sceneform.Node childNode : nodeList) {
            if (childNode instanceof AnchorNode) {
                if (childNode.getName().equals("starter")) {
                    return true;
                }
            }
        }
        return false;


    }

}

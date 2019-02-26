package com.ece1778.musego.UI.Tour;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArShowTourActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ArShowTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int START_MARKER = 1;
    private static final int ARROW = 2;
    private static final int STAR = 3;
    private static final int END_MARKER = 4;

    private ArFragment arFragment;
    private ModelRenderable startRenderable, endRenderable, arrowRenderable, starRenderable;
    private int selected = START_MARKER;


    private CollectionReference pathRef;
    private int counter = 0;
    private Node current_starter;
    private Node previous_starter;
    private List<Node> nodes;
    private Node previous_end;
    private NodeList nodeList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_show_tour);

        initView();
        initData();
        setRenderable();
        downloadPath();
    }



    private void initView() {

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment_download);

        findViewById(R.id.cancelArBtn).setOnClickListener(this);


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
        if(i == R.id.cancelArBtn) {
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
                .setSource(this, R.raw.star)
                .build()
                .thenAccept(renderable -> starRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load star renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.marker_yellow)
                .build()
                .thenAccept(renderable -> endRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

    private void downloadPath(){

        removePreviousAnchors();

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (startRenderable == null || endRenderable == null ||starRenderable == null || arrowRenderable == null) {
                        Log.d(TAG, "Renderable unprovided!");
                        return;
                    }

                    counter++;

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    Translation t = new Translation(anchor.getPose().tx(), anchor.getPose().ty(), anchor.getPose().tz());
                    Rotation r = new Rotation(anchor.getPose().qx(), anchor.getPose().qy(), anchor.getPose().qz(), anchor.getPose().qw());


                    if(counter == 1){
                        Toast.makeText(ArShowTourActivity.this, "Get Path", Toast.LENGTH_SHORT).show();

                        renderObj(previous_starter,t,r);
                        for(Node node: nodes){
                            renderObj(node,t,r);
                        }
                        renderObj(previous_end,t,r);

                    }

                }
        );

    }

    private void renderObj(Node node,Translation t,Rotation r){

        float[] f1 = calToffset(node,t);
        float[] f2 = calRoffset(node,r);

        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(new com.google.ar.core.Pose(f1,f2));
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        //andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 90f));
        andy.setParent(anchorNode);

        if(node.getTag() == START_MARKER) {
            andy.setRenderable(startRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
        }
        else if(node.getTag() == ARROW){
            andy.setRenderable(arrowRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225f));
            andy.setLocalPosition(new Vector3(0f, 0.2f, 0f));
        }
        else if(node.getTag() == STAR){
            andy.setRenderable(starRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180f));
        }
        else{
            andy.setRenderable(endRenderable);
            andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
        }

        andy.select();

    }

    private float[] calToffset(Node node, Translation t){

        float[] f1 = new float[]{
                t.getTx() - previous_starter.getT().getTx() + node.getT().getTx(),
                t.getTy() - previous_starter.getT().getTy() + node.getT().getTy(),
                t.getTz() - previous_starter.getT().getTz() + node.getT().getTz()};

        return f1;

    }

    private float[] calRoffset(Node node, Rotation r){
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
}

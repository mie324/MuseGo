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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

public class ArCreateTourActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ArCreateTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int MARKER = 1;
    private static final int ARROW = 2;
    private static final int STAR = 3;


    private ArFragment arFragment;
    private ModelRenderable markerRenderable, arrowRenderable, starRenderable;
    private int selected = MARKER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_create_tour);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        initView();
        setRenderable();
        createPath();

    }

    private void initView() {

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment_upload);

        findViewById(R.id.finishArBtn).setOnClickListener(this);
        findViewById(R.id.cancelArBtn).setOnClickListener(this);
        findViewById(R.id.renderable_start).setOnClickListener(this);
        findViewById(R.id.renderable_arrow).setOnClickListener(this);
        findViewById(R.id.renderable_flag).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ux_fragment_upload) {
            startActivity(new Intent(ArCreateTourActivity.this, UploadTourActivity.class));
        } else if (i == R.id.cancelArBtn) {
            startActivity(new Intent(ArCreateTourActivity.this, TourListActivity.class));
        } else if (i == R.id.renderable_start) {
            selected = MARKER;
        } else if (i == R.id.renderable_arrow) {
            selected = ARROW;
        } else if (i == R.id.renderable_flag) {
            selected = STAR;
        }

    }

    private void setRenderable() {

        ModelRenderable.builder()
                .setSource(this, R.raw.marker)
                .build()
                .thenAccept(renderable -> markerRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load marker renderable", Toast.LENGTH_LONG);
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

    }

    private void addInfoCard(Node flag) {

        Node infoCard = new Node();
        infoCard.setParent(flag);
        infoCard.setLocalPosition(new Vector3(0f, 0.25f, 0f));

        ViewRenderable.builder()
                .setView(this, R.layout.description_card)
                .build()
                .thenAccept(
                        (renderable) -> {
                            infoCard.setRenderable(renderable);
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    private void createPath() {
        removePreviousAnchors();

        arFragment.setOnTapArPlaneListener(

                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (markerRenderable == null || starRenderable == null || arrowRenderable == null) {
                        Log.d(TAG, "Renderable unprovided!");
                        return;
                    }

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode object = new TransformableNode(arFragment.getTransformationSystem());
                    object.setParent(anchorNode);

                    if (selected == MARKER) {
                        object.setRenderable(markerRenderable);
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));


                    } else if (selected == ARROW) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(arrowRenderable);

                    } else if (selected == STAR) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180f));
                        object.setRenderable(starRenderable);

                        addInfoCard(object);
                    }

                    object.select();
                });

        removeRenderableByClick();
    }


    private void removePreviousAnchors() {
        List<Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (Node childNode : nodeList) {
            if (childNode instanceof AnchorNode) {
                if (((AnchorNode) childNode).getAnchor() != null) {
                    ((AnchorNode) childNode).getAnchor().detach();
                    ((AnchorNode) childNode).setParent(null);
                }
            }
        }
    }

    private void removeRenderableByClick() {

        Scene scene = arFragment.getArSceneView().getScene();
        scene.addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                Log.d(TAG, "handleOnTouch");

                // First call ArFragment's listener to handle TransformableNodes.
                arFragment.onPeekTouch(hitTestResult, motionEvent);

                //We are only interested in the ACTION_UP events - anything else just return
                if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
                    return;
                }

                // Check for touching a Sceneform node
                if (hitTestResult.getNode() != null) {
                    Log.d(TAG, "handleOnTouch hitTestResult.getNode() != null");
                    Node hitNode = hitTestResult.getNode();

                    if (hitNode.getRenderable() == markerRenderable || hitNode.getRenderable() == arrowRenderable || hitNode.getRenderable() == starRenderable) {
                        arFragment.getArSceneView().getScene().removeChild(hitNode);
                        hitNode.setParent(null);
                        hitNode = null;
                        Toast.makeText(ArCreateTourActivity.this, "Node Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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



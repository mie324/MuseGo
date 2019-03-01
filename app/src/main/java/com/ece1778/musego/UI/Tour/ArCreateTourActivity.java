package com.ece1778.musego.UI.Tour;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.CustomArFragment;
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
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.ZXingView;


public class ArCreateTourActivity extends BaseActivity implements Scene.OnUpdateListener, View.OnClickListener {

    private static final String TAG = ArCreateTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int START_MARKER = 1;
    private static final int ARROW = 2;
    private static final int STAR = 3;
    private static final int END_MARKER = 4;

    private CustomArFragment arFragment;
    private ModelRenderable startRenderable, endRenderable, arrowRenderable, starRenderable;
    private int selected = ARROW;

    private com.ece1778.musego.Model.Node starter;
    private com.ece1778.musego.Model.Node end;
    private List<com.ece1778.musego.Model.Node> nodes = new ArrayList<>();

    private Button finishArBtn;
    private Button cancelArBtn;
    private Button renderable_start;
    private Button renderable_arrow;
    private Button renderable_flag;
    private Button renderable_end;

    private ZXingView scanBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ar_create_tour);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

       initView();



    }


    @Override
    protected void onStart() {
        super.onStart();
        setRenderable();


    }

    @Override
    protected void onResume() {
        super.onResume();

        createPath();

    }

    private void initView() {

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment_upload);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);



        scanBox = (ZXingView) findViewById(R.id.scanbox);

        finishArBtn = (Button) findViewById(R.id.finishArBtn);
        finishArBtn.setOnClickListener(this);
        finishArBtn.setVisibility(View.GONE);
        cancelArBtn = (Button) findViewById(R.id.cancelArBtn);
        cancelArBtn.setOnClickListener(this);
        cancelArBtn.setVisibility(View.GONE);
        renderable_start = (Button)findViewById(R.id.renderable_start);
        renderable_start.setOnClickListener(this);
        renderable_start.setVisibility(View.GONE);
        renderable_arrow = (Button) findViewById(R.id.renderable_arrow);
        renderable_arrow.setOnClickListener(this);
        renderable_arrow.setVisibility(View.GONE);
        renderable_flag = (Button) findViewById(R.id.renderable_flag);
        renderable_flag.setOnClickListener(this);
        renderable_flag.setVisibility(View.GONE);
        renderable_end = (Button) findViewById(R.id.renderable_end);
        renderable_end.setOnClickListener(this);
        renderable_end.setVisibility(View.GONE);



    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.finishArBtn) {
            if (checkNodes()) {
                NodeList nodeList = new NodeList(starter, end, nodes);
                Intent intent = new Intent(ArCreateTourActivity.this, UploadTourActivity.class);
                intent.putExtra("nodeList", new Gson().toJson(nodeList));
                startActivity(intent);
                finish();
            }

        } else if (i == R.id.cancelArBtn) {

            startActivity(new Intent(ArCreateTourActivity.this, TourListActivity.class));
            finish();

        } else if (i == R.id.renderable_start) {

            selected = START_MARKER;
        } else if (i == R.id.renderable_arrow) {
            selected = ARROW;
        } else if (i == R.id.renderable_flag) {
            selected = STAR;
        } else if (i == R.id.renderable_end) {
            selected = END_MARKER;
        }

    }

    private Boolean checkNodes() {
        if (starter == null) {
            Toast.makeText(ArCreateTourActivity.this, "Please enter start node", Toast.LENGTH_SHORT).show();
            return false;

        } else if (end == null) {
            Toast.makeText(ArCreateTourActivity.this, "Please enter end node", Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;
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
                            EditText mContent = (EditText) renderable.getView().findViewById(R.id.card_content);
                            Button mUploadBtn = (Button) renderable.getView().findViewById(R.id.card_upload);
                            mUploadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Content is" + mContent.getText().toString());
                                    mUploadBtn.setVisibility(View.INVISIBLE);

                                }
                            });
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
                    if (startRenderable == null || endRenderable == null || starRenderable == null || arrowRenderable == null) {
                        Log.d(TAG, "Renderable unprovided!");
                        return;
                    }

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    Translation t = new Translation(anchor.getPose().tx(), anchor.getPose().ty(), anchor.getPose().tz());
                    Rotation r = new Rotation(anchor.getPose().qx(), anchor.getPose().qy(), anchor.getPose().qz(), anchor.getPose().qw());

                    TransformableNode object = new TransformableNode(arFragment.getTransformationSystem());
                    object.setParent(anchorNode);

                    if (selected == START_MARKER) {

                        if (starter == null) {
                            object.setRenderable(startRenderable);
                            object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
//                            starter = new com.ece1778.musego.Model.Node(t, r, START_MARKER);
                        } else {
                            Toast.makeText(ArCreateTourActivity.this, "Start Node existed!", Toast.LENGTH_SHORT).show();
                        }

                    } else if (selected == ARROW) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(arrowRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, ARROW));


                    } else if (selected == STAR) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180f));
                        object.setRenderable(starRenderable);
//                        addInfoCard(object);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, STAR, "Comments"));

                    } else if (selected == END_MARKER) {
                        if (end == null) {
                            object.setRenderable(endRenderable);
                            object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
                            end = new com.ece1778.musego.Model.Node(t, r, END_MARKER);
                        } else {
                            Toast.makeText(ArCreateTourActivity.this, "End Node existed!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    object.select();

                });

        removeRenderableByClick();
    }


    private void removePreviousAnchors() {

        starter = null;
        end = null;
        nodes.clear();

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

                    if (hitNode.getRenderable() == startRenderable) {
                        arFragment.getArSceneView().getScene().removeChild(hitNode);
                        hitNode.setParent(null);
                        hitNode = null;
                        starter = null;
                        Toast.makeText(ArCreateTourActivity.this, "Node Deleted", Toast.LENGTH_SHORT).show();

                    } else if (hitNode.getRenderable() == endRenderable) {
                        arFragment.getArSceneView().getScene().removeChild(hitNode);
                        hitNode.setParent(null);
                        hitNode = null;
                        end = null;
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

    public void setupDatabase(Config config, Session session){

        Bitmap ramenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ramen);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("ramen", ramenBitmap);
        config.setAugmentedImageDatabase(aid);

    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        for(AugmentedImage  image: images){
            if(image.getTrackingState() == TrackingState.TRACKING){
                if(image.getName().equals("ramen")){
                    Anchor anchor = image.createAnchor(image.getCenterPose());

                    if(!existAnchor()) {

                        Translation t = new Translation(
                                image.getCenterPose().tx(),
                                image.getCenterPose().ty(),
                                image.getCenterPose().tz());

                        Rotation r = new Rotation(
                                image.getCenterPose().qx(),
                                image.getCenterPose().qy(),
                                image.getCenterPose().qz(),
                                image.getCenterPose().qw());

                        starter = new com.ece1778.musego.Model.Node(t, r, START_MARKER);
                        placeModel(startRenderable, anchor);
                        showBtnAndHideBox();
                        TastyToast.makeText(getApplicationContext(), "Create StartNode Success!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                    }
                }
            }

        }

    }

    private void showBtnAndHideBox(){

        scanBox.setVisibility(View.GONE);


        finishArBtn.setVisibility(View.VISIBLE);
        cancelArBtn.setVisibility(View.VISIBLE);
        renderable_start.setVisibility(View.VISIBLE);
        renderable_arrow.setVisibility(View.VISIBLE);
        renderable_flag.setVisibility(View.VISIBLE);
        renderable_end.setVisibility(View.VISIBLE);



    }

    private void createModel(Anchor anchor) {




    }



    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setName("starter");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, TourListActivity.class));
    }
}



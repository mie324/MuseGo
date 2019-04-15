package com.ece1778.musego.UI.Tour;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.CustomArFragment;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bingoogolapple.qrcode.zxing.ZXingView;


public class ArCreateTourActivity extends BaseActivity implements Scene.OnUpdateListener, View.OnClickListener {

    private static final String TAG = ArCreateTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int END_MARKER = 0;
    private static final int START_MARKER = 1;
    private static final int ARROW = 2;
    private static final int WASH  = 3;
    private static final int CROWD = 4;
    private static final int FOOD = 5;
    private static final int HAND = 6;
    private static final int LIGHT = 7;
    private static final int DARK = 71;
    private static final int BRIGHT = 72;
    private static final int NOISE = 8;
    private static final int LOUD = 81;
    private static final int QUIET = 82;
    private static final int TEMP = 9;
    private static final int HOT = 91;
    private static final int COLD = 92;
    private static final int HUMID = 93;
    private static final int HELPER = 10;



    private CustomArFragment arFragment;
    private ModelRenderable startRenderable, endRenderable, arrowRenderable;
    private ModelRenderable washRenderable, crowdRenderable, foodRenderable, handRenderable;
    private ModelRenderable darkRenderable, brightRenderable;
    private ModelRenderable quietRenderable, loudRenderable;
    private ModelRenderable coldRenderable, hotRenderable, humidRenderable;

    private int selected = ARROW;

    private com.ece1778.musego.Model.Node starter;
    private com.ece1778.musego.Model.Node end;
    private List<com.ece1778.musego.Model.Node> nodes = new ArrayList<>();
    private List<String> sensorList = new ArrayList<>(Arrays.asList("#Crowd Free", "#Interactive Free", "#Dark Area Free", "#Bright Area Free", "#Hot Area Free", "#Cold Area Free", "#Humid Area Free"));

    private Button renderable_arrow;
    private Button renderable_end;
    private Button helper;
    private AllAngleExpandableButton toggleBtn;
    private Button end_tour;

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

        initToggleBtn();

        scanBox = (ZXingView) findViewById(R.id.scanbox);

        renderable_arrow = (Button) findViewById(R.id.renderable_arrow);
        renderable_arrow.setOnClickListener(this);
        renderable_arrow.setVisibility(View.GONE);

        renderable_end = (Button) findViewById(R.id.renderable_end);
        renderable_end.setOnClickListener(this);
        renderable_end.setVisibility(View.GONE);

        helper = (Button) findViewById(R.id.helper);
        helper.setOnClickListener(this);
        helper.setVisibility(View.GONE);

        end_tour = (Button) findViewById(R.id.endTour);
        end_tour.setOnClickListener(this);
        end_tour.setVisibility(View.GONE);

    }

    private void initToggleBtn() {

        toggleBtn = findViewById(R.id.button_expandable);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.crowd, R.drawable.noise, R.drawable.light, R.drawable.temp,R.drawable.wash, R.drawable.food, R.drawable.hand};
        int[] color = {R.color.darkGreen, R.color.darkGreen, R.color.darkGreen, R.color.darkGreen, R.color.darkGreen, R.color.darkGreen, R.color.darkGreen, R.color.darkGreen};
        for (int i = 0; i < drawable.length; i++) {
            ButtonData buttonData;
            if(i == 0){
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            }else{
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        toggleBtn.setButtonDatas(buttonDatas);
        toggleBtn.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int i) {
                switch (i){
                    case 1:
                        selected = CROWD;
                        break;
                    case 2:
                        initPopWindow(NOISE);
                        break;
                    case 3:
                        initPopWindow(LIGHT);
                        break;
                    case 4:
                        initPopWindow(TEMP);
                        break;
                    case 5:
                        selected = WASH;
                        break;
                    case 6:
                        selected = FOOD;
                        break;
                    case 7:
                        selected = HAND;
                        break;

                    default:
                        selected = ARROW;

                }
            }

            @Override
            public void onExpand() {
                renderable_arrow.setVisibility(View.GONE);
                renderable_end.setVisibility(View.GONE);
            }

            @Override
            public void onCollapse() {
                renderable_arrow.setVisibility(View.VISIBLE);
                renderable_end.setVisibility(View.VISIBLE);

            }
        });

        toggleBtn.setVisibility(View.GONE);
    }

    private void initPopWindow(int i) {

        if( i == TEMP) {

            View view = LayoutInflater.from(this).inflate(R.layout.cardview_temp, null, false);
            Button hot = (Button) view.findViewById(R.id.temp_hot);
            Button humid = (Button) view.findViewById(R.id.temp_humid);
            Button cold = (Button) view.findViewById(R.id.temp_cold);
            Button dismiss = (Button) view.findViewById(R.id.temp_dismiss);

            final PopupWindow popWindow = new PopupWindow(view,
                    900,600, true);

            popWindow.setTouchable(true);
            popWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });


            hot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = HOT;
                    popWindow.dismiss();
                }
            });

            humid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = HUMID;
                    popWindow.dismiss();
                }
            });

            cold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = COLD;
                    popWindow.dismiss();
                }
            });

        }else if(i == LIGHT){

            View view = LayoutInflater.from(this).inflate(R.layout.cardview_light, null, false);
            Button dismiss = (Button) view.findViewById(R.id.light_dismiss);
            Button bright = (Button) view.findViewById(R.id.light_bright);
            Button dark = (Button) view.findViewById(R.id.light_dark);

            final PopupWindow popWindow = new PopupWindow(view,
                    900, 600, true);

            popWindow.setTouchable(true);
            popWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });


            bright.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = BRIGHT;
                    popWindow.dismiss();
                }
            });

            dark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = DARK;
                    popWindow.dismiss();
                }
            });

        }else if(i == NOISE){

            View view = LayoutInflater.from(this).inflate(R.layout.cardview_sound, null, false);
            Button dismiss = (Button) view.findViewById(R.id.sound_dismiss);
            Button loud = (Button) view.findViewById(R.id.sound_loud);
            Button quiet = (Button) view.findViewById(R.id.sound_quiet);

            final PopupWindow popWindow = new PopupWindow(view,
                    900, 600, true);

            popWindow.setTouchable(true);
            popWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });


            loud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = LOUD;
                    popWindow.dismiss();
                }
            });

            quiet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = QUIET;
                    popWindow.dismiss();
                }
            });

        }else if(i == HELPER){

            View view = LayoutInflater.from(this).inflate(R.layout.cardview_helper, null, false);
            Button dismiss = (Button) view.findViewById(R.id.info_dismiss);

            final PopupWindow popWindow = new PopupWindow(view,
                    1200, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            popWindow.setTouchable(true);
            popWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.renderable_arrow) {
            selected = ARROW;
        } else if (i == R.id.renderable_end) {
            selected = END_MARKER;
        } else if(i == R.id.endTour){
            endTour();
        }else if(i == R.id.helper){
            initPopWindow(HELPER);
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
                .setSource(this, R.raw.washroom)
                .build()
                .thenAccept(renderable -> washRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.crowd_title)
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
                .setSource(this, R.raw.food2)
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
                .setSource(this, R.raw.interactive)
                .build()
                .thenAccept(renderable -> handRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.bright)
                .build()
                .thenAccept(renderable -> brightRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.dark)
                .build()
                .thenAccept(renderable -> darkRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.hot)
                .build()
                .thenAccept(renderable -> hotRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


        ModelRenderable.builder()
                .setSource(this, R.raw.cold)
                .build()
                .thenAccept(renderable -> coldRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.humid)
                .build()
                .thenAccept(renderable -> humidRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.quiet)
                .build()
                .thenAccept(renderable -> quietRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.loud)
                .build()
                .thenAccept(renderable -> loudRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load end marker renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

//    private void addInfoCard(Node flag) {
//        Node infoCard = new Node();
//        infoCard.setParent(flag);
//        infoCard.setLocalPosition(new Vector3(0f, 0.25f, 0f));
//
//        ViewRenderable.builder()
//                .setView(this, R.layout.description_card)
//                .build()
//                .thenAccept(
//                        (renderable) -> {
//                            infoCard.setRenderable(renderable);
//                            EditText mContent = (EditText) renderable.getView().findViewById(R.id.card_content);
//                            Button mUploadBtn = (Button) renderable.getView().findViewById(R.id.card_upload);
//                            mUploadBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Log.d(TAG, "Content is" + mContent.getText().toString());
//                                    mUploadBtn.setVisibility(View.INVISIBLE);
//
//                                }
//                            });
//                        })
//                .exceptionally(
//                        throwable -> {
//                            Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            return null;
//                        });
//    }

    private void createPath() {

        arFragment.setOnTapArPlaneListener(

                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (endRenderable == null || arrowRenderable == null) {
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

                    if (selected == ARROW) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(arrowRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, ARROW));

                    } else if (selected == END_MARKER) {
                        if (end == null) {
                            object.setRenderable(endRenderable);
                            object.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 270f));
                            end = new com.ece1778.musego.Model.Node(t, r, END_MARKER);
                            renderable_arrow.setVisibility(View.GONE);
                            renderable_end.setVisibility(View.GONE);
                            toggleBtn.setVisibility(View.GONE);
                            end_tour.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(ArCreateTourActivity.this, "End Node existed!", Toast.LENGTH_SHORT).show();

                        }
                    }else if(selected == WASH) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(washRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, WASH));
                        if(!sensorList.contains("#Washroom")){
                            sensorList.add("#Washroom");
                        }

                    }else if(selected == CROWD) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(crowdRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, CROWD));
                        sensorList.remove("#Crowd Free");

                    }else if(selected == FOOD) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(foodRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, FOOD));
                        if(!sensorList.contains("#Food Allowed")){
                            sensorList.add("#Food Allowed");
                        }

                    }else if(selected == HAND) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(handRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, HAND));
                        sensorList.remove("#Interactive Free");

                    }else if(selected == DARK) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(darkRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, DARK));
                        sensorList.remove("#Dark Area Free");


                    }else if(selected == BRIGHT) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(brightRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, BRIGHT));
                        sensorList.remove("#Bright Area Free");

                    }else if(selected == QUIET) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(quietRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, QUIET));
                        if(!sensorList.contains("#Quiet Area")){
                            sensorList.add("#Quiet Area");
                        }


                    }else if(selected == LOUD) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(loudRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, LOUD));
                        if(!sensorList.contains("#Loud Area")){
                            sensorList.add("#Loud Area");
                        }


                    }else if(selected == HOT) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(hotRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, HOT));
                        sensorList.remove("#Hot Area Free");

                    }else if(selected == COLD) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(coldRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, COLD));
                        sensorList.remove("#Cold Area Free");

                    }else if(selected == HUMID) {
                        object.setLocalRotation(Quaternion.axisAngle(new Vector3(1, 0f, 0), 270f));
                        object.setLocalPosition(new Vector3(0f, 0.2f, 0f));
                        object.setRenderable(humidRenderable);
                        nodes.add(new com.ece1778.musego.Model.Node(t, r, HUMID));
                        sensorList.remove("#Humid Area Free");

                    }

                    selected = ARROW;
                    object.select();

                });
    }

    private void endTour() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to finish and share your tour?");
        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                NodeList nodeList = new NodeList(starter, end, nodes);
                Intent intent = new Intent(ArCreateTourActivity.this, UploadTourActivity.class);
                intent.putExtra("nodeList", new Gson().toJson(nodeList));
                intent.putExtra("sensor",new Gson().toJson(sensorList));
                intent.putExtra("instName", getIntent().getStringExtra("instName"));
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(ArCreateTourActivity.this, TourListActivity.class));
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteLastNode() {
        end = null;
        List<Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
        for (Node childNode : nodeList) {
            if (childNode instanceof AnchorNode) {
                if(childNode.getRenderable() == endRenderable) {
                    if (((AnchorNode) childNode).getAnchor() != null) {
                        ((AnchorNode) childNode).getAnchor().detach();
                        ((AnchorNode) childNode).setParent(null);
                    }
                }
            }
        }
    }



    public void setupDatabase(Config config, Session session){

        Bitmap ramenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scanme);
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

        renderable_arrow.setVisibility(View.VISIBLE);
        renderable_end.setVisibility(View.VISIBLE);
        toggleBtn.setVisibility(View.VISIBLE);
        helper.setVisibility(View.VISIBLE);

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



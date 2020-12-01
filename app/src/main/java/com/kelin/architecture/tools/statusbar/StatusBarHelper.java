package com.kelin.architecture.tools.statusbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.kelin.architecture.tools.AppLayerErrorCatcher;
import com.kelin.architecture.util.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.kelin.architecture.tools.statusbar.QMUIStatusBarHelper.MIUISetStatusBarLightMode;


/**
 * http://www.jianshu.com/p/4f71b98dd6f9
 * <p>
 * 1. 4.4以上的沉浸
 * 2. 处理6.0以上的沉浸与字体颜色，5.0只可以处理沉浸而不能改字体颜色(故不建议用白色背景)
 * 3. 调用私有API处理小米魅族的状态栏字体颜色
 * 4. 获取状态栏高度
 * 4. 状态栏背景取决于你的Toolbar
 * <p>
 * 方法一：状态栏的使用
 * 在Activity中设置如下即可，注意xml/style中不用写任何关于状态栏的属性（特别是fitsSystemWindows这个属性），直接拿来用就可以
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * setContentView(R.toast_system_toast_layout.activity_main);
 * <p>
 * StatusbarUtils.from(this)
 * // 沉浸状态栏
 * .setTransparentStatusbar(true)
 * //白底黑字状态栏
 * .setLightStatusBar(true)
 * //设置toolbar, actionbar等view
 * .setActionbarView(mNavigationBar)
 * .process();
 * }
 * <p>
 * <p>
 * <p>
 * 方法二：view的单独使用
 * 下面代码是在4.4以上自动增长了状态栏的高度，需要在xml中加入windowIsTranslucent属性即可。
 * <p>
 * // 某个View的构造函数中的调用
 * public void setFitTranslucent(final boolean translucent) {
 * post(new Runnable() {
 * @Override public void run() {
 * if (StatusbarUtils.isLessKitkat() || !translucent) {
 * return;
 * }
 * int height = StatusbarUtils.getStatusBarOffsetPx(getContext());
 * setPadding(getPaddingLeft(), height + getPaddingTop(), getPaddingRight(), getPaddingBottom());
 * getLayoutParams().height += height;
 * }
 * });
 * }
 * <p>
 * <p>
 * // http://blog.raffaeu.com/archive/2015/04/11/android-and-the-transparent-status-bar.aspx
 * // http://stackoverflow.com/questions/26440879/how-do-i-use-drawerlayout-to-display-over-the-actionbar-toolbar-and-under-the-st/27153313#27153313
 * // http://stackoverflow.com/questions/27856603/lollipop-draw-behind-statusbar-with-its-color-set-to-transparent
 * // 还需要研究
 * /**
 * 改变字体颜色
 * see {@link <a href="https://developer.android.com/reference/android/R.attr.html#windowLightStatusBar"></a>}
 * <p>
 * 沉浸式状态栏实现及遇到的坑
 * http://www.liuling123.com/2017/02/transparent-status-bar.html
 */
/*getWindow().getDecorView().setSystemUiVisibility(
 * View.SYSTEM_UI_FLAG_LAYOUT_STABLE
 * | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
 * <p>
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
 * getWindow().setStatusBarColor(Color.TRANSPARENT);
 * }
 * <p>
 * // Window.FEATURE_ACTION_BAR_OVERLAY
 * // WindowManager.LayoutParams.FLAG_FULLSCREEN
 * //        getWindow().setFlags();
 * // getWindow().requestFeature();
 */
public final class StatusBarHelper {

    private boolean lightStatusBar;//状态栏字体或者图标是否为黑色，true为黑色，false为白色

    private boolean transparentNavigationBar;

    private Window window;
    private View actionBarView;
    private final int currentVersion = Build.VERSION.SDK_INT;
    private boolean keepPreviousWhenNotTransparentStatusBar = false;// 非沉浸状态下是否恢复或者保持原始状态

    /**
     * 预先设定：默认设置无论什么机型，6.0以上都采用完全的沉浸式（全屏，状态栏颜色跟随toolbar，状态栏字体和toolbar反色）
     */
    private static boolean mOnlyImmersiveAbove6 = false;
    private boolean mActionBarInitialized;

    /**
     * 伪造的状态栏；目的是在全屏状态，白色toolbar和白色的状态栏字体，用黑色fakeStatusBar去解决字体不能改成黑色的问题；适用于6.0以下的非MI_UI、FlyMe机型
     */
    private View fakeStatusBar;

    //    private @ColorInt int statusBarColor = Color.BLACK;

    public StatusBarHelper(Window window, boolean lightStatusBar, boolean transparentNavigationBar, View actionBarView) {
        this.lightStatusBar = lightStatusBar;
        this.transparentNavigationBar = transparentNavigationBar;
        this.window = window;
        this.actionBarView = actionBarView;
    }

    public StatusBarHelper(@NonNull Window window, @Nullable View actionBarView) {
        this.window = window;
        this.actionBarView = actionBarView;
    }

    /**
     * mainUsed
     */
    public StatusBarHelper(@NonNull Activity activity) {
        this(activity, null);
    }

    /**
     * mainUsed
     */
    public StatusBarHelper(@NonNull Activity activity, @Nullable View actionBarView) {
        this.window = activity.getWindow();
        this.actionBarView = actionBarView;
    }

    public StatusBarHelper(@Nullable Dialog dialog, @Nullable View actionBarView) {
        this.window = dialog.getWindow();
        this.actionBarView = actionBarView;
    }

    public StatusBarHelper setLightStatusBar(boolean lightStatusBar) {
        this.lightStatusBar = lightStatusBar;
        return this;
    }

    public StatusBarHelper setTransparentNavigationBar(boolean transparentNavigationBar) {
        this.transparentNavigationBar = transparentNavigationBar;
        return this;
    }

    public StatusBarHelper setWindow(Window window) {
        this.window = window;
        return this;
    }

    public void setFakeStatusBar(View fakeStatusBar) {
        this.fakeStatusBar = fakeStatusBar;
        if (fakeStatusBar != null) {
            fakeStatusBar.getLayoutParams().height = getStatusBarOffsetPx(window.getContext());
        }
    }

    public StatusBarHelper setActionBarView(View actionBarView) {
        this.actionBarView = actionBarView;
        return this;
    }

    /**
     * 大于4.4
     */
    public static boolean atLeastKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Default status dp = 24 or 25
     * mhdpi = dp * 1
     * hdpi = dp * 1.5
     * xhdpi = dp * 2
     * xxhdpi = dp * 3
     * eg : 1920x1080, xxhdpi, => status/all = 25/640(dp) = 75/1080(px)
     * <p/>
     * don't forget toolbar's dp = 48
     *
     * @return px
     */
    @IntRange(from = 0, to = 75)
    public static int getStatusBarOffsetPx(Context context) {
        if (!atLeastKitkat()) {
            return 0;
        }
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId = appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 获取StatusBar的高度。
     */
    @SuppressLint("PrivateApi")
    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;

        int x, statusBarHeight = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e) {
            AppLayerErrorCatcher.INSTANCE.throwException(e);
        }

        return statusBarHeight;
    }

    @IntRange(from = 0, to = 75)
    public static int getNavigationBarOffsetPx(Context context) {
        if (!atLeastKitkat()) {
            return 0;
        }
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId = appContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true);
        // context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        // TODO: 17/2/10
        // int offset = context.getResources().getDimensionPixelSize(tv.resourceId); // Error
        int offset = (int) tv.getDimension(context.getResources().getDisplayMetrics());
        offset = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        // TypedArrayUtils.getBoolean()

        String string = TypedValue.coerceToString(tv.type, tv.data);
        LogHelper.Companion.getSystem().d("coerceToString " + string);
        LogHelper.Companion.getSystem().d("toString " + tv.toString());

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        // typedArray.getResourceId()
        int offsetV2 = typedArray.getDimensionPixelOffset(0, 0);

        LogHelper.Companion.getSystem().d("actionBarSize " + offsetV2);

        // mSwipeRefreshLayout.setProgressViewOffset(false, 0, context.getResources().getDimensionPixelSize(tv.resourceId));
        // mSwipeRefreshLayout.setRefreshing(true);
        return offset;
    }

    public static void setFitTranslucent(final View v, final int offset, final boolean changeHeight) {
        if (v == null || !atLeastKitkat()) {
            return;
        }

        int[] coor = new int[2];
        v.getLocationOnScreen(coor);
        if (coor[1] > 0) {
            return;
        }

        if (changeHeight) {
            v.post(() -> {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + getStatusBarOffsetPx(v.getContext()) + offset, v.getPaddingRight(), v.getPaddingBottom());
                v.getLayoutParams().height += getStatusBarOffsetPx(v.getContext()) + offset;
            });
        } else {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + getStatusBarOffsetPx(v.getContext()) + offset, v.getPaddingRight(), v.getPaddingBottom());

        }

    }

    private void processActionBar(final View actionBarView) {
        if (!mActionBarInitialized && actionBarView != null && atLeastKitkat() && !mOnlyImmersiveAbove6 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActionBarInitialized = true;
            actionBarView.post(() -> actionBarView.setPadding(actionBarView.getPaddingLeft(), actionBarView.getPaddingTop() + getStatusBarOffsetPx(actionBarView.getContext()), actionBarView.getPaddingRight(), actionBarView.getPaddingBottom()));
        }
    }

    public void process(@ColorInt int color) {
        if (actionBarView != null && color == Color.TRANSPARENT) {
            processActionBar(actionBarView);
        }
        if (mOnlyImmersiveAbove6) {
            if (currentVersion >= Build.VERSION_CODES.M) {
                process21(color);
                process23(color);
                processPrivateAPI();
            }
        } else {
            if (currentVersion >= Build.VERSION_CODES.M) {
                processPrivateAPI();
                process23(color);
            } else if (currentVersion >= Build.VERSION_CODES.LOLLIPOP) {
                if (!processPrivateAPI()) {
                    if (lightStatusBar) {
                        setupFakeStatusBar();
                    } else {
                        removeFakeStatusBar();
                    }
                }
                process21(color);
            } else if (currentVersion >= Build.VERSION_CODES.KITKAT) {
                if (!processPrivateAPI()) {
                    if (lightStatusBar) {
                        setupFakeStatusBar();
                    } else {
                        removeFakeStatusBar();
                    }
                }
                process19(color == Color.TRANSPARENT);
            }
        }
    }

    /**
     * 部署fakeStatusBar
     */
    private void setupFakeStatusBar() {
        if (fakeStatusBar != null) {
            fakeStatusBar.setVisibility(lightStatusBar ? View.VISIBLE : View.GONE);
        }
    }

    private void removeFakeStatusBar() {
        if (fakeStatusBar != null) {
            fakeStatusBar.setVisibility(View.GONE);
        }
    }

    /**
     * 调用私有API处理颜色
     */
    private boolean processPrivateAPI() {
        boolean handled = processFlyMe();
        if (!handled) {
            handled = processMIUI(lightStatusBar);
        }
        return handled;
    }


    /**
     * 处理4.4沉浸
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void process19(boolean transparentStatusBar) {
        WindowManager.LayoutParams winParams = window.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        int visibleFlag = window.getDecorView().getSystemUiVisibility();
        visibleFlag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (transparentStatusBar) {
            winParams.flags |= bits;
        } else {
            if (!keepPreviousWhenNotTransparentStatusBar) {
                winParams.flags &= ~bits;
            }

        }
        window.getDecorView().setSystemUiVisibility(visibleFlag);
        window.setAttributes(winParams);
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上
     * Tested on: MIUIV7 5.0 Redmi-Note3
     */
    @SuppressLint("PrivateApi")
    private boolean processMIUI(boolean lightStatusBar) {
        try {
            Class<? extends Window> clazz = window.getClass();
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 6.0：
     * <style name="statusBarStyle" parent="@android:style/Theme.DeviceDefault.Light">
     * <item name="android:statusBarColor">@color/status_bar_color</item>
     * <item name="android:windowLightStatusBar">false</item>
     * </style>
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void process21(@ColorInt int color) {
        isProcess23(false, color);
    }

    @SuppressLint({"ResourceAsColor", "InlinedApi"})
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void isProcess23(boolean isProcess23, @ColorInt int color) {
        int flag = window.getDecorView().getSystemUiVisibility();
        int windowFlag = window.getAttributes().flags;

        if (isProcess23) {
            if (lightStatusBar) {
                windowFlag |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
                flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.setStatusBarColor(color);
            } else {
                flag &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                flag &= ~WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        } else {
            if (lightStatusBar) {
                flag |= (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            } else {
                flag &= ~WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        }


        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        // 5.0以上清除FLAG_TRANSLUCENT_STATUS，因为它让SYSTEM_UI_FLAG_LIGHT_STATUS_BAR和StatusBarColor失效
        if (!isEMUI3_1()) {
            // 因为EMUI3.1系统与这种沉浸式方案API有点冲突，会没有沉浸式效果，所以EMUI3.1系统不清除FLAG_TRANSLUCENT_STATUS
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        windowFlag |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        if (color == Color.TRANSPARENT) {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // X 因为transparentStatusBar一一般搭配fitSystemWindow, 比如CoordinatorLayout，他们会自己绘制statusBar的颜色，就不需要重复设置了
            // 设置状态栏全透明（需要FLAG_DRAWS_SYSTEM_BAR_BACKGROUND以及 clear FLAG_TRANSLUCENT_STATUS）
            windowFlag |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            window.setStatusBarColor(color);
        } else {
            if (!keepPreviousWhenNotTransparentStatusBar) {
                flag &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                flag &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.setStatusBarColor(color);
            }
        }

        if (transparentNavigationBar) {
            flag |= (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            // 设置状态栏全透明（需要FLAG_DRAWS_SYSTEM_BAR_BACKGROUND以及 clear FLAG_TRANSLUCENT_STATUS）
            windowFlag |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            window.setNavigationBarColor(color);
        }
        windowAttributes.flags = windowFlag;
        window.setAttributes(windowAttributes);
        window.getDecorView().setSystemUiVisibility(flag);


        if (color == Color.TRANSPARENT) {
//            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            windowFlag |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            window.setStatusBarColor(color);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void process23(@ColorInt int color) {
        isProcess23(true, color);
    }


    private boolean processFlyMe() {
        try {
//            Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
//            Field value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            /*
             * 有点纳闷
             * WindowManager.LayoutParams.class.getField("statusBarColor")，修改这个数值就可以改颜色，而且说了是状态栏颜色，为啥改的是状态栏字体颜色
             */
            if (lightStatusBar) {
                String blackColor = "#222222";
                BugMeStatusBarColorUtils.INSTANCE.setStatusBarDarkIcon(window, Color.parseColor(blackColor));
            } else {
                String whiteColor = "#ffffff";
                BugMeStatusBarColorUtils.INSTANCE.setStatusBarDarkIcon(window, Color.parseColor(whiteColor));
            }
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    public static boolean isEMUI3_1() {
        return "EmotionUI_3.1".equals(getEmuiVersion());
    }

    private static String mEmotionUIVersion;

    @SuppressLint("PrivateApi")
    private static String getEmuiVersion() {
        if (mEmotionUIVersion != null) {
            return mEmotionUIVersion;
        }
        Class<?> classType;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            mEmotionUIVersion = (String) getMethod.invoke(classType, "ro.build.version.emui");
            return mEmotionUIVersion;
        } catch (Exception e) {
            // DebugUtil.exception(TAG,e);
        }
        return "";
    }

    public void clear() {
        if (fakeStatusBar != null) {
            fakeStatusBar = null;
        }
    }

    private static int mStatuBarType = 0;

    private final static int STATUSBAR_TYPE_DEFAULT = 0;
    private final static int STATUSBAR_TYPE_MIUI = 1;
    private final static int STATUSBAR_TYPE_FLYME = 2;
    private final static int STATUSBAR_TYPE_ANDROID6 = 3; // Android 6.0


    /**
     * 设置状态栏白色字体图标
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     */
    public static boolean setStatusBarDarkMode(Activity activity) {
        if (mStatuBarType == STATUSBAR_TYPE_DEFAULT) {
            // 默认状态，不需要处理
            return true;
        }

        if (mStatuBarType == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatuBarType == STATUSBAR_TYPE_FLYME) {
            return QMUIStatusBarHelper.FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatuBarType == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), false);
        }
        return true;
    }


    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     */
    public static boolean setStatusBarLightMode(Activity activity) {
        // 无语系列：ZTK C2016只能时间和电池图标变色。。。。
        if (QMUIDeviceHelper.isZTKC2016()) {
            return false;
        }

        if (mStatuBarType != STATUSBAR_TYPE_DEFAULT) {
            return setStatusBarLightMode(activity, mStatuBarType);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMIUICustomStatusBarLightModeImpl() && MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                mStatuBarType = STATUSBAR_TYPE_MIUI;
                return true;
            } else if (QMUIStatusBarHelper.FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                mStatuBarType = STATUSBAR_TYPE_FLYME;
                return true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Android6SetStatusBarLightMode(activity.getWindow(), true);
                mStatuBarType = STATUSBAR_TYPE_ANDROID6;
                return true;
            }
        }
        return false;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     * @param type     StatusBar 类型，对应不同的系统
     */
    private static boolean setStatusBarLightMode(Activity activity, int type) {
        if (type == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_FLYME) {
            return QMUIStatusBarHelper.FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), true);
        }
        return false;
    }


    /**
     * 设置状态栏字体图标为深色，Android 6
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @TargetApi(23)
    private static boolean Android6SetStatusBarLightMode(Window window, boolean light) {
        View decorView = window.getDecorView();
        int systemUi = light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        systemUi = changeStatusBarModeRetainFlag(window, systemUi);
        decorView.setSystemUiVisibility(systemUi);
        return true;
    }


    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window window, int out) {
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return out;
    }

    public static int retainSystemUiFlag(Window window, int out, int type) {
        int now = window.getDecorView().getSystemUiVisibility();
        if ((now & type) == type) {
            out |= type;
        }
        return out;
    }

    /**
     * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9之后用回Android原生实现
     * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean isMIUICustomStatusBarLightModeImpl() {
        return QMUIDeviceHelper.isMIUIV5() || QMUIDeviceHelper.isMIUIV6() ||
                QMUIDeviceHelper.isMIUIV7() || QMUIDeviceHelper.isMIUIV8();
    }
}
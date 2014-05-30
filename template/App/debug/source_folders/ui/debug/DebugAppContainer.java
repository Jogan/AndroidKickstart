package {package_name}.ui.debug;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jakewharton.madge.MadgeFrameLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import {package_name}.BuildConfig;
import {package_name}.R;
import {package_name}.{app_class_prefix}App;
import {package_name}.data.AnimationSpeed;
import {package_name}.data.NetworkLoggingLevel;
import {package_name}.data.PicassoDebugging;
import {package_name}.data.PixelGridEnabled;
import {package_name}.data.PixelRatioEnabled;
import {package_name}.data.ScalpelEnabled;
import {package_name}.data.ScalpelWireframeEnabled;
import {package_name}.data.SeenDebugDrawer;
import {package_name}.data.prefs.BooleanPreference;
import {package_name}.data.prefs.IntPreference;
import {package_name}.data.prefs.StringPreference;
import {package_name}.data.api.dummyapi.DummyApi;
import {package_name}.ui.AppContainer;
import {package_name}.ui.activities.MainActivity;
import {package_name}.util.Strings;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static butterknife.ButterKnife.findById;
import static java.net.Proxy.Type.HTTP;
import static retrofit.RestAdapter.LogLevel;

/**
 * An {@link AppContainer} for debug builds which wrap the content view with a sliding drawer on
 * the right that holds all of the debug information and settings.
 */
@Singleton
public class DebugAppContainer implements AppContainer {
  private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

  private final Picasso picasso;
  private final IntPreference networkLoggingLevel;
  private final IntPreference animationSpeed;
  private final BooleanPreference picassoDebugging;
  private final BooleanPreference pixelGridEnabled;
  private final BooleanPreference pixelRatioEnabled;
  private final BooleanPreference scalpelEnabled;
  private final BooleanPreference scalpelWireframeEnabled;
  private final BooleanPreference seenDebugDrawer;
  private final RestAdapter dummyRestAdapter;

  {app_class_prefix}App app;
  Activity activity;
  Context drawerContext;

  @Inject public DebugAppContainer(Picasso picasso,
      @NetworkLoggingLevel IntPreference networkLoggingLevel,
      @AnimationSpeed IntPreference animationSpeed,
      @PicassoDebugging BooleanPreference picassoDebugging,
      @PixelGridEnabled BooleanPreference pixelGridEnabled,
      @PixelRatioEnabled BooleanPreference pixelRatioEnabled,
      @ScalpelEnabled BooleanPreference scalpelEnabled,
      @ScalpelWireframeEnabled BooleanPreference scalpelWireframeEnabled,
      @SeenDebugDrawer BooleanPreference seenDebugDrawer,
      @DummyApi RestAdapter dummyRestAdapter) {
    this.picasso = picasso;
    this.networkLoggingLevel = networkLoggingLevel;
    this.scalpelEnabled = scalpelEnabled;
    this.scalpelWireframeEnabled = scalpelWireframeEnabled;
    this.seenDebugDrawer = seenDebugDrawer;
    this.animationSpeed = animationSpeed;
    this.picassoDebugging = picassoDebugging;
    this.pixelGridEnabled = pixelGridEnabled;
    this.pixelRatioEnabled = pixelRatioEnabled;
    this.dummyRestAdapter = dummyRestAdapter;
  }

  @InjectView(R.id.debug_drawer_layout) DrawerLayout drawerLayout;
  @InjectView(R.id.debug_content) ViewGroup content;

  @InjectView(R.id.madge_container) MadgeFrameLayout madgeFrameLayout;
  @InjectView(R.id.debug_content) ScalpelFrameLayout scalpelFrameLayout;

  @InjectView(R.id.debug_contextual_title) View contextualTitleView;
  @InjectView(R.id.debug_contextual_list) LinearLayout contextualListView;

  @InjectView(R.id.debug_network_logging) Spinner networkLoggingView;

  @InjectView(R.id.debug_ui_animation_speed) Spinner uiAnimationSpeedView;
  @InjectView(R.id.debug_ui_pixel_grid) Switch uiPixelGridView;
  @InjectView(R.id.debug_ui_pixel_ratio) Switch uiPixelRatioView;
  @InjectView(R.id.debug_ui_scalpel) Switch uiScalpelView;
  @InjectView(R.id.debug_ui_scalpel_wireframe) Switch uiScalpelWireframeView;

  @InjectView(R.id.debug_build_name) TextView buildNameView;
  @InjectView(R.id.debug_build_code) TextView buildCodeView;
  @InjectView(R.id.debug_build_date) TextView buildDateView;

  @InjectView(R.id.debug_device_make) TextView deviceMakeView;
  @InjectView(R.id.debug_device_model) TextView deviceModelView;
  @InjectView(R.id.debug_device_resolution) TextView deviceResolutionView;
  @InjectView(R.id.debug_device_density) TextView deviceDensityView;
  @InjectView(R.id.debug_device_release) TextView deviceReleaseView;
  @InjectView(R.id.debug_device_api) TextView deviceApiView;

  @InjectView(R.id.debug_picasso_indicators) Switch picassoIndicatorView;
  @InjectView(R.id.debug_picasso_cache_size) TextView picassoCacheSizeView;
  @InjectView(R.id.debug_picasso_cache_hit) TextView picassoCacheHitView;
  @InjectView(R.id.debug_picasso_cache_miss) TextView picassoCacheMissView;
  @InjectView(R.id.debug_picasso_decoded) TextView picassoDecodedView;
  @InjectView(R.id.debug_picasso_decoded_total) TextView picassoDecodedTotalView;
  @InjectView(R.id.debug_picasso_decoded_avg) TextView picassoDecodedAvgView;
  @InjectView(R.id.debug_picasso_transformed) TextView picassoTransformedView;
  @InjectView(R.id.debug_picasso_transformed_total) TextView picassoTransformedTotalView;
  @InjectView(R.id.debug_picasso_transformed_avg) TextView picassoTransformedAvgView;

  @Override public ViewGroup get(final Activity activity, {app_class_prefix}App app) {
    this.app = app;
    this.activity = activity;
    drawerContext = activity;

    activity.setContentView(R.layout.debug_activity_frame);

    // Manually find the debug drawer and inflate the drawer layout inside of it.
    ViewGroup drawer = findById(activity, R.id.debug_drawer);
    LayoutInflater.from(drawerContext).inflate(R.layout.debug_drawer_content, drawer);

    // Inject after inflating the drawer layout so its views are available to inject.
    ButterKnife.inject(this, activity);

    // Set up the contextual actions to watch views coming in and out of the content area.
    Set<ContextualDebugActions.DebugAction<?>> debugActions = Collections.emptySet();
    ContextualDebugActions contextualActions = new ContextualDebugActions(this, debugActions);
    content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

    drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, Gravity.END);
    drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerOpened(View drawerView) {
        refreshPicassoStats();
      }
    });

    // If you have not seen the debug drawer before, show it with a message
    if (!seenDebugDrawer.get()) {
      drawerLayout.postDelayed(new Runnable() {
        @Override public void run() {
          drawerLayout.openDrawer(Gravity.END);
          Toast.makeText(activity, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
        }
      }, 1000);
      seenDebugDrawer.set(true);
    }

    setupNetworkSection();
    setupUserInterfaceSection();
    setupBuildSection();
    setupDeviceSection();
    setupPicassoSection();

    return content;
  }

  private void setupNetworkSection() {
      // We use the JSON rest adapter as the source of truth for the log level.
      final EnumAdapter<LogLevel> loggingAdapter = new EnumAdapter<>(activity, LogLevel.class);
      networkLoggingView.setAdapter(loggingAdapter);
      networkLoggingView.setSelection(networkLoggingLevel.get());
      networkLoggingView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
              LogLevel selected = loggingAdapter.getItem(position);
              if (selected != dummyRestAdapter.getLogLevel()) {
                  Timber.d("Setting logging level to %s", selected);
                  dummyRestAdapter.setLogLevel(selected);
                  networkLoggingLevel.set(selected.ordinal());
              } else {
                  Timber.d("Ignoring re-selection of logging level " + selected);
              }
          }

          @Override public void onNothingSelected(AdapterView<?> adapterView) {
          }
      });
  }

  private void setupUserInterfaceSection() {
    final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(activity);
    uiAnimationSpeedView.setAdapter(speedAdapter);
    final int animationSpeedValue = animationSpeed.get();
    uiAnimationSpeedView.setSelection(
        AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));
    uiAnimationSpeedView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        int selected = speedAdapter.getItem(position);
        if (selected != animationSpeed.get()) {
          Timber.d("Setting animation speed to %sx", selected);
          animationSpeed.set(selected);
          applyAnimationSpeed(selected);
        } else {
          Timber.d("Ignoring re-selection of animation speed %sx", selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });
    // Ensure the animation speed value is always applied across app restarts.
    content.post(new Runnable() {
      @Override public void run() {
        applyAnimationSpeed(animationSpeedValue);
      }
    });

    boolean gridEnabled = pixelGridEnabled.get();
    madgeFrameLayout.setOverlayEnabled(gridEnabled);
    uiPixelGridView.setChecked(gridEnabled);
    uiPixelRatioView.setEnabled(gridEnabled);
    uiPixelGridView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting pixel grid overlay enabled to " + isChecked);
        pixelGridEnabled.set(isChecked);
        madgeFrameLayout.setOverlayEnabled(isChecked);
        uiPixelRatioView.setEnabled(isChecked);
      }
    });

    boolean ratioEnabled = pixelRatioEnabled.get();
    madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
    uiPixelRatioView.setChecked(ratioEnabled);
    uiPixelRatioView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting pixel scale overlay enabled to " + isChecked);
        pixelRatioEnabled.set(isChecked);
        madgeFrameLayout.setOverlayRatioEnabled(isChecked);
      }
    });

    boolean scalpel = scalpelEnabled.get();
    scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
    uiScalpelView.setChecked(scalpel);
    uiScalpelWireframeView.setEnabled(scalpel);
    uiScalpelView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting scalpel interaction enabled to " + isChecked);
        scalpelEnabled.set(isChecked);
        scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
        uiScalpelWireframeView.setEnabled(isChecked);
      }
    });

    boolean wireframe = scalpelWireframeEnabled.get();
    scalpelFrameLayout.setDrawViews(!wireframe);
    uiScalpelWireframeView.setChecked(wireframe);
    uiScalpelWireframeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting scalpel wireframe enabled to " + isChecked);
        scalpelWireframeEnabled.set(isChecked);
        scalpelFrameLayout.setDrawViews(!isChecked);
      }
    });
  }

  private void setupBuildSection() {
    buildNameView.setText(BuildConfig.VERSION_NAME);
    buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));

    try {
      // Parse ISO8601-format time into local time.
      DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
      inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date buildTime = inFormat.parse(BuildConfig.BUILD_TIME);
      buildDateView.setText(DATE_DISPLAY_FORMAT.format(buildTime));
    } catch (ParseException e) {
      throw new RuntimeException("Unable to decode build time: " + BuildConfig.BUILD_TIME, e);
    }
  }

  private void setupDeviceSection() {
    DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
    String densityBucket = getDensityString(displayMetrics);
    deviceMakeView.setText(Strings.truncateAt(Build.MANUFACTURER, 20));
    deviceModelView.setText(Strings.truncateAt(Build.MODEL, 20));
    deviceResolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
    deviceDensityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
    deviceReleaseView.setText(Build.VERSION.RELEASE);
    deviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
  }

  private void setupPicassoSection() {
    boolean picassoDebuggingValue = picassoDebugging.get();
    picasso.setDebugging(picassoDebuggingValue);
    picassoIndicatorView.setChecked(picassoDebuggingValue);
    picassoIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        Timber.d("Setting Picasso debugging to " + isChecked);
        picasso.setDebugging(isChecked);
        picassoDebugging.set(isChecked);
      }
    });

    refreshPicassoStats();
  }

  private void refreshPicassoStats() {
    StatsSnapshot snapshot = picasso.getSnapshot();
    String size = getSizeString(snapshot.size);
    String total = getSizeString(snapshot.maxSize);
    int percentage = (int) ((1f * snapshot.size / snapshot.maxSize) * 100);
    picassoCacheSizeView.setText(size + " / " + total + " (" + percentage + "%)");
    picassoCacheHitView.setText(String.valueOf(snapshot.cacheHits));
    picassoCacheMissView.setText(String.valueOf(snapshot.cacheMisses));
    picassoDecodedView.setText(String.valueOf(snapshot.originalBitmapCount));
    picassoDecodedTotalView.setText(getSizeString(snapshot.totalOriginalBitmapSize));
    picassoDecodedAvgView.setText(getSizeString(snapshot.averageOriginalBitmapSize));
    picassoTransformedView.setText(String.valueOf(snapshot.transformedBitmapCount));
    picassoTransformedTotalView.setText(getSizeString(snapshot.totalTransformedBitmapSize));
    picassoTransformedAvgView.setText(getSizeString(snapshot.averageTransformedBitmapSize));
  }

  private void applyAnimationSpeed(int multiplier) {
    try {
      Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
      method.invoke(null, (float) multiplier);
    } catch (Exception e) {
      throw new RuntimeException("Unable to apply animation speed.", e);
    }
  }

  private static String getDensityString(DisplayMetrics displayMetrics) {
    switch (displayMetrics.densityDpi) {
      case DisplayMetrics.DENSITY_LOW:
        return "ldpi";
      case DisplayMetrics.DENSITY_MEDIUM:
        return "mdpi";
      case DisplayMetrics.DENSITY_HIGH:
        return "hdpi";
      case DisplayMetrics.DENSITY_XHIGH:
        return "xhdpi";
      case DisplayMetrics.DENSITY_XXHIGH:
        return "xxhdpi";
      case DisplayMetrics.DENSITY_XXXHIGH:
        return "xxxhdpi";
      case DisplayMetrics.DENSITY_TV:
        return "tvdpi";
      default:
        return "unknown";
    }
  }

  private static String getSizeString(long bytes) {
    String[] units = new String[] { "B", "KB", "MB", "GB" };
    int unit = 0;
    while (bytes >= 1024) {
      bytes /= 1024;
      unit += 1;
    }
    return bytes + units[unit];
  }

  /*private void showNewNetworkProxyDialog(final ProxyAdapter proxyAdapter) {
    final int originalSelection = networkProxy.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;

    View view = LayoutInflater.from(app).inflate(R.layout.debug_drawer_network_proxy, null);
    final EditText host = findById(view, R.id.debug_drawer_network_proxy_host);

    new AlertDialog.Builder(activity) //
        .setTitle("Set Network Proxy")
        .setView(view)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int i) {
            networkProxyView.setSelection(originalSelection);
            dialog.cancel();
          }
        })
        .setPositiveButton("Use", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int i) {
            String theHost = host.getText().toString();
            if (!Strings.isBlank(theHost)) {
              String[] parts = theHost.split(":", 2);
              SocketAddress address =
                  InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));

              networkProxy.set(theHost); // Persist across restarts.
              proxyAdapter.notifyDataSetChanged(); // Tell the spinner to update.
              networkProxyView.setSelection(ProxyAdapter.PROXY); // And show the proxy.

              client.setProxy(new Proxy(HTTP, address));
            } else {
              networkProxyView.setSelection(originalSelection);
            }
          }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialogInterface) {
            networkProxyView.setSelection(originalSelection);
          }
        })
        .show();
  }

  private void showCustomEndpointDialog(final int originalSelection, String defaultUrl) {
    View view = LayoutInflater.from(app).inflate(R.layout.debug_drawer_network_endpoint, null);
    final EditText url = findById(view, R.id.debug_drawer_network_endpoint_url);
    url.setText(defaultUrl);
    url.setSelection(url.length());

    new AlertDialog.Builder(activity) //
        .setTitle("Set Network Endpoint")
        .setView(view)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int i) {
            endpointView.setSelection(originalSelection);
            dialog.cancel();
          }
        })
        .setPositiveButton("Use", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int i) {
            String theUrl = url.getText().toString();
            if (!Strings.isBlank(theUrl)) {
              setEndpointAndRelaunch(theUrl);
            } else {
              endpointView.setSelection(originalSelection);
            }
          }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialogInterface) {
            endpointView.setSelection(originalSelection);
          }
        })
        .show();
  }

  private void setEndpointAndRelaunch(String endpoint) {
    Timber.d("Setting network endpoint to %s", endpoint);
    networkEndpoint.set(endpoint);

    Intent newApp = new Intent(app, MainActivity.class);
    newApp.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
    app.startActivity(newApp);
    app.buildObjectGraphAndInject();
  }*/
}

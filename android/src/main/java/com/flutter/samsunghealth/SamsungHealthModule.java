package com.flutter.samsunghealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public final class SamsungHealthModule implements MethodChannel.MethodCallHandler {

    private static final String DAY_TIME = "day_time";
    private static final String SOURCE_TYPE = "source_type";

    private Activity activity;
    private AtomicBoolean isConnected = new AtomicBoolean(false);

    private HealthDataStore healthDataStore;

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
        if (activity == null || activity.isFinishing()) return;
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        if (error.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    alert.setMessage("Please install Samsung Health");
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    alert.setMessage("Please upgrade Samsung Health");
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    alert.setMessage("Please enable Samsung Health");
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    alert.setMessage("Please agree with Samsung Health policy");
                    break;
                default:
                    alert.setMessage("Please make Samsung Health available");
                    break;
            }
        } else {
            alert.setMessage("Samsung Health is not available");
        }
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (error.hasResolution()) {
                    error.resolve(activity);
                }
            }
        });
        if (error.hasResolution()) {
            alert.setNegativeButton(android.R.string.cancel, null);
        }
        alert.show();
    }

    private void readSteps(@NonNull final ReadRequest readRequest, @NonNull final MethodChannel.Result result) {
        final HealthDataResolver resolver = new HealthDataResolver(healthDataStore, null);

        final HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(
                HealthDataResolver.Filter.greaterThanEquals(DAY_TIME, readRequest.getDateFrom()),
                HealthDataResolver.Filter.lessThanEquals(DAY_TIME, readRequest.getDateTo()),
                HealthDataResolver.Filter.eq(SOURCE_TYPE, -2));

        final HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(readRequest.getDataType())
                .setFilter(filter)
                .build();

        try {
            resolver.read(request).setResultListener(new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @Override
                public void onResult(final HealthDataResolver.ReadResult healthData) {
                    double totalCount = 0;
                    try {
                        final Iterator<HealthData> iterator = healthData.iterator();
                        if (iterator.hasNext()) {
                            final HealthData data = iterator.next();
                            totalCount = data.getInt(HealthConstants.StepCount.COUNT);
                        }
                        result.success(totalCount);
                    } catch (Exception e) {
                        result.error("READ_STEPS", "Getting step count fails.", null);
                    } finally {
                        healthData.close();
                    }
                }
            });
        } catch (Exception e) {
            result.error("READ_STEPS", "Getting step count fails.", null);
        }
    }

    @Override
    public final void onMethodCall(@NonNull final MethodCall call, @NonNull final MethodChannel.Result result) {
        if (!isConnected.get()) {
            result.error("CONNECTION_ERROR", "Cannot connect to SamsungHealth service", null);
            return;
        }
        switch (call.method) {
            case "hasPermissions": {
                connectAndExecute(new Runnable() {
                    @Override
                    public void run() {
                        final PermissionRequest permissionRequest = PermissionRequest.fromCall(call);
                        hasPermissions(permissionRequest, result);
                    }
                });
                break;
            }
            case "requestPermissions": {
                connectAndExecute(new Runnable() {
                    @Override
                    public void run() {
                        final PermissionRequest permissionRequest = PermissionRequest.fromCall(call);
                        requestPermissions(permissionRequest, result);
                    }
                });
                break;
            }
            case "read":
                connectAndExecute(new Runnable() {
                    @Override
                    public void run() {
                        final ReadRequest readRequest = ReadRequest.fromCall(call);
                        readSteps(readRequest, result);
                    }
                });
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void connectAndExecute(final Runnable task) {
        if (isConnected.get()) {
            task.run();
        } else {
            final HealthDataService healthDataService = new HealthDataService();
            try {
                healthDataService.initialize(activity.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            healthDataStore = new HealthDataStore(activity.getApplicationContext(), new HealthDataStore.ConnectionListener() {
                @Override
                public final void onConnected() {
                    isConnected.set(true);
                    if (activity != null) task.run();
                }

                @Override
                public final void onConnectionFailed(final HealthConnectionErrorResult healthConnectionErrorResult) {
                    isConnected.set(false);
                    showConnectionFailureDialog(healthConnectionErrorResult);
                }

                @Override
                public final void onDisconnected() {
                    isConnected.set(false);
                    if (activity != null && !activity.isFinishing()) {
                        healthDataStore.disconnectService();
                    }
                }
            });
            healthDataStore.connectService();
        }
    }

    private void hasPermissions(final PermissionRequest permissionRequest, final MethodChannel.Result result) {
        final HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        final Set<HealthPermissionManager.PermissionKey> permissionKeyList = new HashSet<>();
        for (final String dataType : permissionRequest.getTypes()) {
            permissionKeyList.add(new HealthPermissionManager.PermissionKey(dataType, HealthPermissionManager.PermissionType.READ));
        }
        try {
            final Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(permissionKeyList);
            result.success(!resultMap.containsValue(Boolean.FALSE));
            return;
        } catch (Exception e) {
            result.error("HAS_PERMMISSIONS", e.getMessage(), null);
        }
        result.success(false);
    }

    private void requestPermissions(final PermissionRequest permissionRequest, final MethodChannel.Result result) {
        final HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        final Set<HealthPermissionManager.PermissionKey> permissionKeySet = new HashSet<>();
        for (final String dataType : permissionRequest.getTypes()) {
            permissionKeySet.add(new HealthPermissionManager.PermissionKey(dataType, HealthPermissionManager.PermissionType.READ));
        }
        try {
            pmsManager.requestPermissions(permissionKeySet, activity)
                    .setResultListener(new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
                        @Override
                        public final void onResult(final HealthPermissionManager.PermissionResult permissionResult) {
                            final Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = permissionResult.getResultMap();
                            result.success(!resultMap.containsValue(Boolean.FALSE));
                        }
                    });
        } catch (Exception e) {
            result.error("REQUEST_PERMISSIONS", e.getMessage(), "Permission setting fails.");
        }
    }

    final void onAttachToActivity(final Activity activity) {
        this.activity = activity;
    }

    final void onDetachedFromActivity() {
        this.activity = null;
    }

    final void release() {
        healthDataStore.disconnectService();
    }
}
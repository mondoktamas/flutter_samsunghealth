package com.flutter.samsunghealth;

import com.samsung.android.sdk.healthdata.HealthConstants;

import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodCall;

final class PermissionRequest {
    private final List<String> types;

    private PermissionRequest(final List<String> types) {
        this.types = types;
    }

    static PermissionRequest fromCall(final MethodCall methodCall) {
        final List<String> dartTypes = methodCall.argument("types");
        final List<String> types = new ArrayList<>();
        for (String type : dartTypes) {
            types.add(fromDartType(type));
        }
        return new PermissionRequest(types);
    }

    public final List<String> getTypes() {
        return types;
    }

    private static String fromDartType(final String dartType) {
        switch (dartType) {
            case "step_count":
//                return HealthConstants.StepCount.HEALTH_DATA_TYPE;
                return "com.samsung.shealth.step_daily_trend";
            case "water":
                return HealthConstants.WaterIntake.HEALTH_DATA_TYPE;
            case "heart_rate":
                return HealthConstants.HeartRate.HEALTH_DATA_TYPE;
            case "height":
                return HealthConstants.Height.HEALTH_DATA_TYPE;
            case "weight":
                return HealthConstants.Weight.HEALTH_DATA_TYPE;
            case "sleep":
                return HealthConstants.Sleep.HEALTH_DATA_TYPE;
            default:
                throw new RuntimeException(String.format("type %s is not supported", dartType));
        }
    }
}

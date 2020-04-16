package com.flutter.samsunghealth;

import androidx.annotation.Nullable;

import com.samsung.android.sdk.healthdata.HealthConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.flutter.plugin.common.MethodCall;

final class ReadRequest {

    private final String dataType;
    private final Long dateFrom;
    private final Long dateTo;

    private ReadRequest(final String dataType, final Long dateFrom, final Long dateTo) {
        this.dataType = dataType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    static ReadRequest fromCall(final MethodCall methodCall) {
        final String dartType = fromDartType(methodCall.<String>argument("type"));
        return new ReadRequest(dartType, safeLong(methodCall, "date_from"), safeLong(methodCall, "date_to"));
    }

    public final String getDataType() {
        return dataType;
    }

    public final Long getDateFrom() {
        return dateFrom;
    }

    public final Long getDateTo() {
        return dateTo;
    }

    private static Long safeLong(final MethodCall call, final String key) {
        final Object value = call.argument(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return null;
        }
    }

    private static String fromDartType(final @Nullable String dartType) {
        if (dartType == null)
            throw new RuntimeException("type null is not supported");
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

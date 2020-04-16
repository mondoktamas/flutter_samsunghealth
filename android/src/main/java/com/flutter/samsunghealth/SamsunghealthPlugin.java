package com.flutter.samsunghealth;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodChannel;

/**
 * SamsunghealthPlugin
 */
public final class SamsunghealthPlugin implements FlutterPlugin, ActivityAware {

    private MethodChannel methodChannel;
    private SamsungHealthModule samsungHealthModule;

    @Override
    public final void onAttachedToEngine(@NonNull final FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "samsunghealth");
    }

    @Override
    public final void onAttachedToActivity(final ActivityPluginBinding binding) {
        samsungHealthModule = new SamsungHealthModule(binding.getActivity().getApplicationContext());
        samsungHealthModule.onAttachToActivity(binding.getActivity());
        methodChannel.setMethodCallHandler(samsungHealthModule);
    }

    @Override
    public final void onDetachedFromActivityForConfigChanges() {
        samsungHealthModule.onDetachedFromActivity();
    }

    @Override
    public final void onReattachedToActivityForConfigChanges(final ActivityPluginBinding binding) {
        samsungHealthModule.onAttachToActivity(binding.getActivity());
    }

    @Override
    public final void onDetachedFromEngine(@NonNull final FlutterPluginBinding binding) {
        samsungHealthModule.release();
    }

    @Override
    public final void onDetachedFromActivity() {
        samsungHealthModule.release();
        samsungHealthModule.onDetachedFromActivity();
    }
}
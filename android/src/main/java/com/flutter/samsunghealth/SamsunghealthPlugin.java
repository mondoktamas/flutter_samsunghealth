package com.flutter.samsunghealth;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * SamsunghealthPlugin
 */
public final class SamsunghealthPlugin implements FlutterPlugin, ActivityAware {

    private final static String CHANNEL = "plugins.flutter.io/samsunghealth";
    private MethodChannel methodChannel;
    private SamsungHealthModule samsungHealthModule;

    public static void registerWith(final Registrar registrar) {
        final SamsunghealthPlugin plugin = new SamsunghealthPlugin();
        plugin.setUpChannel(registrar.messenger());
        plugin.attachToActivity(registrar.activity());
    }

    @Override
    public final void onAttachedToEngine(@NonNull final FlutterPluginBinding flutterPluginBinding) {
        setUpChannel(flutterPluginBinding.getBinaryMessenger());
    }

    @Override
    public final void onAttachedToActivity(final ActivityPluginBinding binding) {
        attachToActivity(binding.getActivity());
    }

    @Override
    public final void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public final void onReattachedToActivityForConfigChanges(final ActivityPluginBinding binding) {
        attachToActivity(binding.getActivity());
    }

    @Override
    public final void onDetachedFromEngine(@NonNull final FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
        samsungHealthModule.release();
    }

    @Override
    public final void onDetachedFromActivity() {
        methodChannel.setMethodCallHandler(null);
        samsungHealthModule.release();
        samsungHealthModule.onDetachedFromActivity();
    }

    private void setUpChannel(final BinaryMessenger messenger) {
        samsungHealthModule = new SamsungHealthModule();
        methodChannel = new MethodChannel(messenger, CHANNEL);
        methodChannel.setMethodCallHandler(samsungHealthModule);
    }

    private void attachToActivity(final Activity activity) {
        samsungHealthModule.onAttachToActivity(activity);
    }
}
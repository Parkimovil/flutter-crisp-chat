package com.alaminkarno.flutter_crisp_chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

// Añade estas importaciones
import java.util.HashMap;
import java.util.Map; 
import im.crisp.client.external.ChatActivity;
import im.crisp.client.external.Crisp;
import im.crisp.client.external.data.SessionEvent;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterCrispChatPlugin
 */
public class FlutterCrispChatPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private static final String CHANNEL_NAME = "flutter_crisp_chat";

    private MethodChannel channel;
    private Context context;
    private Activity activity;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        this.activity = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "openCrispChat":
                openCrispChat(call, result);
                break;
            case "resetCrispChatSession":
                Crisp.resetChatSession(context);
                result.success(null);
                break;
            case "setSessionString":
                setSessionString(call);
                break;
            case "setSessionInt":
                setSessionInt(call);
                break;
            case "getSessionIdentifier":
                getSessionIdentifier(result);
                break;
            case "pushSessionEvent":
                pushSessionEvent(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void openCrispChat(@NonNull MethodCall call, @NonNull Result result) {
        HashMap<String, Object> args = (HashMap<String, Object>) call.arguments;
        if (args != null) {
            CrispConfig config = CrispConfig.fromJson(args);
            if (config.tokenId != null) {
                Crisp.configure(context, config.websiteId, config.tokenId);
            } else {
                Crisp.configure(context, config.websiteId);
            }
            setCrispData(context, config);
            openActivity();
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    private void setSessionString(@NonNull MethodCall call) {
        HashMap<String, Object> args = (HashMap<String, Object>) call.arguments;
        if (args != null) {
            String key = (String) args.get("key");
            String value = (String) args.get("value");
            Crisp.setSessionString(key, value);
        }
    }

    private void setSessionInt(@NonNull MethodCall call) {
        HashMap<String, Object> args = (HashMap<String, Object>) call.arguments;
        if (args != null) {
            String key = (String) args.get("key");
            int value = (int) args.get("value");
            Crisp.setSessionInt(key, value);
        }
    }

    private void getSessionIdentifier(@NonNull Result result) {
        String sessionId = Crisp.getSessionIdentifier(context);
        if (sessionId != null) {
            result.success(sessionId);
        } else {
            result.error("NO_SESSION", "No active session found", null);
        }
    }

    private void pushSessionEvent(@NonNull MethodCall call, @NonNull Result result) {
        try {
            Map<String, Object> args = (Map<String, Object>) call.arguments;
            if (args == null) {
                result.error("INVALID_ARGS", "Arguments are required", null);
                return;
            }
            // eventType es obligatorio
            String eventType = args.containsKey("eventType") ? args.get("eventType").toString() : "";
            // Si deseas un eventData, puedes obtenerlo aquí:
            // String eventData = args.containsKey("eventData") ? args.get("eventData").toString() : "";

            // Construimos SessionEvent (modifica si Crisp SDK necesita algo distinto)
            SessionEvent sessionEvent = new SessionEvent(eventType, SessionEvent.Color.ORANGE);

            // Llamamos a Crisp para enviar el evento
            Crisp.pushSessionEvent(sessionEvent);

            result.success("Session event pushed successfully");
        } catch (Exception e) {
            result.error("ERROR", "Failed to push session event: " + e.getMessage(), null);
        }
    }

    private void setCrispData(Context context, CrispConfig config) {
        if (config.tokenId != null) {
            Crisp.setTokenID(context, config.tokenId);
        }
        if (config.sessionSegment != null) {
            Crisp.setSessionSegment(config.sessionSegment);
        }
        if (config.user != null) {
            if (config.user.nickName != null) {
                Crisp.setUserNickname(config.user.nickName);
            }
            if (config.user.email != null) {
                boolean result = Crisp.setUserEmail(config.user.email);
                if(!result){
                    Log.d("CRSIP_CHAT","Email not set");
                }
            }
            if (config.user.avatar != null) {
                boolean result = Crisp.setUserAvatar(config.user.avatar);
                if(!result){
                    Log.d("CRSIP_CHAT","Avatar not set");
                }
            }
            if (config.user.phone != null) {
                boolean result = Crisp.setUserPhone(config.user.phone);
                if(!result){
                    Log.d("CRSIP_CHAT","Phone not set");
                }
            }
            if (config.user.company != null) {
                Crisp.setUserCompany(config.user.company.toCrispCompany());
            }
        }
    }

    private void openActivity() {
        Intent intent = new Intent(context, ChatActivity.class);
        if (activity != null) {
            activity.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        context = null;
    }
}
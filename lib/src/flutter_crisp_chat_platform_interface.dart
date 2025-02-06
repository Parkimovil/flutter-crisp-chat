import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'config.dart';
import 'flutter_crisp_chat_method_channel.dart';

/// An implementation of [FlutterCrispChatPlatform] that uses method channels.
abstract class FlutterCrispChatPlatform extends PlatformInterface {
  /// Constructs a FlutterCrispChatPlatform.
  FlutterCrispChatPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterCrispChatPlatform _instance = MethodChannelFlutterCrispChat();

  /// The default instance of [FlutterCrispChatPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterCrispChat].
  static FlutterCrispChatPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterCrispChatPlatform] when
  /// they register themselves.
  static set instance(FlutterCrispChatPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// [openCrispChat] calls native platform code with the provided [CrispConfig].
  Future<void> openCrispChat({required CrispConfig config}) {
    throw UnimplementedError('openCrispChat() has not been implemented.');
  }

  /// [resetCrispChatSession] calls native platform code to reset the Crisp chat session.
  Future<void> resetCrispChatSession() {
    throw UnimplementedError('resetCrispChatSession() has not been implemented.');
  }

  /// [setSessionString] calls native platform code to set a session string with the given key and value.
  void setSessionString({required String key, required String value}) {
    throw UnimplementedError('setSessionString() has not been implemented.');
  }

  /// [setSessionInt] calls native platform code to set a session integer with the given key and value.
  void setSessionInt({required String key, required int value}) {
    throw UnimplementedError('setSessionInt() has not been implemented.');
  }

  /// [getSessionIdentifier] retrieves the current session identifier from the native platform.
  Future<String?> getSessionIdentifier() {
    throw UnimplementedError('getSessionIdentifier() has not been implemented.');
  }

  /// [pushSessionEvent] calls native platform code to push a session event.
  /// [eventType] is required, and [eventData] is optional.
  Future<void> pushSessionEvent({required String eventType}) {
    throw UnimplementedError('pushSessionEvent() has not been implemented.');
  }
}
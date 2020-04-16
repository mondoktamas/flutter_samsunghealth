import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:samsunghealth/samsunghealth.dart';

void main() {
  const MethodChannel channel = MethodChannel('samsunghealth');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Samsunghealth.platformVersion, '42');
  });
}

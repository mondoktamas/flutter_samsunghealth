import 'dart:async';

import 'package:flutter/services.dart';

class Samsunghealth {
  static const MethodChannel _channel =
      const MethodChannel('plugins.flutter.io/samsunghealth');

  static Future<bool> hasPermissions(List<DataType> types) async {
    return await _channel.invokeMethod('hasPermissions', {
      "types": types.map((type) => _dataTypeToString(type)).toList(),
    });
  }

  static Future<bool> requestPermissions(List<DataType> types) async {
    return await _channel.invokeMethod('requestPermissions', {
      "types": types.map((type) => _dataTypeToString(type)).toList(),
    });
  }

  static Future<double> read(
      DataType type, {
        DateTime dateFrom,
        DateTime dateTo,
      }) async {
    return await _channel.invokeMethod('read', {
      "type": _dataTypeToString(type),
      "date_from": dateFrom?.millisecondsSinceEpoch ?? 1,
      "date_to": (dateTo ?? DateTime.now()).millisecondsSinceEpoch,
    });
  }

  static String _dataTypeToString(DataType type) {
    switch (type) {
      case DataType.HEART_RATE:
        return "heart_rate";
      case DataType.STEP_COUNT:
        return "step_count";
      case DataType.HEIGHT:
        return "height";
      case DataType.WEIGHT:
        return "weight";
      case DataType.DISTANCE:
        return "distance";
      case DataType.ENERGY:
        return "energy";
      case DataType.WATER:
        return "water";
      case DataType.SLEEP:
        return "sleep";
      case DataType.STAND_TIME:
        return "stand_time";
      case DataType.EXERCISE_TIME:
        return "exercise_time";
    }
    throw Exception('dataType $type not supported');
  }
}

enum DataType { HEART_RATE, STEP_COUNT, HEIGHT, WEIGHT, DISTANCE, ENERGY, WATER, SLEEP, STAND_TIME, EXERCISE_TIME }

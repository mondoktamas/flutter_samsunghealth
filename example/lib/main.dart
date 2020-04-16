import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:samsunghealth/samsunghealth.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  double stepCount = 0.0;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    bool hasPermission;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      hasPermission = await Samsunghealth.hasPermissions([DataType.STEP_COUNT]);
      if (!hasPermission)
        hasPermission = await Samsunghealth.requestPermissions([DataType.STEP_COUNT]);
      if (hasPermission)
        stepCount = await Samsunghealth.read(DataType.STEP_COUNT, dateFrom: truncateTime(DateTime.now()), dateTo: DateTime.now());
    } on PlatformException {
      hasPermission = false;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $stepCount'),
        ),
      ),
    );
  }
}

DateTime truncateTime([DateTime date]) {
  if (date == null) {
    date = DateTime.now();
  }

  return DateTime(date.year, date.month, date.day);
}
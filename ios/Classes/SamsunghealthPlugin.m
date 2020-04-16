#import "SamsunghealthPlugin.h"
#if __has_include(<samsunghealth/samsunghealth-Swift.h>)
#import <samsunghealth/samsunghealth-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "samsunghealth-Swift.h"
#endif

@implementation SamsunghealthPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSamsunghealthPlugin registerWithRegistrar:registrar];
}
@end

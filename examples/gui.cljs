#!/usr/bin/env obb

(.import js/ObjC "Cocoa")

(doto (js/$.NSWindow.alloc.initWithContentRectStyleMaskBackingDefer
       (js/$.NSMakeRect 0 0 200 200)
       (bit-or js/$.NSTitledWindowMask
               js/$.NSClosableWindowMask
               js/$.NSResizableWindowMask
               js/$.NSMiniaturizableWindowMask)
       js/$.NSBackingStoreBuffered
       false)
  (.cascadeTopLeftFromPoint (js/$.NSMakePoint 20 20))
  (.setTitle "Hello, world!")
  (.makeKeyAndOrderFront nil))

(doto js/$.NSApplication.sharedApplication
  (.setActivationPolicy js/$.NSApplicationActivationPolicyRegular)
  (.activateIgnoringOtherApps true))

(.run js/$.NSApp true)

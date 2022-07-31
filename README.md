# obb

[![project chat](https://img.shields.io/badge/slack-join_chat-brightgreen.svg)](https://app.slack.com/client/T03RZGPFR/C02S1220XRV)
![Stability: Experimental](https://img.shields.io/badge/stability-experimental-orange.svg)

Ad-hoc [ClojureScript](https://clojurescript.org/) scripting of Mac applications via Apple's [Open Scripting Architecture](https://developer.apple.com/library/archive/documentation/LanguagesUtilities/Conceptual/MacAutomationScriptingGuide/).

## Status

Experimental.

## Installation

### Homebrew

``` shell
$ brew install babashka/brew/obb
```

### Manual

Download from [Github releases](https://github.com/babashka/obb/releases).

## Usage

Evaluate an expression:

``` shell
$ obb -e '(-> (js/Application "Safari") (.-documents) (aget 0) (.url))'
"https://clojure.org/"
```

``` shell
$ obb -e '(-> (js/Application "Google Chrome") (.-windows) (aget 0) (.activeTab) (.title))'
#js ["GitHub - babashka/obb: Ad-hoc ClojureScript scripting of Mac applications"]
```

Or evaluate a file:

``` shell
$ obb examples/choice.cljs
```

Or make an executable script by using `obb` in a [shebang](https://en.wikipedia.org/wiki/Shebang_(Unix)):

``` clojure
#!/usr/bin/env obb
(-> (js/Application "Safari")
    (.quit))
```

## How does this tool work?

ClojureScript code is evaluated through [SCI](https://github.com/borkdude/sci), the same interpreter that powers [babashka](https://babashka.org/). SCI is compiled to JavaScript which is then by executed by `osascript`.

## Macros

SCI supports macros as first class citizens so you can write a few macros to deal with interop boilerplate:

``` clojure
(defmacro ->clj [obj & interops]
  (let [names (map #(clojure.string/replace (str %) #"[.-]" "") interops)
        ks (mapv keyword names)
        exprs (mapv #(list % obj) interops)]
    `(zipmap ~ks [~@exprs])))

(-> (js/Application "Spotify") (.-currentTrack) (->clj .artist .album .name))
;;=>
{:artist "The Gathering", :album "How to Measure a Planet? (Deluxe Edition)", :name "Travel"}
```

## References

- [Mac Automation Scripting Guide](https://developer.apple.com/library/archive/documentation/LanguagesUtilities/Conceptual/MacAutomationScriptingGuide/GettoKnowScriptEditor.html#//apple_ref/doc/uid/TP40016239-CH5-SW1)
- [JXA Cookbook](https://github.com/JXA-Cookbook/JXA-Cookbook/wiki)
- [Scripting with JXA](https://bru6.de/jxa/)
    - [It illustrates a way to find an App's properties and elements.](https://bru6.de/jxa/basics/working-with-apps/#get-an-apps-properties-and-elements)

## Tips and tricks

### Explore app specific APIs

Open `Script Editor.app`, go to `File` > `Open Dictionary` and select the
application you would like to explore, e.g. `Spotify.app`.  After selection,
select `JavaScript` instead of `AppleScript`.

### Application bundle

Read [here](doc/application-bundle.md) how to create an application bundle from an obb script.

## Sister projects

- [babashka](https://github.com/babashka/babashka): Native, fast starting Clojure interpreter for scripting.
- [nbb](https://github.com/babashka/nbb): Ad-hoc CLJS scripting on Node.js using SCI.
- [scittle](https://github.com/babashka/scittle): The Small Clojure Interpreter exposed for usage in browser script tags.

## Build

[Install Babashka](https://github.com/babashka/babashka/#installation). Then build with:

``` shell
$ bb build
```

Then place `out/obb` anywhere on your path.

## Dev

To develop `obb` you can use the `bb dev` task which starts a shadow-cljs server
and a file watcher.

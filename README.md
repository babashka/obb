# obb

![Stability: Experimental](https://img.shields.io/badge/stability-experimental-orange.svg)

Ad-hoc [ClojureScript](https://clojurescript.org/) scripting of Mac applications via Apple's [Open Scripting Architecture](https://developer.apple.com/library/archive/documentation/LanguagesUtilities/Conceptual/MacAutomationScriptingGuide/).

## Status

Experimental.

## Installation

[Install Babashka](https://github.com/babashka/babashka/#installation) then build with:

``` shell
$ bb build
```

Then place `out/obb` anywhere on your path.

## Usage

Evaluate an expression:

``` shell
$ obb -e '(+ 1 2 3)'
6
```

Or evaluate a file:

``` shell
$ obb examples/choice.cljs
```

## How does this tool work? 

ClojureScript code is evaluated through [SCI](https://github.com/borkdude/sci), the same interpreter that powers [babashka](https://babashka.org/). SCI is compiled to JavaScript which is then by executed by `osascript`.

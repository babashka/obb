# Creating an application bundle

In this document we describe how to get from an obb script, say, `examples/choice.cljs`
(see [examples](../examples/choice.cljs)) to an application bundle `Choice.app`.

## Copy obb to app.js

First, make a copy of `obb` like this:

```
$ cp $(which obb) app.js
```

## Append runner code to app.js

Next, we will modify the global `run` function to run the `choice.cljs` script by appending this to `app.js`:

``` javascript
var app = Application.currentApplication();
app.includeStandardAdditions = true;
const cwd = ObjC.unwrap($(app.pathTo(globalThis).toString()).stringByDeletingLastPathComponent);

globalThis.obbRun = globalThis.run;

globalThis.run=function() {
  var scriptLocation = cwd + "/Choice.app/Contents/Resources/Scripts/choice.cljs";
  obbRun([scriptLocation]);
};
```

You can do this by saving the above in `runner.js` and then appending it to `app.js` with:

```
$ cat runner.js >> app.js
```

## Compile application bundle

Execute the following command:

```
$ osacompile -l JavaScript -o Choice.app app.js
```

This will create `Choice.app` in the current working directory.

## Copy obb script into application bundle

Lastly, copy `choice.cljs` into the application bundle:

```
$ cp examples/choice.cljs Choice.app/Contents/Resources/Scripts
```

## Run!

Now we are ready to launch the application bundle. Launch Finder:

```
$ open .
```

and double click on `Choice.app`. Choose your favorite fruit!

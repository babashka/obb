# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Print all values, including object specifiers, readably.
- Stop printing result of last expression when evaluating a file.

## [0.0.2] - 2022-01-05

### Fixed

- #10: Fix crash when `clojure.core/meta` is called on an object specifier.

## [0.0.1] - 2022-01-03

### Added

- Initial release.

[Unreleased]: https://github.com/babashka/obb/compare/v0.0.2...HEAD
[0.0.2]: https://github.com/babashka/obb/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/babashka/obb/releases/tag/v0.0.1

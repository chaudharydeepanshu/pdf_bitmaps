[![pub package](https://img.shields.io/pub/v/pdf_bitmaps.svg)](https://pub.dev/packages/pdf_bitmaps) [![wakatime](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/a86db259-be86-4dbb-a4bc-d503f45a356f.svg)](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/a86db259-be86-4dbb-a4bc-d503f45a356f)

## Package description

A pdf pages bitmaps genrator and page counter which uses Android PdfRenderer.

Note: This package currently supports only Android native URIs of files not absolute file paths and to get the Android native URIs of files you can use [pick_or_save](https://pub.dev/packages/pick_or_save) plugin. Why this behaviour? Well because I needed a plugin which can give me pdf bitmaps using a URI so that I can avoid copying the whole pdf into device cache just to generate bitmaps like other plugins do.

## Features

- Generates bitmaps on the fly using Android native URI.
- Gets pdf page count using Android native URI.

## Getting started

- In pubspec.yaml, add this dependency:

```yaml
pdf_bitmaps: 
```

- Add this package to your project:

```dart
import 'package:pdf_bitmaps/pdf_bitmaps.dart';
```

## Basic Usage

### Getting the page count

```dart
int? pageCount = await PdfBitmaps().pdfPageCount(pdfUri: pdfUri)
```

### Getting a pdf page bitmap

```dart
Uint8List? bytes = await PdfBitmaps().pdfBitmap(params: PDFBitmapParams(pdfUri: pdfUri, pageIndex: 2, quality: 3));
```
Note: ```pageIndex``` starts from 0 to (PdfPageCount - 1) and ```quality``` can be from 1 to 100.

| See Example  |
| ------------- |
| ![Reference_to_S_from_DC_28-09-2022](https://user-images.githubusercontent.com/85361211/193132735-3d9c4dba-6b12-4ce2-b2a9-4cd2e98d4f8e.gif) | 

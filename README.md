[![pub package](https://img.shields.io/pub/v/pdf_bitmaps.svg)](https://pub.dev/packages/pdf_bitmaps) [![wakatime](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/a86db259-be86-4dbb-a4bc-d503f45a356f.svg)](https://wakatime.com/badge/user/83f3b15d-49de-4c01-b8de-bbc132f11be1/project/a86db259-be86-4dbb-a4bc-d503f45a356f)

## Word from creator

**Hello👋, This package is completely compatible with flutter and it also supports using Android Uri of picked file to work with which offer some real benefits such as getting pdf any page bitmap without first caching that pdf or validating them without caching.**

**Yes, without a doubt, giving a free 👍 or ⭐ will encourage me to keep working on this plugin.**

## Package description

A flutter PDF pages images generator which also helps with few other common pdf related things.

## Features

- Works on Android 5.0 (API level 21) or later.
- Generate images of pdf pages using absolute file path or Android native Uri.
- Ability to generate images of pdf pages with different background color, scale (size/quality), and rotation angle.
- Check if PDF is valid or not.
- Check if PDF is protected or not.
- Get PDF total number of pages count.
- Get any PDF page width and height.

**Note:** If you are getting errors in you IDE after updating this plugin to newer version and the error contains works like Redeclaration, Conflicting declarations, Overload resolution ambiguity then to fix that you probably need to remove the older version of plugin from pub cache `C:\Users\username\AppData\Local\Pub\Cache\hosted\pub.dev\older_version` or simply run `flutter clean`.

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

### Getting the PDF total page count

```dart
int? pageCount = await PdfBitmaps().pdfPageCount(
  params: PDFPageCountParams(pdfPath: pdfPath),
);
```

### Getting a PDF page image data

```dart
String? imageCachedPath = await PdfBitmaps().pdfBitmap(
  params: PDFBitmapParams(
    pdfPath: pdfPath,
    pageInfo: BitmapConfigForPage(
        pageNumber: 1,
        rotationAngle: 90,
        scale: 0.5,
        backgroundColor: Colors.red,
    ),
    pdfRendererType: PdfRendererType.androidPdfRenderer,
  ),
);

Uint8List? bytes = File(imageCachedPath!).readAsBytesSync();
```
**Note:**
- `scale` should be greater than 0 and less than or equal to 5. By default it is 1.
- `backgroundColor` is Colors.white by default.
- `rotationAngle` is 0 by default.
- `pdfRendererType` is PdfRendererType.androidPdfRenderer by default as its fast but PdfRendererType.pdfBoxPdfRenderer is recommended for PDF with very high resolution images as PdfRendererType.androidPdfRenderer may create a white useless image.

### Getting the PDF validity and protection info

```dart
PdfValidityAndProtection? pdfValidityAndProtectionInfo = await PdfBitmaps().pdfValidityAndProtection(
  params: PDFValidityAndProtectionParams(pdfPath: pdfPath),
);

bool? isPDFValid = pdfValidityAndProtectionInfo?.isPDFValid;
bool? isPDFProtected = pdfValidityAndProtectionInfo?.isOpenPasswordProtected;
```
**Note:**
- A PDF would still be valid even if it is protected.
- A PDF is protected if the PDF has an open/user password set.

### Getting a PDF page size info

```dart
PageSizeInfo? pageSizeInfo = await PdfBitmaps().pdfPageSize(
  params: PDFPageSizeParams(pdfPath: pdfPath, pageNumber: pageNumber),
);

int? widthOfPage = pageSizeInfo?.widthOfPage;
int? heightOfPage = pageSizeInfo?.heightOfPage;
```

**Note:** To try the demos shown in below gifs run the example included in this plugin.

| Loading PDF pages in Gridview | PDF validity and protection info | PDF page size info |
| :----: | :---: | :---: |
| <img src="https://user-images.githubusercontent.com/85361211/201478119-61661f00-789a-4372-a01e-1bbf2496953c.gif"></img> | <img src="https://user-images.githubusercontent.com/85361211/201478141-662d5da1-9d76-4230-bd1c-b90d9c995d6b.gif"></img> | <img src="https://user-images.githubusercontent.com/85361211/201478304-02bdcfc2-9d32-4980-98b0-7c68535328f5.gif"></img> |

## 1.0.1

* Fixes issue of unnecessarily creating copy of PDF when using android renderer.(Due to changes made in 1.0.0)
* Fixes issue of not deleting PDF copy which was created when using PDFBox renderer.(Due to changes made in 1.0.0)

## 1.0.0

* BREAKING: `pdfBitmap()` now provides a cached file path of the rendered image instead of Uint8List.
  
  For migration: Replace the `pdfBitmap()` result type to String? and then convert path to Uint8List using File(path).readAsBytesSync().

* BREAKING: `pdfBitmaps()` now provides a list cached files paths of the rendered images instead of list of Uint8List.

  For migration: Replace the `pdfBitmaps()` result type to List<String>? and then convert each path to Uint8List using File(path).readAsBytesSync().

* Added `pdfRendererType` to `pdfBitmap()` and `pdfBitmaps()` for choosing different renderers of choice and benefits.

* Added new type of renderer based on amazing library PDFBox android port [PdfBox-Android](https://github.com/TomRoush/PdfBox-Android) by [TomRoush](https://github.com/TomRoush).

## 0.3.3

* Updated documentation.

## 0.3.1

* Updated documentation.
* Fixed issue with rendering PDF with names containing colon `:`.

## 0.2.5

* Fixed rendering issue for pdfs with rotated pages (Verified on Android 7.0 and more info at more info at: https://stackoverflow.com/a/41421216).

## 0.2.4

* Added `pdfValidityAndProtection` method for getting pdf validity and protection info.

## 0.2.3

* Fixed 0.2.2 changelog.

## 0.2.2

* Added `pdfPagesSize` method for getting size info of a page of pdf.
* BREAKING: `PageInfo` replaced with `PDFBitmapParams`.

## 0.2.1

* BREAKING: Removed `scale`, `backgroundColor`, `pageIndex`, `pagesIndexes`.
* Added `pageInfo` to be used like `params: PDFBitmapParams(pdfPath: pdfPath, pageInfo: PageInfo(pageNumber: 5, rotationAngle: 153, scale: 1.6, backgroundColor: Colors.red))`.
* Added `pagesInfo` to be used like `params: PDFBitmapsParams(pdfPath: pdfPath, pagesInfo: [PageInfo(pageNumber: 5, rotationAngle: 153, scale: 1.6, backgroundColor: Colors.red)])`.
* Updated example and readme.

## 0.1.2

* Fixes issues due to calling page count while generating bitmaps.

## 0.1.1

* BREAKING: `quality` is replaced with `scale` and scale takes a double value greater than 0 & less than and equal to 5 (Also fixes the plugin performance issue).
* BREAKING: `pdfUri` is replaced with `pdfPath` as now `pdfPath` is capable of taking care both URI path and absolute file path.
* Added new method `pdfBitmaps()` which takes a list of page indexes and provides a list of Bytes as result.
* Added new option `backgroundColor` which takes color to apply on the background of the pdf bitmap.
* Added new method `cancelBitmaps()` which allows cancelling running bitmaps tasks.

## 0.0.2

* Added support to get bitmaps and page count for both uri and absolute file path.

## 0.0.1

* Initial release.

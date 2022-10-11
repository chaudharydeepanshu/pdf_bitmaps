import 'dart:typed_data';

import 'package:pdf_bitmaps/src/pdf_bitmaps_method_channel.dart';
import 'package:pdf_bitmaps/src/pdf_bitmaps_platform_interface.dart';

class PdfBitmaps {
  /// Get the page count for a pdf file.
  ///
  /// Returns the page count of a pdf file or null if operation was cancelled.
  /// Throws exception on error.
  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfPageCount(params: params);
  }

  /// Get the specific page bitmap for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfBitmap(params: params);
  }

  /// Get the specific page bitmap for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<List<Uint8List>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfBitmaps(params: params);
  }

  /// Cancels running bitmaps operation.
  ///
  /// Returns the cancelling message.
  Future<String?> cancelBitmaps() {
    return PdfBitmapsPlatform.instance.cancelBitmaps();
  }
}
